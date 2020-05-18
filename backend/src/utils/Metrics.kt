package org.moevm.bsc_ilyashuk.utils

fun calculateVolume(predictions: ArrayList<FloatArray>): Pair<Float, Array<Float>> {
    var totalVolume = 0f
    val volumes = Array(5) { 0f }

    predictions.forEach { prediction ->
        prediction.forEachIndexed { index, emotionProbability ->
            totalVolume += emotionProbability
            volumes[index] += emotionProbability
        }
    }
    return Pair(totalVolume, volumes)
}