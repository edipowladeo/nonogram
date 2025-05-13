package org.example.ocr

import org.example.OcrParams
import org.example.Window
import org.example.bufferedImageExtensions.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.roundToInt
import kotlin.random.Random


class NumeralOCR {

    val numeralImages = (0..9).mapNotNull { digit ->
        val file = File("src/main/resources/numerals/bw_${digit}.png")

        if (file.exists()) ImageIO.read(file) else throw Exception("Failed to load numerals for OCR. File not found: ${file.absolutePath}")

        val originalImage: BufferedImage = ImageIO.read(file)


        val numeralHeight = getNumberHeight(originalImage)?: throw Exception("Failed to get numeral height for digit $digit")
      //  println("numeral $digit height: $numeralHeight")

        val scaledNumeral = originalImage.resize(OcrParams.COMPARISON_IMAGE_HEIGHT/numeralHeight)

        val numeralWithCenterOfMass = scaledNumeral.toBlackAndWhite(128+64).computeCenterOfMass()
            ?: throw Exception("Failed to compute center of mass for numeral $digit")

//        println("Loaded numeral image for digit: $digit, width: ${originalImage.width}, height: ${originalImage.height}")
        Window(numeralWithCenterOfMass.imageWithCrossHair().resize(scaleFactor = 200/OcrParams.COMPARISON_IMAGE_HEIGHT), "CENTER OF MASS, numeral: $digit", y = 0, x = digit * 120) //DEBUG
        Window(originalImage, "original numeral: $digit", y = 50, x = digit * 120) //DEBUG

        numeralWithCenterOfMass
    }

    fun interpretNumeral(image: BufferedImage): Int? {
        val height = getNumberHeight(image)?: return null //TODO WHO IS RESPONSIBLE FOR CHECK IF HE NUMBER IS BLANK? HEIGHT OR CENTER OF MASS?

        val scaledImage = image.resize(OcrParams.COMPARISON_IMAGE_HEIGHT / height)
        val centerOfMassImage = scaledImage.toBlackAndWhite(128+64).computeCenterOfMass()
            ?: return null

        var bestDigit = -1
        var bestError = Int.MAX_VALUE

        for (digit in 0..9) {
            val error = centerOfMassImage.compareTo(numeralImages[digit])

            when {
                error < bestError -> {
                    bestError = error
                    bestDigit = digit
                }
            }
        }

        Window(scaledImage.resize(2.0), "OK! N: $bestDigit", y = Random.nextInt(350, 700), x = Random.nextInt(770, 1350), monitorIndex = 0) //DEBUG
        //println("interpretNumeral: $bestDigit, error: $bestError")
        return bestDigit
    }

    fun getNumberHeight(image: BufferedImage): Int? {
        val brightnessThresold = (255 - 255*OcrParams.MIN_DENSITY_THRESHOLD).roundToInt()
        fun debugImages(title: String) {
            val desiredHeight = 400
            Window(image.transpose().reduceVerticallyAverage().toBlackAndWhite(brightnessThresold).transpose()
                .resize(width = image.width).resize(scaleFactor = desiredHeight.toDouble()/image.height), title, y = 400, x =25)
            Window(image.transpose().reduceVerticallyAverage().transpose()
                .resize(width = image.width).resize(scaleFactor = desiredHeight.toDouble()/image.height), title, y = 400,x =200)
            Window(image
                .resize(scaleFactor = desiredHeight.toDouble()/image.height), title, y = 400,x=375)
         }
        val horizontalBars = image.transpose().reduceVerticallyAverage().toBlackAndWhite(brightnessThresold)
            .readBarPositions()
            .filter {
                it.width > image.height * OcrParams.MIN_EXPECTED_HEIGHT
            }

        if (horizontalBars.isEmpty()) {
            return null
        }
        if (horizontalBars.size > 1) {
           val error ="ERROR: multiple horizontal projections: ${horizontalBars.size}"
            debugImages(error)
            return null //throw Exception(error) //
        }

        return horizontalBars.first().width.toInt()
    }
}




