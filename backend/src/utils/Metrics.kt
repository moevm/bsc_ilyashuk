package org.moevm.bsc_ilyashuk.utils

import org.moevm.bsc_ilyashuk.config.chunkLength
import org.moevm.bsc_ilyashuk.config.numOfEmotions

fun calculateVolume(predictions: ArrayList<FloatArray>): Pair<Float, Array<Float>> {
    val volumes = Array(numOfEmotions) { 0f }

    predictions.forEach { prediction ->
        prediction.forEachIndexed { index, emotionProbability ->
            volumes[index] += chunkLength * emotionProbability
        }
    }

    val totalVolume = volumes.reduce { acc, volume ->
        acc.plus(volume)
    }
    return Pair(totalVolume, volumes)
}