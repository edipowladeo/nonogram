package org.example

import org.example.configparams.DebugParams.DEBUG_MASTER
import org.example.configparams.DebugParams.DEBUG_NUMERAL_COMPARISONS
import org.example.configparams.DebugParams.DEBUG_NUMERAL_COMPARISONS_COLUMN_RANGE
import org.example.configparams.DebugParams.DEBUG_NUMERAL_COMPARISONS_ROW_RANGE
import org.example.bufferedImageExtensions.*
import org.example.configparams.GameImageParams
import org.example.ocr.NumeralOCR
import java.awt.image.BufferedImage


class GameImage(
    val image: BufferedImage,
    val verticalBarStarts: DoubleArray, //todo should use array<bar> ?
    val horizontalBarStarts: DoubleArray,
    val verticalBarEnds: DoubleArray,
    val horizontalBarEnds: DoubleArray,
) {
    companion object {
        val ocr = NumeralOCR()
    }

    private val filteredImage = image.toGrayscale().toBlackAndWhite(0.5)

    val width = verticalBarStarts.size - 1
    val height = horizontalBarStarts.size - 1

    fun isCellClue(x: Int, y: Int): Boolean {
        val cell = image.getGameCell(x, y)
        val filtered = cell.compareWithColor(
            targetColor = GameImageParams.CLUE_CELL_BACKGROUND_COLOR,
            tolerance = GameImageParams.CLUE_CELL_COLOR_MATCH_TOLERANCE
        )

        val blackPixels = filtered.countBlackPixels()
        val pixels = filtered.width * filtered.height
        val ratio = blackPixels.toDouble() / pixels.toDouble()

        return ratio > 0.5
    }

    private val rowClues = (0 until width)
        .firstOrNull { x -> isCellClue(x, 0) }
        ?: throw Exception("No index found for when scanning first row")

    private val columnClues = (0 until height)
        .firstOrNull { y -> isCellClue(0, y) }
        ?: throw Exception("No index found for when scanning first column")

    val columns = width - rowClues
    val rows = height - columnClues

    init {
        println("row clues: $rowClues, column clues: $columnClues")
        if ((verticalBarEnds.size != verticalBarStarts.size) or (horizontalBarEnds.size != horizontalBarStarts.size))
        {
            Window(image, "GAME FAILED TO LOAD", x = 1200, y = 100, monitorIndex = 1)
            throw Exception("Vertical and horizontal bar arrays must be of the same size")
        }

        Window(image.resize(.6), "GAME", x = 1200, y = 100, monitorIndex = 1)
    }

    fun BufferedImage.getGameCell(x: Int, y: Int, boundingBoxScale: Double = 1.0): BufferedImage {

        val x1 = verticalBarEnds[x].toInt() + GameImageParams.CROP_X_OFFSET
        val x2 = verticalBarStarts[x + 1].toInt() + GameImageParams.CROP_X_OFFSET
        val y1 = horizontalBarEnds[y].toInt() + GameImageParams.CROP_Y_OFFSET
        val y2 = horizontalBarStarts[y + 1].toInt() + GameImageParams.CROP_Y_OFFSET

        val xCenter = (x1 + x2) / 2
        val yCenter = (y1 + y2) / 2

        val x1scaled = ((x1 - xCenter) * boundingBoxScale + xCenter).toInt()
        val x2scaled = ((x2 - xCenter) * boundingBoxScale + xCenter).toInt()
        val y1scaled = ((y1 - yCenter) * boundingBoxScale + yCenter).toInt()
        val y2scaled = ((y2 - yCenter) * boundingBoxScale + yCenter).toInt()

        return cropXY(x1scaled, x2scaled, y1scaled, y2scaled)
    }

    private fun getColumnClueFilteredImage(column: Int, cluePosition: Int): BufferedImage {
        val x = rowClues + column
        val y = columnClues - cluePosition - 1
        require(x < width)
        require(y >= 0)
        return filteredImage.getGameCell(x, y, boundingBoxScale = GameImageParams.BOUNDING_BOX_SIZE)
    }

    private fun getColumnClues(column: Int): List<Int> {
        val results = mutableListOf<Int>()
        val debug = (DEBUG_MASTER && DEBUG_NUMERAL_COMPARISONS && (column in DEBUG_NUMERAL_COMPARISONS_COLUMN_RANGE))
        for (clue in 0 until columnClues) {
            val image = getColumnClueFilteredImage(column, clue)
            val result = ocr.interpretNumerals(image, debug) ?: break
            results.add(result)
        }
        return results.reversed()
    }

    fun getAllColumnClues(): List<List<Int>> {
        return (0 until columns).map { column ->
            getColumnClues(column).also {
                println("clues for column $column: ${it.joinToString(", ")}")
            }
        }
    }

    fun getAllRowClues():  List<List<Int>> {
        return (0 until rows).map { row ->
            getRowClues(row).also {
                println("clues for row $row: ${it.joinToString(", ")}")
            }
        }
    }

    private fun getRowClues(row: Int): List<Int> {
        val results = mutableListOf<Int>()
        val debug = (DEBUG_MASTER && DEBUG_NUMERAL_COMPARISONS && (row in DEBUG_NUMERAL_COMPARISONS_ROW_RANGE))
        for (clue in 0 until rowClues) {
            val image = getRowClueFilteredImage(row, clue)
            val result = ocr.interpretNumerals(image,debug) ?: break
            results.add(result)
        }
        return results.reversed()
    }

    private fun getRowClueFilteredImage(row: Int, cluePosition: Int): BufferedImage {
    val y = columnClues + row
        val x = rowClues - cluePosition - 1
        require(y < height)
        require(x >= 0)
        return filteredImage.getGameCell(x, y, boundingBoxScale = GameImageParams.BOUNDING_BOX_SIZE)
    }

}
