package org.example.bufferedImageExtensions

import java.awt.Image
import java.awt.image.BufferedImage

fun BufferedImage.resize(scaleFactor: Double): BufferedImage =
    resize(xScaleFactor = scaleFactor, yScaleFactor = scaleFactor)

fun BufferedImage.resize(xScaleFactor: Double = 1.0, yScaleFactor: Double = 1.0): BufferedImage {
    val newWidth = (this.width * xScaleFactor).toInt()
    val newHeight = (this.height * yScaleFactor).toInt()

    // Use getScaledInstance to resize the image
    val resizedImage: Image = this.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH)

    // Convert the resized image to BufferedImage
    val bufferedResizedImage = BufferedImage(newWidth, newHeight, this.type)
    val g2d = bufferedResizedImage.createGraphics()
    g2d.drawImage(resizedImage, 0, 0, null)
    g2d.dispose()

    return bufferedResizedImage
}

fun BufferedImage.resize(height: Int = 0, width: Int = 0): BufferedImage {
    // If height or width is zero, keep the original dimension
    val newWidth = if (width == 0) this.width else width
    val newHeight = if (height == 0) this.height else height

    // Use getScaledInstance to resize the image
    val resizedImage: Image = this.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH)

    // Convert the resized image to BufferedImage
    val bufferedResizedImage = BufferedImage(newWidth, newHeight, this.type)
    val g2d = bufferedResizedImage.createGraphics()
    g2d.drawImage(resizedImage, 0, 0, null)
    g2d.dispose()

    return bufferedResizedImage
}
