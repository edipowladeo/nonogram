package org.example.ocr

import org.example.configparams.DebugParams.DEBUG_MASTER
import org.example.configparams.DebugParams.DEBUG_NUMERALS
import org.example.configparams.OcrParams
import org.example.configparams.OcrParams.MIN_DENSITY_THRESHOLD
import org.example.Window
import org.example.arithmetic.Bar
import org.example.bufferedImageExtensions.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.random.Random


class NumeralOCR {

    private val numeralImages = (0..9).mapNotNull { digit ->
        val file = File("src/main/resources/numerals/bw_${digit}.png")

        if (file.exists()) ImageIO.read(file) else throw Exception("Failed to load numerals for OCR. File not found: ${file.absolutePath}")

        val originalImage: BufferedImage = ImageIO.read(file)


        val numeralHeight =
            getNumberHeight(originalImage) ?: throw Exception("Failed to get numeral height for digit $digit")

        val scaledNumeral = originalImage.resize(OcrParams.COMPARISON_IMAGE_HEIGHT / numeralHeight.width)

        val numeralWithCenterOfMass =
            scaledNumeral.toBlackAndWhite(0.25).computeCenterOfMass() // todo check thresold, maybe use param
                ?: throw Exception("Failed to compute center of mass for numeral $digit")

        if (DEBUG_MASTER && DEBUG_NUMERALS) {
            Window(
                numeralWithCenterOfMass.imageWithCrossHair()
                    .resize(scaleFactor = 200 / OcrParams.COMPARISON_IMAGE_HEIGHT),
                "CENTER OF MASS, numeral: $digit",
                y = 0,
                x = digit * 120
            )

            Window(
                originalImage.transpose().toVerticalBars().toBlackAndWhite(MIN_DENSITY_THRESHOLD),
                "original numeral: $digit",
                y = 50,
                x = digit * 120
            ) //DEBUG
            Window(
                originalImage.transpose().toVerticalBars(), "original numeral: $digit", y = 100, x = digit * 120
            ) //DEBUG
            Window(originalImage.transpose(), "original numeral: $digit", y = 150, x = digit * 120) //DEBUG
            Window(originalImage, "original numeral: $digit", y = 250, x = digit * 120) //DEBUG
        }
        numeralWithCenterOfMass
    }

    fun interpretNumerals(image: BufferedImage,debug: Boolean=false): Int? {
        val height = getNumberHeight(image)
            ?: return null //TODO WHO IS RESPONSIBLE FOR CHECK IF HE NUMBER IS BLANK? HEIGHT OR CENTER OF MASS?
        val widths = getNumberWidth(image)
        if (widths.isEmpty()) {
            println("widths is empty")
            Window(image, "widths is empty", x = 1200, y = 100, monitorIndex = 1)
            return null
        }
        val numbers = widths.map {
            val croppedImage = image.cropXY(
                x1 = (it.center - it.width / 2).toInt() - OcrParams.PADDING_PIXELS,
                y1 = (height.center - height.width / 2).toInt() + 1 - OcrParams.PADDING_PIXELS,
                x2 = (it.center + it.width / 2 ).toInt() + OcrParams.PADDING_PIXELS*2,
                y2 = (height.center + height.width / 2).toInt() + 1 + OcrParams.PADDING_PIXELS*2
            )
            val scaledImage = croppedImage.resize(OcrParams.COMPARISON_IMAGE_HEIGHT / height.width)
            if (debug) {
                val y = Random.nextInt(350, 700)
                val x = Random.nextInt(770, 1350)
                val scale = 450.0 / image.height
                val bestDigit = interpretSingleNumeral(scaledImage)
             //    Window(image.resize(scale), "OK! N: $bestDigit", x = x, y = y, monitorIndex = 0)

               // Window(croppedImage.resize(scale), "OK! N: $bestDigit", x = x, y = y + 90, monitorIndex = 0)
               //  Window(croppedImage.resize(scale).toVerticalBars(), "OK! N: $bestDigit", x = x, y = y + 180, monitorIndex = 0)

                //Window(croppedImage.resize(scale).toVerticalBars().toBlackAndWhite(OcrParams.MIN_DENSITY_THRESHOLD ), "OK! N: $bestDigit", x = x, y = y + 270, monitorIndex = 0)
                //println("interpretNumeral: $bestDigit, error: $bestError")
            }
            val interpretationResult = interpretSingleNumeral(scaledImage)
            if (interpretationResult == null) {
                println("interpretationResult is null")
                return  null
            }
            interpretationResult
        }

        if (debug) {
            val y = Random.nextInt(350, 700)
            val x = Random.nextInt(770, 1350)
            val scale = 450.0 / image.height
            val bestDigit = numbers
            Window(image.resize(scale), "OK! N: $bestDigit", x = x, y = y, monitorIndex = 0)

          //  Window(image.resize(scale).toVerticalBars(), "OK! N: $bestDigit", x = x, y = y + 180, monitorIndex = 0)
            Window(image.resize(scale).toVerticalBars().toBlackAndWhite(MIN_DENSITY_THRESHOLD), "OK! N: $bestDigit", x = x, y = y + 180, monitorIndex = 0)

            //Window(croppedImage.resize(scale).toVerticalBars().toBlackAndWhite(OcrParams.MIN_DENSITY_THRESHOLD ), "OK! N: $bestDigit", x = x, y = y + 270, monitorIndex = 0)
            //println("interpretNumeral: $bestDigit, error: $bestError")
        }
        //println("read numbers: $numbers")

        if (numbers.isEmpty())return null //todo check cases where list is null
            val number = numbers.joinToString(separator = "") { it.toString() }.toInt()

return number
    }

