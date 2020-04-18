package org.moevm.bsc_ilyashuk

import io.ktor.application.*
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import org.moevm.bsc_ilyashuk.models.Features
import org.tensorflow.SavedModelBundle
import org.tensorflow.Tensor
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

    val labels = arrayOf(
        "female_angry",
        "female_calm",
        "female_fearful",
        "female_happy",
        "female_sad",
        "male_angry",
        "male_calm",
        "male_fearful",
        "male_happy",
        "male_sad"
    )

    routing {
        get("/") {
            call.respondText("bsc_ilyashuk", contentType = ContentType.Text.Plain)
        }

        post("/predict") {
            val file = call.getFile()

            val client = HttpClient() {
                install(JsonFeature)
            }

            val features = client.get<Features>("http://localhost:5000?filename=${file.name}")

            val predictions = ArrayList<FloatBuffer>()

            for (i in 0 until features.data.size / 216) {
                val session = model.session()
                val runner = session.runner()

                val input = Tensor.create(
                    arrayOf<Long>(1, 216, 1).toLongArray(),
                    FloatBuffer.wrap(features.data.slice((i * 216) until (((i + 1) * 216))).toFloatArray())
                )

                runner.apply {
                    feed("input_input", input)
                    fetch("output/Softmax")
                }
                val outputTensor = runner.run()[0]
                FloatBuffer.allocate(10).apply {
                    outputTensor.writeTo(this)
                    predictions.add(this)
                }
            }
            call.respond(predictions.map { it.array() })
        }
    }
}

