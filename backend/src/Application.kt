package org.moevm.bsc_ilyashuk

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {}
    }

    routing {
        get("/") {

            val model = KerasModelImport.importKerasSequentialModelAndWeights("./model.json", "./model.h5")
            call.respondText("memes", contentType = ContentType.Text.Plain)
        }

        

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

//    val client = HttpClient() {
//        install(JsonFeature) {
//            serializer = GsonSerializer()
//        }
//    }
//    runBlocking {
//        // Sample for making a HTTP Client request
//
//        val message = client.post<JsonSampleClass> {
//            url("http://127.0.0.1:8080/path/to/endpoint")
//            contentType(ContentType.Application.Json)
//            body = JsonSampleClass(hello = "world")
//        }
//    }

//data class JsonSampleClass(val hello: String)

