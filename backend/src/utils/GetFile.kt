package org.moevm.bsc_ilyashuk.utils

import io.ktor.application.ApplicationCall
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.request.receiveMultipart
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.*

suspend fun InputStream.copyToSuspend(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    yieldSize: Int = 4 * 1024 * 1024,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): Long {
    return withContext(dispatcher) {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}

suspend fun ApplicationCall.getFile() : File {
    val dir = File("./uploads/")
    if (!dir.exists()) {
        dir.mkdir()
    }

    val multipart = receiveMultipart()
    var file: File? = null
    multipart.forEachPart { part ->
        if (part is PartData.FileItem) {
            val ext = File(part.originalFileName!!).extension
            file = File(
                "./uploads",
                "${part.originalFileName!!}-${System.currentTimeMillis()}.$ext"
            )

            part.streamProvider().use { input ->
                file!!.outputStream().buffered().use { output -> input.copyToSuspend(output) }
            }
        }
        part.dispose()
    }
    return file!!
}