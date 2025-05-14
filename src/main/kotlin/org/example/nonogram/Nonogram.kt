package org.example.nonogram

import org.example.Clues

class Nonogram(
    val clues: Clues,
    val width: Int, //todo remove from constructor
    val height: Int,
    val grid: Array<Array<NonogramCell>>,  //todo check for bounds
) {
    interface NonogramChangeListener {
        fun onCellUpdated(row: Int, col: Int, cell: NonogramCell)
    }

    private val listeners = mutableListOf<NonogramChangeListener>()

    fun addListener(listener: NonogramChangeListener) {
        listeners += listener
    }

    fun updateCell(row: Int, col: Int, cell: NonogramCell) {
        grid[row][col] = cell // todo check for bounds
        listeners.forEach { it.onCellUpdated(row, col, cell) }
    }

    class NonogramCell(
        val state: NonogramCellState
    )


    enum class NonogramCellState(i: Int) {
        EMPTY(0),
        FILLED(1),
        UNKNOWN(-1);

        companion object {
            fun fromInt(value: Int): NonogramCellState {
                return when (value) {
                    0 -> EMPTY
                    1 -> FILLED
                    else -> UNKNOWN
                }
            }
        }
    }

    fun reset() {
        for (row in grid.indices) {
            for (col in grid[row].indices) {
               updateCell(row,col, NonogramCell(NonogramCellState.UNKNOWN))
            }
        }
    }

    fun solve(){
        reset()

    }
}
