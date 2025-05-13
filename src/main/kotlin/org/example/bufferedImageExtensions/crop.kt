package org.example.bufferedImageExtensions

import java.awt.image.BufferedImage

fun BufferedImage.cropXY(
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

fun BufferedImage.cropHW(
    left: Int, width: Int,
    top: Int, height: Int
): BufferedImage {
    val l = left.coerceIn(0, this.width - 1)
    val t = top.coerceIn(0, this.height - 1)
    val w = width.coerceIn(0, this.width - left)
    val h = height.coerceIn(0, this.height - top)

    require(w > 0 && h > 0) { "Crop area must be at least 1x1 pixels" }

    return getSubimage(l,t,w,h)

}