package org.example

class NonogramDrawer {

    fun drawNonogram(nonogram: Nonogram): String {
        val sb = StringBuilder()
        sb.append("Nonogram:\n")
        for (row in nonogram.grid) {
            for (cell in row) {
                when (cell) {
                    Nonogram.NonogramCell.EMPTY -> sb.append(" ")
                    Nonogram.NonogramCell.FILLED -> sb.append("#")
                    Nonogram.NonogramCell.UNKNOWN -> sb.append("?")
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}