    fun interpretSingleNumeral(scaledImage: BufferedImage): Int?{

        val centerOfMassImage = scaledImage.toBlackAndWhite(0.25).computeCenterOfMass()
            ?: return null //todo check thresold, maybe use param

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


        return bestDigit
    }


    private fun getNumberHeight(image: BufferedImage): Bar? {
        val brightnessThresold = MIN_DENSITY_THRESHOLD // todo check if this is correct
        fun debugImages(title: String) {
            val desiredHeight = 400
            Window(
                image.transpose().reduceVerticallyAverage().toBlackAndWhite(brightnessThresold).transpose()
                    .resize(width = image.width).resize(scaleFactor = desiredHeight.toDouble() / image.height),
                title,
                y = 400,
                x = 25
            )
            Window(
                image.transpose().reduceVerticallyAverage().transpose().resize(width = image.width)
                    .resize(scaleFactor = desiredHeight.toDouble() / image.height), title, y = 400, x = 200
            )
            Window(
                image.resize(scaleFactor = desiredHeight.toDouble() / image.height), title, y = 400, x = 375
            )
        }

        val horizontalBars =
            image.transpose().reduceVerticallyAverage().toBlackAndWhite(brightnessThresold).readBarPositions().filter {
                it.width > image.height * OcrParams.MIN_EXPECTED_HEIGHT
            }

        if (horizontalBars.isEmpty()) {
            return null
        }
        if (horizontalBars.size > 1) {
            val error = "ERROR: multiple horizontal projections: ${horizontalBars.size}"
            debugImages(error)
            return null //throw Exception(error) //
        }

        return horizontalBars.first()
    }

    private fun getNumberWidth(image: BufferedImage): List<Bar> {
        val brightnessThresold = MIN_DENSITY_THRESHOLD // todo check if this is correct
        fun debugImages(title: String) {
            val desiredHeight = 400
            Window(
                image.transpose().reduceVerticallyAverage().toBlackAndWhite(brightnessThresold).transpose()
                    .resize(width = image.width).resize(scaleFactor = desiredHeight.toDouble() / image.height),
                title,
                y = 400,
                x = 25
            )
            Window(
                image.transpose().reduceVerticallyAverage().transpose().resize(width = image.width)
                    .resize(scaleFactor = desiredHeight.toDouble() / image.height), title, y = 400, x = 200
            )
            Window(
                image.resize(scaleFactor = desiredHeight.toDouble() / image.height), title, y = 400, x = 375
            )
        }

        val verticalBars =
            image.reduceVerticallyAverage().toBlackAndWhite(brightnessThresold).readBarPositions().filter {
                it.width > image.height * OcrParams.MIN_EXPECTED_WIDTH
            }


        if (verticalBars.size > 2) {
            val error = "ERROR: multiple horizontal projections: ${verticalBars.size}"
            debugImages(error)
            return emptyList()//throw Exception(error)
        }

        return verticalBars
    }
}




