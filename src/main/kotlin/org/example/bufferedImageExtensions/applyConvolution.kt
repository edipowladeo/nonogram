package org.example.bufferedImageExtensions

import java.awt.image.BufferedImage
import kotlin.math.min

fun BufferedImage.applyConvolution(kernel: Array<IntArray>): BufferedImage {
    val threshold = 125
    val width = this.width
    val height = this.height
    val resultImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

    val kernelWidth = kernel[0].size
    val kernelHeight = kernel.size
    val kernelCenterX = kernelWidth / 2
    val kernelCenterY = kernelHeight / 2

    for (y in kernelCenterY until height - kernelCenterY) {
        for (x in kernelCenterX until width - kernelCenterX) {
            var sum = 0

            for (ky in -kernelCenterY..kernelCenterY) {
                for (kx in -kernelCenterX..kernelCenterX) {
                    val pixelColor = this.getRGB(x + kx, y + ky) and 0xFF
                    sum += pixelColor * kernel[ky + kernelCenterY][kx + kernelCenterX]
                }
            }

            // Ensure that the result is within the valid RGB range
            val clampedSum = sum.coerceIn(0, 255)
            val color = if (clampedSum > threshold) 0xFFFFFF else 0x000000

            resultImage.setRGB(min(x+kernelWidth,width-1), min(y+kernelHeight,height-1), color)
        }
    }

    return resultImage
}