package org.example.bufferedImageExtensions

import java.awt.image.BufferedImage
import kotlin.math.roundToInt

fun drawVerticalBars(
    bars: Array<Bar>,
    imageWidth: Int,
    color: Int = 0x79000000, //todo fix issue where i cant use more than 79 for alpha
    barWidth: Double = 5.0
): BufferedImage {
    // Create a new BufferedImage with a height of 1 and width equal to the number of bars
    val newImage = BufferedImage(imageWidth, 1, BufferedImage.TYPE_INT_ARGB)

    for (x in 0 until imageWidth) {
        newImage.setRGB(x, 0, 0x00000000)  // Set all pixels to white
    }

    // For each bar center, set the pixel in the 1xN image to black (0x000000)
    for (i in bars.indices) {
        val barCenter = bars[i].center.toInt()
        val halfWidth = barWidth / 2

        val startX = (barCenter - halfWidth).roundToInt().coerceAtLeast(0)
        val endX = (barCenter + halfWidth).roundToInt().coerceAtMost(imageWidth - 1)

        for (x in startX..endX) {
            newImage.setRGB(x, 0, color)
        }
    }

    // Return the new image
    return newImage
}

//todo remove
fun drawVerticalBarsThin(bars: Array<Bar>, imageWidth: Int, color: Int = 0x00000000): BufferedImage {
    // Create a new BufferedImage with a height of 1 and width equal to the number of bars
    val newImage = BufferedImage(imageWidth, 1, BufferedImage.TYPE_INT_ARGB)

    for (x in 0 until imageWidth) {
        newImage.setRGB(x, 0, 0x00000000)  // Set all pixels to white
    }

    // For each bar center, set the pixel in the 1xN image to black (0x000000)
    for (i in bars.indices) {
        val barCenter = bars[i].center

        // Check if the bar center is within the image height range (1-pixel tall image)
        if (barCenter.toInt() in 0 until imageWidth) {
            newImage.setRGB(barCenter.toInt(), 0, color)  // Set black pixel at the corresponding position
        }
    }

    // Return the new image
    return newImage
}