package org.example.bufferedImageExtensions

import java.awt.image.BufferedImage

fun BufferedImage.transpose(): BufferedImage {
    val width = this.width
    val height = this.height

    // Create a new BufferedImage with swapped width and height
    val transposedImage = BufferedImage(height, width, this.type)

    // Loop through each pixel of the original image
    for (x in 0 until width) {
        for (y in 0 until height) {
            // Get the pixel color from the original image
            val pixelColor = this.getRGB(x, y)

            // Set the transposed pixel color in the new image
            transposedImage.setRGB(y, x, pixelColor)
        }
    }

    return transposedImage
}
