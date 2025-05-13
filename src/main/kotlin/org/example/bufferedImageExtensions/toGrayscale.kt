package org.example.bufferedImageExtensions

import java.awt.image.BufferedImage


fun BufferedImage.toGrayscale(): BufferedImage {
    val width = this.width
    val height = this.height
    val grayImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val rgb = this.getRGB(x, y)
            val gray = (0.3 * ((rgb shr 16) and 0xFF) + 0.59 * ((rgb shr 8) and 0xFF) + 0.11 * (rgb and 0xFF)).toInt()
            val newRGB = (gray shl 16) or (gray shl 8) or gray
            grayImage.setRGB(x, y, newRGB)
        }
    }
    return grayImage
}
