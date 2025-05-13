package org.example.bufferedImageExtensions

import java.awt.image.BufferedImage


fun BufferedImage.toVerticalBars(): BufferedImage = this.reduceVerticallyAverage().resize(height = this.height)

fun BufferedImage.reduceVerticallyAverage(): BufferedImage { // todo create type to accept only BW images
    val width = this.width
    val height = this.height

    // Create a new BufferedImage with a height of 1 to store the result
    val newImage = BufferedImage(width, 1, BufferedImage.TYPE_INT_RGB)

    // For each column, calculate the percentage of black pixels and set the grayscale value
    for (x in 0 until width) {
        var blackPixelCount = 0

        // Count the black pixels in the current column
        for (y in 0 until height) {
            val pixelColor = this.getRGB(x, y)
            // If the pixel is black (0x000000), increment the count
            if ((pixelColor and 0x00FFFFFF) == 0x000000) {
                blackPixelCount++
            }
        }

        // Calculate the percentage of black pixels in this column
        val blackPixelPercentage = (blackPixelCount.toDouble() / height.toDouble()) * 100
        val grayscaleValue = 255 - (255 * (blackPixelPercentage / 100)).toInt()

        // Set the grayscale value to the single row of the new image (1-pixel tall)
        val newColor = (grayscaleValue shl 16) or (grayscaleValue shl 8) or grayscaleValue
        newImage.setRGB(x, 0, newColor)  // Set the value at the single row (y = 0)
    }


    return newImage
}
