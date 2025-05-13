package org.example

import org.example.bufferedImageExtensions.*
import org.example.ocr.NumeralOCR
import java.awt.Color
import java.awt.image.BufferedImage


class GameImage(
    val image: BufferedImage,
    val verticalBars: DoubleArray, //todo should use array<bar> ?
    val horizontalBars: DoubleArray
) {
    companion object{
        val ocr = NumeralOCR()
    }
    private val filteredImage = image.toGrayscale().toBlackAndWhite(brightnessThresold = 128)
    val width = verticalBars.size - 1
    val height = horizontalBars.size - 1

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
    private val rowClues = verticalBars
        .dropLast(1)
        .withIndex()
        .firstOrNull { (x, _) ->
            isCellClue(x,0)
        }?.index ?: throw Exception("No index found for when scanning first row")

    private val columnClues = horizontalBars
        .dropLast(1)
        .withIndex()
        .firstOrNull { (y, _) ->
            isCellClue(0,y)
        }?.index ?: throw Exception("No index found for when scanning first column")

    val columns = width - rowClues
    val rows = height - columnClues

    init {
        println("row clues: $rowClues, column clues: $columnClues")
        //Window(filteredImage.resize(0.5), "Filtered Image", y = 0, x = 0)
    }

    fun BufferedImage.getGameCell(x: Int, y: Int, boundingBoxScale: Double = 1.0): BufferedImage {

        val x1 = verticalBars[x].toInt()
        val x2 = verticalBars[x + 1].toInt()
        val y1 = horizontalBars[y].toInt()
        val y2 = horizontalBars[y + 1].toInt()

        val xCenter = (x1 + x2) / 2
        val yCenter = (y1 + y2) / 2

        val x1scaled = ((x1 - xCenter) * boundingBoxScale + xCenter).toInt()
        val x2scaled = ((x2 - xCenter) * boundingBoxScale + xCenter).toInt()
        val y1scaled = ((y1 - yCenter) * boundingBoxScale + yCenter).toInt()
        val y2scaled = ((y2 - yCenter) * boundingBoxScale + yCenter).toInt()

        return crop(x1scaled, x2scaled, y1scaled, y2scaled)
    }

    private fun getColumnClueFilteredImage(column: Int, cluePosition: Int): BufferedImage {
       val x = rowClues + column
        val y = columnClues - cluePosition - 1
        require(x < width)
        require(y >= 0)
        return filteredImage.getGameCell(x,y,boundingBoxScale= GameImageParams.BOUNDING_BOX_SIZE)
    }

    private fun getColumnClues(column: Int): List<Int> {
        val results = mutableListOf<Int>()
        for (clue in 0 until columnClues) {
            val image = getColumnClueFilteredImage(column, clue)
            val result = ocr.interpretNumeral(image) ?: break
            results.add(result)
        }
        return results
    }

    fun getAllColumnClues(): List<List<Int?>> {
      return (0 until columns).map { column ->
            getColumnClues(column).also {
                println("clues for column $column: ${it.joinToString(", ")}")
            }
        }
    }
}
