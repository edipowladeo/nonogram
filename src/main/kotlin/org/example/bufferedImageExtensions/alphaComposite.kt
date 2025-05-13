package org.example.bufferedImageExtensions

import java.awt.AlphaComposite
import java.awt.image.BufferedImage

fun BufferedImage.alphaComposite(top: BufferedImage): BufferedImage {
    val result = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val g = result.createGraphics()

    // Draw the bottom image (this)
    g.drawImage(this, 0, 0, null)

    // Set alpha composite (source-over is default, just like Photoshop Normal mode)
    g.composite = AlphaComposite.SrcOver

    // Draw the top image on top
    g.drawImage(top, 0, 0, null)

    g.dispose()
    return result
}

fun BufferedImage.compositeCustomImpl(top: BufferedImage): BufferedImage {
    val width = this.width
    val height = this.height
    val result = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

    for (x in 0 until width) {
        for (y in 0 until height) {
            val rgbTop = top.getRGB(x, y)
            val rgbBottom = this.getRGB(x, y)

            val aTop = (rgbTop shr 24) and 0xFF
            val rTop = (rgbTop shr 16) and 0xFF
            val gTop = (rgbTop shr 8) and 0xFF
            val bTop = rgbTop and 0xFF

            val aBottom = (rgbBottom shr 24) and 0xFF
            val rBottom = (rgbBottom shr 16) and 0xFF
            val gBottom = (rgbBottom shr 8) and 0xFF
            val bBottom = rgbBottom and 0xFF

            val aTopF = aTop / 255.0
            val aBottomF = aBottom / 255.0 * (1 - aTopF)

            val aOutF = aTopF + aBottomF
            val aOut = (aOutF * 255).toInt().coerceIn(0, 255)

            val rOut = ((rTop * aTopF + rBottom * aBottomF) / aOutF).toInt().coerceIn(0, 255)
            val gOut = ((gTop * aTopF + gBottom * aBottomF) / aOutF).toInt().coerceIn(0, 255)
            val bOut = ((bTop * aTopF + bBottom * aBottomF) / aOutF).toInt().coerceIn(0, 255)

            val finalRgb = (aOut shl 24) or (rOut shl 16) or (gOut shl 8) or bOut
            result.setRGB(x, y, finalRgb)
        }
    }

    return result
}