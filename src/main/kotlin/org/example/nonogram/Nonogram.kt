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

    private fun generateLinePermutations(clue: List<Int>, length: Int): List<List<NonogramCellState>> {
        fun backtrack(index: Int, clueIndex: Int, current: MutableList<NonogramCellState>): List<List<NonogramCellState>> {
            if (clueIndex == clue.size) {
                // Fill remaining with EMPTY
                val filled = current + List(length - index) { NonogramCellState.EMPTY }
                return listOf(filled)
            }

            val permutations = mutableListOf<List<NonogramCellState>>()
            val blockLength = clue[clueIndex]

            // Try placing the block at different starting points
            for (i in index until (length - blockLength + 1)) {
                val newCurrent = current.toMutableList()
                // Fill with EMPTY up to i
                while (newCurrent.size < i) newCurrent.add(NonogramCellState.EMPTY)
                // Fill block with FILLED
                repeat(blockLength) { newCurrent.add(NonogramCellState.FILLED) }
                // Add separator EMPTY after block (if space remains)
                if (newCurrent.size < length) newCurrent.add(NonogramCellState.EMPTY)
                permutations += backtrack(newCurrent.size, clueIndex + 1, newCurrent)
            }

            return permutations
        }

        return backtrack(0, 0, mutableListOf()).filter { it.size == length }
    }

    private fun intersectLines(permutations: List<List<NonogramCellState>>): List<NonogramCellState> {
        if (permutations.isEmpty()) return emptyList()
        val length = permutations[0].size
        return (0 until length).map { i ->
            val allSame = permutations.all { it[i] == permutations[0][i] }
            if (allSame) permutations[0][i] else NonogramCellState.UNKNOWN
        }
    }

    fun solve() {
        reset()
        var changed: Boolean
        do {
            changed = false

            // Solve rows
            for (row in 0 until height) {
                val clueRow = clues.rows[row]
                val currentLine = grid[row].map { it.state }
                val permutations = generateLinePermutations(clueRow, width)
                    .filter { p -> currentLine.zip(p).all { (c, v) -> c == NonogramCellState.UNKNOWN || c == v } }

                val intersected = intersectLines(permutations)
                for (col in 0 until width) {
                    if (grid[row][col].state == NonogramCellState.UNKNOWN && intersected[col] != NonogramCellState.UNKNOWN) {
                        updateCell(row, col, NonogramCell(intersected[col]))
                        changed = true
                    }
                }
            }

            // Solve columns
            for (col in 0 until width) {
                val clueCol = clues.columns[col]
                val currentLine = (0 until height).map { grid[it][col].state }
                val permutations = generateLinePermutations(clueCol, height)
                    .filter { p -> currentLine.zip(p).all { (c, v) -> c == NonogramCellState.UNKNOWN || c == v } }

                val intersected = intersectLines(permutations)
                for (row in 0 until height) {
                    if (grid[row][col].state == NonogramCellState.UNKNOWN && intersected[row] != NonogramCellState.UNKNOWN) {
                        updateCell(row, col, NonogramCell(intersected[row]))
                        changed = true
                    }
                }
            }
        } while (changed)
    }

}
