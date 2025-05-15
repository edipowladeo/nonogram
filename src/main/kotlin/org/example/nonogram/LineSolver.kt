package org.example.nonogram

import arrow.core.raise.Raise


object LineSolver{
    data class Bar(val start: Int, val end: Int){
        val length: Int = end - start + 1
    }

    data class Line (
        val states: List<Nonogram.NonogramCellState>,
        val clues: List<Int>){
        val length = states.size

        val bars = gBars() //todo remove
        val freeBars = getUnknownBars() //todo remove

        fun getState(i: Int): Nonogram.NonogramCellState {
            if (i < 0 || i >= length) { return Nonogram.NonogramCellState.EMPTY }

            return states[i]
        }

        fun checkCollision(bar: Bar): Boolean {
            for (i in bar.start..bar.end) {
                if (getState(i) == Nonogram.NonogramCellState.EMPTY) {
                    return true
                }
            }
            if (getState(bar.start - 1) == Nonogram.NonogramCellState.FILLED) {
                return true
            }
            if (getState(bar.end + 1) == Nonogram.NonogramCellState.FILLED) {
                return true
            }

            return false
        }

        fun getUnknownBars(): List<Bar> {
            val unknownBars = mutableListOf<Bar>()
            var start = -1
            for (i in states.indices) {
                if (states[i] == Nonogram.NonogramCellState.UNKNOWN) {
                    if (start == -1) {
                        start = i
                    }
                } else {
                    if (start != -1) {
                        unknownBars.add(Bar(start, i - 1))
                        start = -1
                    }
                }
            }
            if (start != -1) {
                unknownBars.add(Bar(start, length - 1))
            }
            return unknownBars
        }

        fun gEmptyBars(): List<Bar> {
            val emptyBars = mutableListOf<Bar>()
            var start = -1
            for (i in states.indices) {
                if (states[i] == Nonogram.NonogramCellState.EMPTY) {
                    if (start == -1) {
                        start = i
                    }
                } else {
                    if (start != -1) {
                        emptyBars.add(Bar(start, i - 1))
                        start = -1
                    }
                }
            }
            if (start != -1) {
                emptyBars.add(Bar(start, length - 1))
            }
            return emptyBars
        }

        fun gBars(): List<Bar> {
            val bars = mutableListOf<Bar>()
            var start = -1
            for (i in states.indices) {
                if (states[i] == Nonogram.NonogramCellState.FILLED) {
                    if (start == -1) {
                        start = i
                    }
                } else {
                    if (start != -1) {
                        bars.add(Bar(start, i - 1))
                        start = -1
                    }
                }
            }
            if (start != -1) {
                bars.add(Bar(start, length - 1))
            }
            return bars
        }

        fun checkBars(bars: List<Bar>, clues: List<Int>): Boolean {
            if (bars.size != clues.size) {
                return false
            }
            for (i in bars.indices) {
                if (bars[i].length != clues[i]) {
                    return false
                }
            }
            return true
        }


    }

    fun printLine(line: Line): String {
        val sb = StringBuilder()
        for (state in line.states) {
            when (state) {
                Nonogram.NonogramCellState.EMPTY -> sb.append("X")
                Nonogram.NonogramCellState.FILLED -> sb.append("#")
                Nonogram.NonogramCellState.UNKNOWN -> sb.append("_")
            }
        }
        return sb.toString()
    }

    data class Inconsistency(
        val reason: String
    )
    fun Raise<Inconsistency>.solveLine(states: List<Nonogram.NonogramCellState>, clues: List<Int>, debug: Boolean = false):  Line {
        val line = Line(states, clues)

        val firstClue = clues.firstOrNull() ?: raise( Inconsistency("No clues found"))

        val smallestAllowedPosition = (0 until line.length).firstOrNull{ p->
            !line.checkCollision(Bar(p, p + firstClue - 1))
            }


        val solvedStates = states.map { Nonogram.NonogramCellState.FILLED }



        if (debug) {
            println("Solving Line: \t${printLine(line)}, clues: $clues")
            println("Solved Line: \t${printLine(line)}")
            println("Bars: ${line.bars}")
            println("Empty Bars: ${line.freeBars}")
            println("Smallest Allowed Position: $smallestAllowedPosition for clue $firstClue")
        }

        return Line(states,clues) // solvedStates
    }


}