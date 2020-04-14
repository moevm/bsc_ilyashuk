package org.moevm.bsc_ilyashuk

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.request.isMultipart
import io.ktor.request.receiveMultipart
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.tensorflow.SavedModelBundle
import org.tensorflow.Tensor
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.FloatBuffer

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CORS) {
        anyHost()
    }
    install(ContentNegotiation) {
        gson {}
    }
    val model = SavedModelBundle.load("saved_model", "serve")

    routing {
        get("/") {
            val session = model.session()
            val runner = session.runner()

            val input = Tensor.create(arrayOf<Long>(1, 216, 1).toLongArray(), FloatBuffer.allocate(216))

            runner.apply {
                feed("input_input", input)
                fetch("output/Softmax")
            }

            val outputTensor = runner.run()[0]

            val result = FloatBuffer.allocate(10)

            outputTensor.writeTo(result)

            val resultArray = Array(10) {result[it]}

            call.respondText(resultArray.joinToString(), contentType = ContentType.Text.Plain)
        }

        post("/upload") {
            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val ext = File(part.originalFileName!!).extension
                    val file = File(
                        "./uploads",
                        "upload-${System.currentTimeMillis()}.$ext"
                    )
                    part.streamProvider().use { input ->
                        file.outputStream().buffered().use { output -> input.copyToSuspend(output) }
                    }
                    println(file.readLines()[0])
                }
                part.dispose()
            }
            call.respondText("success", contentType = ContentType.Text.Plain)
        }

        get("/json") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

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
