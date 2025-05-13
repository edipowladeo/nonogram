package org.example.bufferedImageExtensions

import java.awt.image.BufferedImage

fun BufferedImage.countBlackPixels(): Int {
    var count = 0
    val width = this.width
    val height = this.height

    for (y in 0 until height) {
        for (x in 0 until width) {
            // Check if the pixel is black (0x000000)
            if ((this.getRGB(x, y) and 0x00FFFFFF) == 0x000000) {
                count++
            }
        }
    }
    return count
}