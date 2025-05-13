package org.example

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.abs
import kotlin.math.roundToInt

data class ImageWithCenterOfMass(
    val image: BufferedImage, //create type for BW image
    val centerOfMassX: Double,
    val centerOfMassY: Double
) : Comparable<ImageWithCenterOfMass> {

    /**
     * Compares this image to [other] by summing absolute differences
     * of gray values over the overlapping region when their centers
     * of mass are aligned.
     */
    override fun compareTo(other: ImageWithCenterOfMass): Int {
        val w1 = image.width
        val h1 = image.height
        val w2 = other.image.width
        val h2 = other.image.height

        // compute integer shift to align centers of mass
        val dx = (other.centerOfMassX - centerOfMassX).roundToInt()
        val dy = (other.centerOfMassY - centerOfMassY).roundToInt()

        var errorSum = 0

        for (y in 0 until h1) {
            for (x in 0 until w1) {
                // corresponding coords in other image
                val xo = x + dx
                val yo = y + dy

                // only if inside other image bounds
                if (xo in 0 until w2 && yo in 0 until h2) {
                    val rgb1 = image.getRGB(x, y)
                    val rgb2 = other.image.getRGB(xo, yo)

                    // both images are grayscale so R=G=B; extract any channel:
                    val gray1 = rgb1 and 0xFF
                    val gray2 = rgb2 and 0xFF

                    errorSum += abs(gray1 - gray2)
                }
                // else: no overlap → skip
            }
        }

        return errorSum
    }

    fun imageWithCrossHair(): BufferedImage {

        // Create a copy of the original image
        val result = BufferedImage(image.width, image.height, image.type)
        val g2 = result.createGraphics()
        g2.drawImage(this.image, 0, 0, null)

        // Draw crosshair
        g2.color = Color.RED
        g2.drawLine(0, centerOfMassY.toInt(), image.width - 1, centerOfMassY.toInt())

        g2.color = Color.BLUE
        g2.drawLine(centerOfMassX.toInt(), 0, centerOfMassX.toInt(), image.height - 1)

        g2.dispose()
        return result
    }
}