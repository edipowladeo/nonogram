package org.example.nonogram

import org.example.Clues

class Nonogram(
    val clues: Clues,
    val width: Int, //todo remove from constructor
    val height: Int,
    val grid: Array<Array<NonogramCell>>,  //todo check for bounds
) {
    val maxIterations = 1000
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

    val rowsToCheck: MutableList<Int> = listOf<Int>().toMutableList()
    val columnsToCheck: MutableList<Int> = listOf<Int>().toMutableList()

    fun solve(){
        reset()
        println("Solving")
        clues.columns.forEachIndexed { i,_->
            columnsToCheck.add(i)
        }
        clues.rows.forEachIndexed { i,_->
            rowsToCheck.add(i)
        }
        solveAllRows()
    }

    fun solveAllRows(){
        if (rowsToCheck.isEmpty()) return
        for ( i in 0 until maxIterations){
            if (rowsToCheck.isEmpty()) break
            solveRow(rowsToCheck.first())
        }
        solveAllColumns()
    }
    fun  solveAllColumns(){
        if (columnsToCheck.isEmpty()) return
        for ( i in 0 until maxIterations){
            if (columnsToCheck.isEmpty()) break
            solveColumn(columnsToCheck.first())
        }
        solveAllRows()
    }

    fun solveRow(index:Int){
        rowsToCheck.remove(index)
        solveLine(grid[index])
        println("Solved Row #${index}")
    }
    fun solveColumn(index: Int){
        columnsToCheck.remove(index)

        println("Solved Column #${index}")
    }

    fun solveLine(cells:Array<NonogramCell>){
    println("Solving Line ${printLine(cells)}")
    }

    fun printLine(cells: Array<NonogramCell>):String{
        val sb = StringBuilder()
            for (cell in cells) {
                when (cell.state) {
                    Nonogram.NonogramCellState.EMPTY -> sb.append(" ")
                    Nonogram.NonogramCellState.FILLED -> sb.append("#")
                    Nonogram.NonogramCellState.UNKNOWN -> sb.append("?")
                }
            }
        return sb.toString()
    }
}
