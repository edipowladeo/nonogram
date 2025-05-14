package org.example

import org.example.nonogram.Nonogram

class NonogramDrawer {

    fun drawNonogram(nonogram: Nonogram): String {
        val sb = StringBuilder()
        sb.append("Nonogram:\n")
        for (row in nonogram.grid) {
            for (cell in row) {
                when (cell.state) {
                    Nonogram.NonogramCellState.EMPTY -> sb.append(" ")
                    Nonogram.NonogramCellState.FILLED -> sb.append("#")
                    Nonogram.NonogramCellState.UNKNOWN -> sb.append("?")
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}