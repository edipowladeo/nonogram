package org.example.bufferedImageExtensions

import org.example.ImageWithCenterOfMass
import java.awt.Color
import java.awt.image.BufferedImage

fun BufferedImage.computeCenterOfMass(): ImageWithCenterOfMass? { // todo move to venter of mass class ant init{}
    var sumX = 0L
    var sumY = 0L
    var mass = 0L

    for (y in 0 until height) {
        for (x in 0 until width) {
            val rgb = getRGB(x, y) and 0xFFFFFF
            if (rgb == 0x000000) { // black pixel
                sumX += x
                sumY += y
                mass++
            }
        }
    }

    return if (mass == 0L) {
        null // no black pixels, no center of mass
    } else {
        ImageWithCenterOfMass(
            image = this,
            centerOfMassX = sumX.toDouble() / mass,
            centerOfMassY = sumY.toDouble() / mass
        )
    }
}
