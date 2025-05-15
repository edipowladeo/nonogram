package org.example.nonogram

import org.example.Clues
import org.example.nonogram.LineSolver.solveLine

class Nonogram(
    val clues: Clues,
) {
    val width = clues.columns.size
    val height = clues.rows.size
    // todo grid is mutable
    // list of Rows , each row is a array
    val grid: List<Array<NonogramCell>> = List(clues.rows.size) {
        Array(clues.columns.size) { NonogramCell(Nonogram.NonogramCellState.UNKNOWN) }
    }
    val maxIterations = 1000

    interface NonogramChangeListener {
        fun onCellUpdated(row: Int, col: Int, state: NonogramCellState)
    }

    private val listeners = mutableListOf<NonogramChangeListener>()

    fun addListener(listener: NonogramChangeListener) {
        listeners += listener
    }

    fun updateCell(row: Int, col: Int, state: NonogramCellState) {
        grid[row][col] = NonogramCell(state) // todo check for bounds
        listeners.forEach { it.onCellUpdated(row, col, state) }
    }

    class NonogramCell(
        val state: NonogramCellState
    )


    enum class NonogramCellState(i: Int) {
        EMPTY(0), FILLED(1), UNKNOWN(-1);

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
                updateCell(row, col, NonogramCellState.UNKNOWN)
            }
        }
    }

    val rowsToCheck: MutableList<Int> = listOf<Int>().toMutableList()
    val columnsToCheck: MutableList<Int> = listOf<Int>().toMutableList()

    fun solve() {
        reset()
        println("Solving")
        println("columns: ${clues.columns.size}, rows: ${clues.rows.size}")
        clues.columns.forEachIndexed { i, _ ->
            columnsToCheck.add(i)
        }
        clues.rows.forEachIndexed { i, _ ->
            rowsToCheck.add(i)
        }
        solveAllRows()
    }

    fun solveAllRows() {
        if (rowsToCheck.isEmpty()) return
        for (i in 0 until maxIterations) {
            if (rowsToCheck.isEmpty()) break
            solveRow(rowsToCheck.first())
        }
        solveAllColumns()
    }

    fun solveAllColumns() {
        if (columnsToCheck.isEmpty()) return
        for (i in 0 until maxIterations) {
            if (columnsToCheck.isEmpty()) break
            solveColumn(columnsToCheck.first())
        }
        solveAllRows()
    }

    fun solveRow(row: Int) {
        rowsToCheck.remove(row)
        val rowLine = grid[row].map { it.state }
        val solvedLine = solveLine(rowLine, clues.rows[row])

        for (column in 0 until width ) {
            updateCell(row = row, col = column, solvedLine[column])
        }

        println("Solved Row #${row}")
    }

    fun solveColumn(col: Int) {
        columnsToCheck.remove(col)
        val columnLine = List(height) { row -> grid[row][col].state }

        // Solve the extracted column
        val solvedLine = solveLine(columnLine, clues.columns[col])

        // Write the solved column back into the grid
        for (row in 0 until height ) {
            updateCell(row = row, col = col, solvedLine[row])
        }

        println("Solved Column #${col}")
    }




}

