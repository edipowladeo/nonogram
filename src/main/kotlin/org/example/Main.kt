package org.example

import org.example.DebugParams.DEBUG_BOUNDARIES
import org.example.DebugParams.DEBUG_MASTER
import org.example.DebugParams.DEBUG_VERTICAL_BARS
import org.example.DebugParams.DEBUG_HORIZONTAL_BARS
import org.example.DebugParams.DEBUG_REMOVE_OUTLIERS
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

    val sobelKernelVerticalStarts = arrayOf(
        intArrayOf(1, 0, -1), intArrayOf(2, 0, -2), intArrayOf(1, 0, -1)
    )

    val verticalbarStarts =
        grayImage.applyConvolution(sobelKernelVerticalStarts).reduceVerticallyAverage().toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD)
            .readBarPositions().removeOutliersFromArithmeticProgression(0.2)

    val horizontalbarStarts = grayImage.transpose().applyConvolution(sobelKernelVerticalStarts).reduceVerticallyAverage()
        .toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD).readBarPositions().removeOutliersFromArithmeticProgression(0.2)

    val sobelKernelVerticalEnds = arrayOf(
        intArrayOf(-1, 0, 1), intArrayOf(-2, 0, 2), intArrayOf(-1, 0, 1)
    )

    val verticalbarEnds =
        grayImage.applyConvolution(sobelKernelVerticalEnds).reduceVerticallyAverage().toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD)
            .readBarPositions().removeOutliersFromArithmeticProgression(0.2)

    val horizontalbarEnds =
        grayImage.transpose().applyConvolution(sobelKernelVerticalEnds).reduceVerticallyAverage()
        .toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD).readBarPositions().removeOutliersFromArithmeticProgression(0.2, debug = (DEBUG_MASTER&&DEBUG_REMOVE_OUTLIERS))


    require(verticalbarStarts.size == verticalbarEnds.size) { "Vertical bars Starts and Ends sizes do not match: ${verticalbarStarts.size} != ${verticalbarEnds.size}" }
    //require(horizontalbarStarts.size == horizontalbarEnds.size) { "Horizontal bars Starts and Ends sizes do not match: ${horizontalbarStarts.size} != ${horizontalbarEnds.size}" }

  /*  val verticalBars = verticalbarStarts.zip(verticalbarEnds) { Starts, Ends ->
        Bar(
            center = (Starts.center + Ends.center) / 2,
            width = (Starts.width + Ends.width) / 2,
        )
    }
    val horizontalBars = horizontalbarStarts.zip(horizontalbarEnds) { Starts, Ends ->
        Bar(
            center = (Starts.center + Ends.center) / 2,
            width = (Starts.width + Ends.width) / 2,
        )
    }

   */

    val verticalProofBars = drawVerticalBars(verticalbarStarts, width, color = BLUE).resize(height = height)


    val horizontalProofBars =
        drawVerticalBars(horizontalbarStarts, height, color = RED).resize(height = originalImage.width).transpose()

    val verticalBarsRaw =
        grayImage.applyConvolution(sobelKernelVerticalStarts).reduceVerticallyAverage().toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD)
            .readBarPositions()

    val horizontalBarsRaw = grayImage.transpose().applyConvolution(sobelKernelVerticalStarts).reduceVerticallyAverage()
        .toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD).readBarPositions()

    val verticalProofBarsRaw = drawVerticalBars(verticalBarsRaw, width, color = BLUE).resize(height = height)
    val horizontalProofBarsRaw =
        drawVerticalBars(horizontalBarsRaw, height, color = RED).resize(height = originalImage.width).transpose()

    val verticalProofBarsThinStarts = drawVerticalBarsThin(verticalbarStarts, width, color = BLUE).resize(height = height)
    val horizontalProofBarsThinStarts =
        drawVerticalBarsThin(horizontalbarStarts, height, color = RED).resize(height = originalImage.width).transpose()
    val verticalProofBarsThinEnds = drawVerticalBarsThin(verticalbarEnds, width, color = MAGENTA).resize(height = height)
    val horizontalProofBarsThinEnds =
        drawVerticalBarsThin(horizontalbarEnds, height, color = GREEN).resize(height = originalImage.width)
            .transpose()

    val gameImage = originalImage.cropXY( //todo do should be in constructor of GameImage
        verticalbarStarts.first().center.toInt(),
        verticalbarEnds.last().center.toInt(),
        horizontalbarStarts.first().center.toInt(),
        horizontalbarEnds.last().center.toInt()
    )


    val resized = gameImage.resize(0.55)

    if (DEBUG_MASTER && DEBUG_VERTICAL_BARS) {
        //  Window(grayImage, "Game Image", x = 1100, y = 200, monitorIndex = 1)
        Window(
            grayImage.applyConvolution(sobelKernelVerticalStarts).alphaComposite(verticalProofBarsThinStarts),
            "Kernel Starts",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )
        Window(
            grayImage.applyConvolution(sobelKernelVerticalStarts).toVerticalBars()
                .alphaComposite(verticalProofBarsThinStarts),
            "Kernel Starts detected ${verticalbarStarts.size} bars",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )
        Window(
                grayImage.applyConvolution(sobelKernelVerticalStarts).toVerticalBars().toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD)
                    .alphaComposite(verticalProofBarsThinStarts),
        "Kernel Starts detected ${verticalbarStarts.size} bars",
        x = 1100,
        y = 0,
        monitorIndex = 1
        )
        Window(
            grayImage.applyConvolution(sobelKernelVerticalEnds).alphaComposite(verticalProofBarsThinEnds),
            "Kernel Ends",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )
        Window(
            grayImage.applyConvolution(sobelKernelVerticalEnds).toVerticalBars()
                .alphaComposite(verticalProofBarsThinEnds),
            "Kernel Ends detected ${verticalbarEnds.size} bars",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )
        Window(
                grayImage.applyConvolution(sobelKernelVerticalEnds).toVerticalBars().toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD)
                    .alphaComposite(verticalProofBarsThinEnds),
        "Kernel Ends detected ${verticalbarEnds.size} bars",
        x = 1100,
        y = 0,
        monitorIndex = 1
        )
        Window(
            grayImage.alphaComposite(verticalProofBarsThinStarts).alphaComposite(verticalProofBarsThinEnds)
                .alphaComposite(horizontalProofBarsThinStarts).alphaComposite(horizontalProofBarsThinEnds),
            "Game Image",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )

    }

    if (DEBUG_MASTER && DEBUG_HORIZONTAL_BARS) {
        //  Window(grayImage, "Game Image", x = 1100, y = 200, monitorIndex = 1)
        Window(
            grayImage.transpose().applyConvolution(sobelKernelVerticalStarts).alphaComposite(horizontalProofBarsThinStarts.transpose()),
            "Kernel Starts",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )
        Window(
            grayImage.transpose().applyConvolution(sobelKernelVerticalStarts).toVerticalBars()
                .alphaComposite(horizontalProofBarsThinStarts.transpose()),
            "Kernel Starts detected ${horizontalbarStarts.size} bars",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )
        Window(
            grayImage.transpose().applyConvolution(sobelKernelVerticalEnds).alphaComposite(horizontalProofBarsThinEnds.transpose()),
            "Kernel Ends",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )
        Window(
            grayImage.transpose().applyConvolution(sobelKernelVerticalEnds).toVerticalBars()
                .alphaComposite(horizontalProofBarsThinEnds.transpose()),
            "Kernel Ends detected ${horizontalbarEnds.size} bars",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )
        Window(
            grayImage.alphaComposite(verticalProofBarsThinStarts).alphaComposite(verticalProofBarsThinEnds)
                .alphaComposite(horizontalProofBarsThinStarts).alphaComposite(horizontalProofBarsThinEnds).transpose(),
            "Game Image",
            x = 1100,
            y = 0,
            monitorIndex = 1
        )

    }

    if (DEBUG_MASTER && DEBUG_BOUNDARIES) {

        Window(originalImage.resize(0.55), "Game Image", x = 100, y = 0, monitorIndex = 1)
        Window(verticalProofBars.alphaComposite(horizontalProofBars).resize(0.55), monitorIndex = 1, x = 1300)
        Window(verticalProofBarsRaw.alphaComposite(horizontalProofBarsRaw).resize(0.55), monitorIndex = 1, x = 2500)



        Window(resized, "Game Image", x = 800, y = 700, monitorIndex = 1)
        Window(resized.toGrayscale(), "Game Image", x = 1000, y = 600, monitorIndex = 1)
        Window(resized.toGrayscale().toBlackAndWhite(REDUCTION_DENSITY_THRESHOLD), "Game Image", x = 1200, y = 500, monitorIndex = 1)
        Window(
            resized.toGrayscale().applyConvolution(sobelKernelVerticalStarts),
            "Game Image",
            x = 1400,
            y = 400,
            monitorIndex = 1
        )
        Window(
            resized.toGrayscale().transpose().applyConvolution(sobelKernelVerticalStarts).transpose(),
            "Game Image",
            x = 1600,
            y = 300,
            monitorIndex = 1
        )


        val gameDebugImage =
            originalImage.alphaComposite(horizontalProofBarsThinStarts) //todo this should not be in game image
                .alphaComposite(verticalProofBarsThinStarts).alphaComposite(horizontalProofBarsThinEnds)
                .alphaComposite(verticalProofBarsThinEnds).cropXY( //todo do should be in constructor of GameImage
                    verticalbarStarts.first().center.toInt(),
                    verticalbarEnds.last().center.toInt(),
                    horizontalbarStarts.first().center.toInt(),
                    horizontalbarEnds.last().center.toInt()
                )

        Window(gameDebugImage, "Game DEBUG Image", x = 1100, y = 200, monitorIndex = 1)
    }

    val game = GameImage(
        gameImage,
        verticalBarStarts = verticalbarStarts.map { it.center - verticalbarStarts.first().center }.toDoubleArray(), // todo should be in constructor of gameImage
        verticalBarEnds = verticalbarEnds.map { it.center - verticalbarStarts.first().center }.toDoubleArray(), // todo should be in constructor of gameImage
        horizontalBarStarts = horizontalbarStarts.map { it.center - horizontalbarStarts.first().center }.toDoubleArray(), // todo should be in constructor of gameImage,
        horizontalBarEnds = horizontalbarEnds.map { it.center - horizontalbarStarts.first().center }.toDoubleArray() // todo should be in constructor of gameImage
        
    )

    System.setProperty("jna.library.path", "/opt/homebrew/lib")
    System.setProperty("TESSDATA_PREFIX", "/opt/homebrew/share")

    println("V ${verticalbarStarts.joinToString(", ")}")
    println("H ${horizontalbarStarts.joinToString(", ")}")

    println("GameImage Width: ${gameImage.width}, Height: ${gameImage.height})")
    println("Cells: X: ${game.width}, Y: ${game.height}")

    println("Detected game cells: ${game.rows} x ${game.columns}")



    game.getAllColumnClues()

    //game.getColumnClues(28)


    //game.getGameCell(7,2,0.85).convertToGrayscale().toBlackAndWhite(128).also { ImageIO.write(it, "png", File("bw_4.png"))}
}