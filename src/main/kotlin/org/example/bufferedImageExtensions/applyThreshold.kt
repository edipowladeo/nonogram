package org.example.bufferedImageExtensions

import java.awt.image.BufferedImage


fun BufferedImage.applyNegativeThreshold(threshold: Int): BufferedImage {
    val width = this.width
    val height = this.height

    // Create a new BufferedImage to hold the result
    val newImage = BufferedImage(width, height, this.type)

    // Iterate over each pixel in the image
    for (x in 0 until width) {
        for (y in 0 until height) {
            // Get the color of the pixel
            val pixelColor = this.getRGB(x, y)

            // Extract the RGB components (ignoring alpha)
            val red = (pixelColor shr 16) and 0xFF
            val green = (pixelColor shr 8) and 0xFF
            val blue = pixelColor and 0xFF

            // Calculate the grayscale value of the pixel
            val grayscale = (red + green + blue) / 3

            // Apply thresholding: if grayscale >= threshold, set to white, else black
            val newColor = if (grayscale >= threshold) {
                0x000000 // White (RGB = 255, 255, 255)
            } else {
                0xFFFFFF // Black (RGB = 0, 0, 0)
            }

            // Set the new pixel color in the result image
            newImage.setRGB(x, y, newColor)
        }
    }

    return newImage
}

fun BufferedImage.toBlackAndWhite(brightnessThresold: Int): BufferedImage {
    val width = this.width
    val height = this.height

    // Create a new BufferedImage to hold the result
    val newImage = BufferedImage(width, height, this.type)

    // Iterate over each pixel in the image
    for (x in 0 until width) {
        for (y in 0 until height) {
            // Get the color of the pixel
            val pixelColor = this.getRGB(x, y)

            // Extract the RGB components (ignoring alpha)
            val red = (pixelColor shr 16) and 0xFF
            val green = (pixelColor shr 8) and 0xFF
            val blue = pixelColor and 0xFF

            // Calculate the grayscale value of the pixel
            val grayscale = (red + green + blue) / 3

            // Apply thresholding: if grayscale >= threshold, set to white, else black
            val newColor = if (grayscale >= brightnessThresold) {
                0xFFFFFF // White (RGB = 255, 255, 255)
            } else {
                0x000000 // Black (RGB = 0, 0, 0)
            }

            // Set the new pixel color in the result image
            newImage.setRGB(x, y, newColor)
        }
    }

    return newImage
}
