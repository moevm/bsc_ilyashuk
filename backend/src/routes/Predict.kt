package org.moevm.bsc_ilyashuk.routes

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.Route
import org.moevm.bsc_ilyashuk.Predict
import org.moevm.bsc_ilyashuk.config.numOfEmotions
import org.moevm.bsc_ilyashuk.utils.calculateVolume
import org.moevm.bsc_ilyashuk.utils.getFeaturesFromFile
import org.moevm.bsc_ilyashuk.utils.getCallData
import org.tensorflow.SavedModelBundle
import org.tensorflow.Tensor
import java.nio.FloatBuffer
import kotlin.math.min

fun Route.predict(model: SavedModelBundle) {
    post<Predict> {
        val callData = call.getCallData()

        val file = callData.first
        val chunkLength = callData.second

        try {
            val features = getFeaturesFromFile(file.name, chunkLength)

            val predictions = ArrayList<FloatArray>()

            for (chunk in features.chunks) {
                val session = model.session()
                val runner = session.runner()

                val input = Tensor.create(
                    arrayOf<Long>(1, 40, 1).toLongArray(),
                    FloatBuffer.wrap(chunk.toFloatArray())
                )

                runner.apply {
                    feed("input_input", input)
                    fetch("output/Softmax")
                }
                val outputTensor = runner.run()[0]
                FloatBuffer.allocate(numOfEmotions).apply {
                    outputTensor.writeTo(this)
                    val temp = FloatArray(numOfEmotions) { 0f }
                    array().forEachIndexed { index, value ->
                        temp[index] = value
                    }
                    predictions.add(temp)
                }
            }

            val volume = calculateVolume(predictions, features.duration, chunkLength)

            val predictionsWithTime =
                predictions.mapIndexed { index, prediction ->
                    mapOf(
                        "timeFrom" to index * chunkLength,
                        "timeTo" to min((index + 1) * chunkLength, features.duration),
                        "prediction" to prediction
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