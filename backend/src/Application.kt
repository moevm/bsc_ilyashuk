package org.moevm.bsc_ilyashuk

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import org.tensorflow.SavedModelBundle

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {}
    }
    val model = SavedModelBundle.load("saved_model", "serve")

    routing {
        get("/") {
            val session = model.session()

            model.graph().operations()

            println(session.runner().runAndFetchMetadata().outputs)


            call.respondText("hello", contentType = ContentType.Text.Plain)
        }

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}
