package org.example.arithmetic

data class Bar(
    val center: Double,
    val width: Double
) {
    val start = center - width / 2
    val end = center + width / 2

    companion object {
        fun fromStartEnd(start: Double, end: Double): Bar {
            val center = (start + end) / 2
            val width = end - start
            return Bar(center, width)
        }
    }
}

