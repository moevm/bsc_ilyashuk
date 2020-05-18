package org.moevm.bsc_ilyashuk.routes

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.Route
import org.moevm.bsc_ilyashuk.Predict
import org.moevm.bsc_ilyashuk.config.fragmentLength
import org.moevm.bsc_ilyashuk.utils.calculateVolume
import org.moevm.bsc_ilyashuk.utils.getFeaturesFromFile
import org.moevm.bsc_ilyashuk.utils.getFile
import org.tensorflow.SavedModelBundle
import org.tensorflow.Tensor
import java.nio.FloatBuffer

fun Route.predict(model: SavedModelBundle) {
    post<Predict> {
        val file = call.getFile()
        try {
            val features = getFeaturesFromFile(file.name)

            val predictions = ArrayList<FloatArray>()

            for (i in 0 until features.data.size / 216) {
                val session = model.session()
                val runner = session.runner()

                val input = Tensor.create(
                    arrayOf<Long>(1, 216, 1).toLongArray(),
                    FloatBuffer.wrap(features.data.slice((i * 216) until ((i + 1) * 216)).toFloatArray())
                )

                runner.apply {
                    feed("input_input", input)
                    fetch("output/Softmax")
                }
                val outputTensor = runner.run()[0]
                FloatBuffer.allocate(10).apply {
                    outputTensor.writeTo(this)
                    val temp = FloatArray(5) { 0f }
                    array().forEachIndexed { index, value ->
                        temp[index % 5] += value
                    }
                    predictions.add(temp)
                }
            }

            val volume = calculateVolume(predictions)

            val predictionsWithTime =
                predictions.mapIndexed { index, pred ->
                    mapOf(
                        "time" to index * fragmentLength,
                        "pred" to pred
                    )
                }


            call.respond(
                mapOf(
                    "predictions" to predictionsWithTime,
                    "metrics" to mapOf(
                        "totalVolume" to volume.first,
                        "volumes" to volume.second
                    )
                )
            )

        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error")
            throw(e)
        } finally {
            file.delete()
        }
    }
}