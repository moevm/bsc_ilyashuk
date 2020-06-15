package org.moevm.bsc_ilyashuk.utils

import org.moevm.bsc_ilyashuk.config.numOfEmotions
import kotlin.math.min

fun calculateVolume(
    predictions: ArrayList<FloatArray>,
    duration: Float,
    chunkLength: Float
): Pair<Float, Array<Float>> {
    val volumes = Array(numOfEmotions) { 0f }

    predictions.forEach { prediction ->
        prediction.forEachIndexed { index, emotionProbability ->
            volumes[index] += min(chunkLength, duration % chunkLength) * emotionProbability
        }
    }

    val totalVolume = volumes.reduce { acc, volume ->
        acc.plus(volume)
    }
    return Pair(totalVolume, volumes)
}