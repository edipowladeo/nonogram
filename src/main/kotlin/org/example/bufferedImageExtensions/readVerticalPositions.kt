package org.example.bufferedImageExtensions

import org.example.arithmetic.removeOutliersFromArithmeticProgression
import java.awt.image.BufferedImage

data class Bar(
    val center: Double,
    val width: Double
)

fun BufferedImage.readBarPositions(toleranceRatio: Double = 0.2): Array<Bar> {
    val width = this.width
    val height = this.height

    // Check if the image is taller than 1 pixel
    if (height > 1) {
        // throw IllegalArgumentException("Image height should be 1 pixel. This image has height: $height")
    }
    val y = 0

    // List to store the center points of the bars
    val bars = ArrayList<Bar>()


    var inBar = false
    var barStart = -1.0

    // Iterate through each column in the row
    for (x in 0 until width) {
        val pixelColor = this.getRGB(x, y)
        // Check if the pixel is black (0x000000)
        if ((pixelColor and 0x00FFFFFF) == 0x000000) {
            if (!inBar) {
                // Bar started, record the starting position
                barStart = x.toDouble()
                inBar = true
            }
        } else {
            if (inBar) {
                // Bar ended, calculate and store the center
                bars.add(
                    Bar(
                        center = (barStart + x - 1) / 2,
                        width = x - barStart.toInt().toDouble()
                    )
                )
                inBar = false
            }
        }
    }

    // If the row ends with a bar (in case the last pixel is black)
    if (inBar) {
        val barCenter = (barStart + (width - 1)) / 2
        bars.add(
            Bar(
                center = barCenter,
                width = (width - barStart)
            )
        )
    }

    // Convert the list to an IntArray and return
    return bars.toTypedArray()
}
