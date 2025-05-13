package org.example.bufferedImageExtensions

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.abs

fun BufferedImage.compareWithColor(targetColor: Color, tolerance: Int = 5): BufferedImage {
    val width = this.width
    val height = this.height
    val output = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    for (x in 0 until width) {
        for (y in 0 until height) {
            val rgb = Color(this.getRGB(x, y))

            val rDiff = abs(rgb.red - targetColor.red)
            val gDiff = abs(rgb.green - targetColor.green)
            val bDiff = abs(rgb.blue - targetColor.blue)

            val isNear = rDiff <= tolerance && gDiff <= tolerance && bDiff <= tolerance

            val outputColor = if (isNear) Color.BLACK.rgb else Color.WHITE.rgb
            output.setRGB(x, y, outputColor)
        }
    }

    return output
}