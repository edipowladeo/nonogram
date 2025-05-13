package org.example

import org.example.DebugParams.DEBUG_BOUNDARIES
import org.example.DebugParams.DEBUG_MODE
import org.example.DebugParams.DEBUG_VERTICAL_BARS
import org.example.DebugParams.DEBUG_HORIZONTAL_BARS
import org.example.GameImageParams.REDUCTION_DENSITY_THRESHOLD
import org.example.arithmetic.removeOutliersFromArithmeticProgression
import org.example.bufferedImageExtensions.*
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File


fun main() {
    val imageFile = File("src/main/devResources/10screenshot.jpeg")

    val originalImage: BufferedImage = ImageIO.read(imageFile)
    val width = originalImage.width
    val height = originalImage.height


    val RED = 0x79FF0000
    val GREEN = 0x7900FF00
    val BLUE = 0x790000ff
    val MAGENTA = 0x79ff00ff

    // Convert the image to grayscale
    val grayImage = originalImage.toGrayscale()

    val sobelKernelVerticalLeft = arrayOf(
        intArrayOf(1, 0, -1), intArrayOf(2, 0, -2), intArrayOf(1, 0, -1)
    )

    val verticalBarsLeft =
        grayImage.applyConvolution(sobelKernelVerticalLeft).reduceVerticallyAverage().toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD)
            .readBarPositions().removeOutliersFromArithmeticProgression(0.2)

    val horizontalBarsLeft = grayImage.transpose().applyConvolution(sobelKernelVerticalLeft).reduceVerticallyAverage()
        .toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD).readBarPositions().removeOutliersFromArithmeticProgression(0.2)

    val sobelKernelVerticalRight = arrayOf(
        intArrayOf(-1, 0, 1), intArrayOf(-2, 0, 2), intArrayOf(-1, 0, 1)
    )

    val verticalBarsRight =
        grayImage.applyConvolution(sobelKernelVerticalRight).reduceVerticallyAverage().toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD)
            .readBarPositions().removeOutliersFromArithmeticProgression(0.2)

    val horizontalBarsRight =
        grayImage.transpose().applyConvolution(sobelKernelVerticalRight).reduceVerticallyAverage()
        .toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD).readBarPositions().removeOutliersFromArithmeticProgression(0.2, debug = true)

    Window(grayImage.transpose().applyConvolution(sobelKernelVerticalRight).reduceVerticallyAverage().toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD).resize(height=500),"dEBUG EXTRA")

    require(verticalBarsLeft.size == verticalBarsRight.size) { "Vertical bars left and right sizes do not match: ${verticalBarsLeft.size} != ${verticalBarsRight.size}" }
    //require(horizontalBarsLeft.size == horizontalBarsRight.size) { "Horizontal bars left and right sizes do not match: ${horizontalBarsLeft.size} != ${horizontalBarsRight.size}" }

    Window(grayImage)
    Window(grayImage.applyConvolution(sobelKernelVerticalLeft))
    Window(grayImage.applyConvolution(sobelKernelVerticalLeft).reduceVerticallyAverage().resize(500))
    Window(grayImage.applyConvolution(sobelKernelVerticalLeft).reduceVerticallyAverage().toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD).resize(500))
    println("Vertical bars left: ${verticalBarsLeft.size}, right: ${verticalBarsRight.size}")
    println("Horizontal bars left: ${horizontalBarsLeft.size}, right: ${horizontalBarsRight.size}")
    val verticalBars = verticalBarsLeft.zip(verticalBarsRight) { left, right ->
        Bar(
            center = (left.center + right.center) / 2,
            width = (left.width + right.width) / 2,
        )
    }
    val horizontalBars = horizontalBarsLeft/*.zip(horizontalBarsRight) { left, right ->
        Bar(
            center = (left.center + right.center) / 2,
            width = (left.width + right.width) / 2,
        )
    }*/


    val verticalProofBars = drawVerticalBars(verticalBarsLeft, width, color = BLUE).resize(height = height)


    val horizontalProofBars =
        drawVerticalBars(horizontalBarsLeft, height, color = RED).resize(height = originalImage.width).transpose()

    val verticalBarsRaw =
        grayImage.applyConvolution(sobelKernelVerticalLeft).reduceVerticallyAverage().toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD)
            .readBarPositions()

    val horizontalBarsRaw = grayImage.transpose().applyConvolution(sobelKernelVerticalLeft).reduceVerticallyAverage()
        .toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD).readBarPositions()

    val verticalProofBarsRaw = drawVerticalBars(verticalBarsRaw, width, color = BLUE).resize(height = height)
    val horizontalProofBarsRaw =
        drawVerticalBars(horizontalBarsRaw, height, color = RED).resize(height = originalImage.width).transpose()

    val verticalProofBarsThinLeft = drawVerticalBarsThin(verticalBarsLeft, width, color = BLUE).resize(height = height)
    val horizontalProofBarsThinLeft =
        drawVerticalBarsThin(horizontalBarsLeft, height, color = RED).resize(height = originalImage.width).transpose()
    val verticalProofBarsThinRight = drawVerticalBarsThin(verticalBarsRight, width, color = MAGENTA).resize(height = height)
    val horizontalProofBarsThinRight =
        drawVerticalBarsThin(horizontalBarsRight, height, color = GREEN).resize(height = originalImage.width)
            .transpose()

    val gameImage = originalImage.cropXY( //todo do should be in constructor of GameImage
        verticalBars.first().center.toInt(),
        verticalBars.last().center.toInt(),
        horizontalBars.first().center.toInt(),
        horizontalBars.last().center.toInt()
    )


    val resized = gameImage.resize(0.55)

    if (DEBUG_MODE && DEBUG_VERTICAL_BARS) {
        //  Window(grayImage, "Game Image", x = 1100, y = 200, monitorIndex = 1)
        Window(
            grayImage.applyConvolution(sobelKernelVerticalLeft).alphaComposite(verticalProofBarsThinLeft),
            "Kernel Left",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )
        Window(
            grayImage.applyConvolution(sobelKernelVerticalLeft).toVerticalBars()
                .alphaComposite(verticalProofBarsThinLeft),
            "Kernel Left detected ${verticalBarsLeft.size} bars",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )
        Window(
                grayImage.applyConvolution(sobelKernelVerticalLeft).toVerticalBars().toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD)
                    .alphaComposite(verticalProofBarsThinLeft),
        "Kernel Left detected ${verticalBarsLeft.size} bars",
        x = 1100,
        y = 0,
        monitorIndex = 1
        )
        Window(
            grayImage.applyConvolution(sobelKernelVerticalRight).alphaComposite(verticalProofBarsThinRight),
            "Kernel Right",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )
        Window(
            grayImage.applyConvolution(sobelKernelVerticalRight).toVerticalBars()
                .alphaComposite(verticalProofBarsThinRight),
            "Kernel Right detected ${verticalBarsRight.size} bars",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )
        Window(
                grayImage.applyConvolution(sobelKernelVerticalRight).toVerticalBars().toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD)
                    .alphaComposite(verticalProofBarsThinRight),
        "Kernel Right detected ${verticalBarsRight.size} bars",
        x = 1100,
        y = 0,
        monitorIndex = 1
        )
        Window(
            grayImage.alphaComposite(verticalProofBarsThinLeft).alphaComposite(verticalProofBarsThinRight)
                .alphaComposite(horizontalProofBarsThinLeft).alphaComposite(horizontalProofBarsThinRight),
            "Game Image",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )

    }

    if (DEBUG_MODE && DEBUG_HORIZONTAL_BARS) {
        //  Window(grayImage, "Game Image", x = 1100, y = 200, monitorIndex = 1)
        Window(
            grayImage.transpose().applyConvolution(sobelKernelVerticalLeft).alphaComposite(horizontalProofBarsThinLeft.transpose()),
            "Kernel Left",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )
        Window(
            grayImage.transpose().applyConvolution(sobelKernelVerticalLeft).toVerticalBars()
                .alphaComposite(horizontalProofBarsThinLeft.transpose()),
            "Kernel Left detected ${horizontalBarsLeft.size} bars",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )
        Window(
            grayImage.transpose().applyConvolution(sobelKernelVerticalRight).alphaComposite(horizontalProofBarsThinRight.transpose()),
            "Kernel Right",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )
        Window(
            grayImage.transpose().applyConvolution(sobelKernelVerticalRight).toVerticalBars()
                .alphaComposite(horizontalProofBarsThinRight.transpose()),
            "Kernel Right detected ${horizontalBarsRight.size} bars",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )
        Window(
            grayImage.alphaComposite(verticalProofBarsThinLeft).alphaComposite(verticalProofBarsThinRight)
                .alphaComposite(horizontalProofBarsThinLeft).alphaComposite(horizontalProofBarsThinRight).transpose(),
            "Game Image",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )

    }

    if (DEBUG_MODE && DEBUG_BOUNDARIES) {

        Window(originalImage.resize(0.55), "Game Image", x = 100, y = 0, monitorIndex = 1)
        Window(verticalProofBars.alphaComposite(horizontalProofBars).resize(0.55), monitorIndex = 1, x = 1300)
        Window(verticalProofBarsRaw.alphaComposite(horizontalProofBarsRaw).resize(0.55), monitorIndex = 1, x = 2500)



        Window(resized, "Game Image", x = 800, y = 700, monitorIndex = 1)
        Window(resized.toGrayscale(), "Game Image", x = 1000, y = 600, monitorIndex = 1)
        Window(resized.toGrayscale().toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD), "Game Image", x = 1200, y = 500, monitorIndex = 1)
        Window(
            resized.toGrayscale().applyConvolution(sobelKernelVerticalLeft),
            "Game Image",
            x = 1400,
            y = 400,
            monitorIndex = 1
        )
        Window(
            resized.toGrayscale().transpose().applyConvolution(sobelKernelVerticalLeft).transpose(),
            "Game Image",
            x = 1600,
            y = 300,
            monitorIndex = 1
        )


        val gameDebugImage =
            originalImage.alphaComposite(horizontalProofBarsThinLeft) //todo this should not be in game image
                .alphaComposite(verticalProofBarsThinLeft).alphaComposite(horizontalProofBarsThinRight)
                .alphaComposite(verticalProofBarsThinRight).cropXY( //todo do should be in constructor of GameImage
                    verticalBars.first().center.toInt(),
                    verticalBars.last().center.toInt(),
                    horizontalBars.first().center.toInt(),
                    horizontalBars.last().center.toInt()
                )

        Window(gameDebugImage, "Game DEBUG Image", x = 1100, y = 200, monitorIndex = 1)

        Window(gameDebugImage, "Game DEBUG Image", x = 1200, y = 200, monitorIndex = 1)
    }

    val game = GameImage(
        gameImage,
        verticalBars = verticalBars.map { it.center - verticalBars.first().center }
            .toDoubleArray(), // todo should be in constructor of gameImage
        horizontalBars = horizontalBars.map { it.center - horizontalBars.first().center }
            .toDoubleArray() // todo should be in constructor of gameImage
    )

    System.setProperty("jna.library.path", "/opt/homebrew/lib")
    System.setProperty("TESSDATA_PREFIX", "/opt/homebrew/share")

    println("V ${verticalBarsLeft.joinToString(", ")}")
    println("H ${horizontalBarsLeft.joinToString(", ")}")

    println("GameImage Width: ${gameImage.width}, Height: ${gameImage.height})")
    println("Cells: X: ${game.width}, Y: ${game.height}")

    println("Detected game cells: ${game.rows} x ${game.columns}")



    game.getAllColumnClues()

    //game.getColumnClues(28)


    //game.getGameCell(7,2,0.85).convertToGrayscale().toBlackAndWhite(128).also { ImageIO.write(it, "png", File("bw_4.png"))}
}