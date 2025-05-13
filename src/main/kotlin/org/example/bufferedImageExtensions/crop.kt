package org.example.bufferedImageExtensions

import java.awt.image.BufferedImage

fun BufferedImage.crop(
    x1: Int, x2: Int,
    y1: Int, y2: Int
): BufferedImage {
    val left = minOf(x1, x2).coerceIn(0, width - 1)
    val right = maxOf(x1, x2).coerceIn(0, width)
    val top = minOf(y1, y2).coerceIn(0, height - 1)
    val bottom = maxOf(y1, y2).coerceIn(0, height)

    val width = right - left
    val height = bottom - top

    require(width > 0 && height > 0) { "Crop area must be at least 1x1 pixels" }

    return getSubimage(left, top, width, height)
}