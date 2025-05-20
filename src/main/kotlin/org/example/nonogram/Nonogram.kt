package org.example.nonogram

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import org.example.Clues
import org.example.nonogram.LineSolver.improveLine
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
    val rowsToCheck: MutableSet<Int> = setOf<Int>().toMutableSet()
    val columnsToCheck: MutableSet<Int> = setOf<Int>().toMutableSet()

    val maxIterations = 1000

    interface NonogramChangeListener {
        fun onCellUpdated(row: Int, col: Int, state: NonogramCellState)
    }

    private val listeners = mutableListOf<NonogramChangeListener>()

    fun addListener(listener: NonogramChangeListener) {
        listeners += listener
    }

    fun updateCell(row: Int, col: Int, state: NonogramCellState) {
        doUpdateCell(row = row, col = col, state = state)
        rowsToCheck.add(row)
        columnsToCheck.add(col)
    }

    private fun doUpdateCell(row: Int, col: Int, state: NonogramCellState) {
        grid[row][col] = NonogramCell(state) // todo check for bounds
        listeners.forEach { it.onCellUpdated(row, col, state) }
      //  rowsToCheck.add(row)
      //  columnsToCheck.add(col)
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
                doUpdateCell(row, col, NonogramCellState.UNKNOWN)
            }
        }
        rowsToCheck.clear()
        columnsToCheck.clear()
    }

    fun forceCheck() {

        rowsToCheck.clear()
        columnsToCheck.clear()
        for (row in 0 until height) {
            rowsToCheck.add(row)
        }
        for (col in 0 until width) {
            columnsToCheck.add(col)
        }
    }


    fun solve( ) = either { solveAllRows(debug = true) }.onLeft {
        println(it.reason)
    }


    fun Raise<LineSolver.Inconsistency>.solveAllRows(debug: Boolean =false ) {
        if (rowsToCheck.isEmpty()) return
        for (i in 0 until maxIterations) {
            if (rowsToCheck.isEmpty()) break
            solveRow(rowsToCheck.first(),debug )
        }
        solveAllColumns(debug)
    }

    fun Raise<LineSolver.Inconsistency>.solveAllColumns(debug: Boolean =false ) {
        if (columnsToCheck.isEmpty()) return
        for (i in 0 until maxIterations) {
            if (columnsToCheck.isEmpty()) break
            solveColumn(columnsToCheck.first(),debug)
        }
        solveAllRows(debug)
    }

    fun Raise<LineSolver.Inconsistency>.solveRow(row: Int, debug: Boolean = false) {
        rowsToCheck.remove(row)
        val rowLine = grid[row].map { it.state }

       if (debug) {
          println("Solving Row #${row}")
        }

        val line = LineSolver.Line(rowLine, clues.rows[row])
        val solvedLine = either { improveLine(line, debug) }.mapLeft {
          it.concatenateLeft("Failed to solve Row #$row")
        }.bind()

        for (column in 0 until width) {
            val oldState = grid[row][column].state
            val newState = solvedLine.getState(column)
            if (oldState == newState) {
                continue
            }
            // todo add to columnsToCheck
            doUpdateCell(row = row, col = column, solvedLine.getState(column))
            columnsToCheck.add(column)
        }


    }


    fun Raise<LineSolver.Inconsistency>.solveColumn(col: Int, debug: Boolean =false ) {
        columnsToCheck.remove(col)
        val columnLine = List(height) { row -> grid[row][col].state }

        if (debug) {
            println("Solving Column #${col}")
        }
        // Solve the extracted column
        val line = LineSolver.Line(columnLine, clues.columns[col])
        val solvedLine = either { improveLine(line,debug) }.mapLeft {
            it.concatenateLeft("Failed to solve Column #$col")
        }.bind()

        // Write the solved column back into the grid
        for (row in 0 until height) {
            val oldState = grid[row][col].state
            val newState = solvedLine.getState(row)
            if (oldState == newState) {
                continue
            }

            // todo add to rowsToCheck
            doUpdateCell(row = row, col = col, solvedLine.getState(row))
            rowsToCheck.add(row)
        }
    }
}

