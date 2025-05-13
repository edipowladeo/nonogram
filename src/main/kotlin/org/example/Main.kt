package org.example

import org.example.arithmetic.removeOutliersFromArithmeticProgression
import org.example.bufferedImageExtensions.*
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File


fun main() {
    val imageFile = File("src/main/devResources/big30screenshot.jpeg")

    val originalImage: BufferedImage = ImageIO.read(imageFile)
    val width = originalImage.width
    val height = originalImage.height


    val RED = 0x79FF0000
    val GREEN = 0x7900FF00
    val BLUE = 0x790000ff
    val MAGENTA = 0x79ff00ff

    // Convert the image to grayscale
    val grayImage = originalImage.toGrayscale()

    val sobelKernelVertical = arrayOf(
        intArrayOf(1, 0, -1),
        intArrayOf(2, 0, -2),
        intArrayOf(1, 0, -1)
    )

    val verticalBars = grayImage.applyConvolution(sobelKernelVertical)
        .reduceVerticallyAverage().applyNegativeThreshold(128 - 64)
        .readBarPositions().removeOutliersFromArithmeticProgression(0.2)

    val horizontalBars = grayImage.transpose().applyConvolution(sobelKernelVertical)
        .reduceVerticallyAverage().applyNegativeThreshold(128 - 64)
        .readBarPositions().removeOutliersFromArithmeticProgression(0.2)

    val verticalProofBars = drawVerticalBars(verticalBars, width, color = BLUE)
        .resize(height = height)
    val horizontalProofBars = drawVerticalBars(horizontalBars, height, color = RED)
        .resize(height = originalImage.width).transpose()

    val verticalBarsRaw = grayImage.applyConvolution(sobelKernelVertical)
        .reduceVerticallyAverage().applyNegativeThreshold(128 - 64)
        .readBarPositions()

    val horizontalBarsRaw = grayImage.transpose().applyConvolution(sobelKernelVertical)
        .reduceVerticallyAverage().applyNegativeThreshold(128 - 64)
        .readBarPositions()

    val verticalProofBarsRaw = drawVerticalBars(verticalBarsRaw, width, color = BLUE)
        .resize(height = height)
    val horizontalProofBarsRaw = drawVerticalBars(horizontalBarsRaw, height, color = RED)
        .resize(height = originalImage.width).transpose()

    val verticalProofBarsThin = drawVerticalBarsThin(verticalBars, width, color = BLUE)
        .resize(height = height)
    val horizontalProofBarsThin = drawVerticalBarsThin(horizontalBars, height, color = RED )
        .resize(height = originalImage.width).transpose()

    val gameImage = originalImage
        .alphaComposite(verticalProofBarsThin)
        .alphaComposite(horizontalProofBarsThin)
        .crop(
            verticalBars.first().center.toInt(),
            verticalBars.last().center.toInt(),
            horizontalBars.first().center.toInt(),
            horizontalBars.last().center.toInt()
        )


    val resized = gameImage.resize(0.55)

    Window(originalImage.resize(0.55), "Game Image",x = 100, y = 0, monitorIndex = 1)
    Window(verticalProofBars.alphaComposite(horizontalProofBars).resize(0.55), monitorIndex = 1, x = 1300)
    Window(verticalProofBarsRaw.alphaComposite(horizontalProofBarsRaw).resize(0.55), monitorIndex = 1, x = 2500)



    Window(resized, "Game Image",x = 800, y = 700, monitorIndex = 1)
    Window(resized.toGrayscale(), "Game Image",x = 1000, y = 600, monitorIndex = 1)
    Window(resized.toGrayscale().toBlackAndWhite(180), "Game Image",x = 1200, y = 500, monitorIndex = 1)
    Window(resized.toGrayscale().applyConvolution(sobelKernelVertical), "Game Image",x = 1400, y = 400, monitorIndex = 1)
    Window(resized.toGrayscale().transpose().applyConvolution(sobelKernelVertical).transpose(), "Game Image",x = 1600, y = 300, monitorIndex = 1)

    val debugImage = originalImage
        .alphaComposite(verticalProofBars)
        .alphaComposite(horizontalProofBars)
        .crop(
            verticalBars.first().center.toInt(),
            verticalBars.last().center.toInt(),
            horizontalBars.first().center.toInt(),
            horizontalBars.last().center.toInt()
        ).resize(0.55)
    Window(debugImage, "Game Image",x = 1800, y = 200, monitorIndex = 1)




    val game = GameImage(
        gameImage,
        verticalBars = verticalBars.map { it.center - verticalBars.first().center }.toDoubleArray(),
        horizontalBars = horizontalBars.map { it.center - horizontalBars.first().center }.toDoubleArray()
    )

    System.setProperty("jna.library.path", "/opt/homebrew/lib")
    System.setProperty("TESSDATA_PREFIX", "/opt/homebrew/share")

    println("V ${verticalBars.joinToString(", ")}")
    println("H ${horizontalBars.joinToString(", ")}")

    println("GameImage Width: ${gameImage.width}, Height: ${gameImage.height})")
    println("Cells: X: ${game.width}, Y: ${game.height}")

    println("Detected game cells: ${game.rows} x ${game.columns}")



   game.getAllColumnClues()
    Window(gameImage, "Game Image",x = 1100, y = 200, monitorIndex = 1)
   //game.getColumnClues(28)


    //game.getGameCell(7,2,0.85).convertToGrayscale().applyNegativeThreshold(128).also { ImageIO.write(it, "png", File("bw_4.png"))}
}