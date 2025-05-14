package org.example

import NonogramGUI
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import org.example.nonogram.Nonogram
import java.io.File
import javax.swing.SwingUtilities


    fun main() {
        SwingUtilities.invokeLater {
            // You can load these from file or generate them programmatically
            val loaded = Cbor.decodeFromByteArray<Clues>(File("clues.dat").readBytes())
            println("loaded")
            println(loaded)
            //game.getGameCell(7,2,0.85).convertToGrayscale().toBlackAndWhite(128).also { ImageIO.write(it, "png", File("bw_4.png"))}
            val nonogram = Nonogram(
                clues = loaded,
                width = loaded.columns.size,
                height = loaded.rows.size,
                grid = Array(loaded.rows.size) { Array(loaded.columns.size) {
                    Nonogram.NonogramCell(Nonogram.NonogramCellState.UNKNOWN) }}
            )
            val nonogramDrawer = NonogramDrawer()
            println(nonogramDrawer.drawNonogram(nonogram))

            val gui = NonogramGUI(nonogram)
            //   gui.colClues.first() = listOf(1, 1, 1, 1, 1)
            //   gui.setCellState(2,2, CellState.BLACK)
            nonogram.updateCell(2, 3, Nonogram.NonogramCell(Nonogram.NonogramCellState.FILLED))
            //gui.setCellState(2,2, CellState.BLACK)
            //gui.setCellState(2,3, CellState.BLACK)
            //gui.setCellState(2,4, CellState.BLACK)
            //gui.setCellState(2,5, CellState.BLACK)
            //gui.setCellState(2,6, CellState.BLACK)
            //gui.setCellState(2,7, CellState.BLACK)
            //gui.setCellState(2,8, CellState.BLACK)
            nonogram.solve()
        }
    }
