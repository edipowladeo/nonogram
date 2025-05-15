package org.example.nonogram



object LineSolver{

    fun printLine(states: List<Nonogram.NonogramCellState>): String {
        val sb = StringBuilder()
        for (state in states) {
            when (state) {
                Nonogram.NonogramCellState.EMPTY -> sb.append(" ")
                Nonogram.NonogramCellState.FILLED -> sb.append("#")
                Nonogram.NonogramCellState.UNKNOWN -> sb.append("?")
            }
        }
        return sb.toString()
    }

    fun solveLine(states: List<Nonogram.NonogramCellState>, clues: List<Int>): List<Nonogram.NonogramCellState> {

        val solvedStates = states.map { Nonogram.NonogramCellState.FILLED }
        println("Solving Line ${printLine(states)}, clues: $clues")
        println("Solved Line ${printLine(solvedStates)}")
        return solvedStates
    }
}