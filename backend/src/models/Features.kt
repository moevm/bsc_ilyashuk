package org.moevm.bsc_ilyashuk.models

data class Features (val chunks: Array<Array<Float>>, val duration: Float) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Features

        if (!chunks.contentDeepEquals(other.chunks)) return false
        if (duration != other.duration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chunks.contentDeepHashCode()
        result = 31 * result + duration.hashCode()
        return result
    }
}