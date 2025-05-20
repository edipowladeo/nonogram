package org.example.nonogram

import arrow.core.raise.Raise
import org.example.Clues
import  org.example.nonogram.Nonogram.NonogramCellState
import kotlin.math.max
import kotlin.math.min


object LineSolver{
    data class Bar(val start: Int, val end: Int){
        val length: Int = end - start + 1
        override fun toString(): String {
            return "Bar(start=$start, end=$end, length=$length)"
        }
    }

    sealed class Inconsistency(
        val reason: String,

    ) {
        class GameInconsistency(reason: String) : Inconsistency(reason)
        class UnexpectedInconsistency(reason: String) : Inconsistency(reason)

        fun concatenateLeft(text: String): Inconsistency {
            return when (this) {
                is GameInconsistency -> GameInconsistency("$text: $reason")
                is UnexpectedInconsistency -> UnexpectedInconsistency("$text: $reason")
            }
        }
    }

    data class MutableLines(
        private val _states: List<NonogramCellState>,
    ){
        private val mutableStates = _states.toMutableList()

        val length = _states.size

        val states: List<NonogramCellState>
            get() = mutableStates.toList()

        fun getState(i: Int): NonogramCellState {
            if (i < 0 || i >= length) { return NonogramCellState.EMPTY }

            return mutableStates[i]
        }

        fun setState(i: Int, state: NonogramCellState) {
            if (i >= 0 && i < length) {
                mutableStates[i] = state
            }
        }

    }


    data class Line (
        private val _states: List<NonogramCellState>,
        val clues: List<Int> = emptyList()){
        val length = _states.size

        val bars = gtBars() //todo remove

        companion object{
           fun fromString(string: String, clues: List<Int> = emptyList()) : Line {
                val states = string.map {
                    when (it) {
                        'X' -> NonogramCellState.EMPTY
                        '#' -> NonogramCellState.FILLED
                        '_' -> NonogramCellState.UNKNOWN
                        else -> throw IllegalArgumentException("Invalid character: $it")
                    }
                }
               return Line(states, clues)

        }
        }

        fun getState(i: Int): NonogramCellState {
            if (i < 0 || i >= length) { return NonogramCellState.EMPTY }

            return _states[i]
        }

        val states: List<NonogramCellState>
            get() = _states

        fun checkCollision(bar: Bar): Boolean {
            for (i in bar.start..bar.end) {
                if (getState(i) == NonogramCellState.EMPTY) {
                    return true
                }
            }
            if (getState(bar.start - 1) == NonogramCellState.FILLED) {
                return true
            }
            if (getState(bar.end + 1) == NonogramCellState.FILLED) {
                return true
            }

            return false
        }



        fun gtBars(): List<Bar> {
            val bars = mutableListOf<Bar>()
            var start = -1
            for (i in _states.indices) {
                if (_states[i] == NonogramCellState.FILLED) {
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
        fun print(): String {
            val sb = StringBuilder()
            for (state in _states) {
                when (state) {
                    NonogramCellState.EMPTY -> sb.append("X")
                    NonogramCellState.FILLED -> sb.append("#")
                    NonogramCellState.UNKNOWN -> sb.append("_")
                }
            }
            return sb.toString()
        }

        fun printWithClues(): String {
            return "${print()}  Clues: ${clues})}"
        }

    }




    fun Raise<Inconsistency>.solveLine(states: List<NonogramCellState>, clues: List<Int>, debug: Boolean = false):  Line {
      /*  val line = Line(states, clues)

        val firstClue = clues.firstOrNull() ?: {
         if (states.none { it == NonogramCellState.FILLED }) {
                return line
            } else {
             raise(Inconsistency.GameInconsistency("line is not empty"))
            }
        }
        val smallestAllowedPosition = (0 until line.length).firstOrNull{ p->
            !line.checkCollision(Bar(p, p + firstClue - 1))
            }?: raise(Inconsistency("No empty space found for clue #1: $firstClue"))


        val solvedStates = states.map { NonogramCellState.FILLED }



        if (debug) {
            println("Solving Line: \t${line.print()}, clues: $clues")
            println("Solved Line: \t${line.print()}")
            println("Bars: ${line.bars}")
            println("Smallest Allowed Position: $smallestAllowedPosition for clue $firstClue")
        }

        return Line(states,clues) // solvedStates*/
        TODO()
    }

    fun Raise<Inconsistency>.improveLine(line: Line, debug: Boolean = false): Line {


        fun placeClues(inputLine: Line): List<Bar> {
            val resultBars = mutableListOf<Bar>()
            var pos = 0
            val step = 1
            val clueList =  inputLine.clues

            for ((index, clue) in clueList.withIndex()) {
                var placed = false
                while (pos in 0 until inputLine.length) {
                    val end = pos + (clue - 1) * step
                    if (end !in 0 until inputLine.length) break

                    val range = pos..end
                    if (range.none { inputLine.getState(it) == NonogramCellState.EMPTY }) {
                        val before =  pos - 1
                        val after =  end + 1
                        if ((before !in inputLine.states.indices || inputLine.getState(before) != NonogramCellState.FILLED) &&
                            (after !in inputLine.states.indices || inputLine.getState(after) != NonogramCellState.FILLED)
                        ) {

                            resultBars.add(Bar(min(pos, end), max(pos, end)))
                            pos =  end + 2
                            placed = true
                            break
                        }
                    }
                    pos += step
                }

                if (!placed) {
                    raise(
                        Inconsistency.UnexpectedInconsistency(
                            "Failed to place clue #${index + 1} = $clue when scanning ${line.printWithClues()}"
                        )
                    )
                }
            }
            return resultBars.sortedBy { it.start }
        }

        fun placeCluesReversed(inputLine: Line): List<Bar> {
            val inputReversed = Line(inputLine.states.asReversed(), clues = inputLine.clues.asReversed())
            val resultReversed = placeClues(inputReversed)
            val lenght = inputLine.length
            val result = resultReversed.map {
                Bar(
                    start = lenght - it.end - 1,
                    end = lenght - it.start - 1
                )
            }
            return result.sortedBy { it.start }
        }


        // Tenta resolver da esquerda para direita e da direita para esquerda
        val leftToRight = placeClues(line)
        val rightToLeft = placeCluesReversed(line)

        if (debug) {
            println("Original: \t${line.print()}, clues: ${line.clues}")
            println("Left->Right: Size:${leftToRight.size} \t${leftToRight}")
            println("Right->Left: Size:${rightToLeft.size} \t${rightToLeft.joinToString()}")
        }
        if (leftToRight.size != rightToLeft.size) {
            raise(Inconsistency.UnexpectedInconsistency("Inconsistent placement: ${line.clues}")) //TODO improve error message
        }

        val improvedStates = MutableLines(line.states)

        leftToRight.zip(rightToLeft){
            leftBar, rightBar ->
            if (leftBar.length != rightBar.length) {
                raise(Inconsistency.UnexpectedInconsistency("Left to right and right to left returned different size bars")) //TODO improve error message
            }
            val intersectionBar = Bar(
                start = maxOf(leftBar.start, rightBar.start),
                end = minOf(leftBar.end, rightBar.end)
            )
            for (i in intersectionBar.start..intersectionBar.end) {
                improvedStates.setState(i ,NonogramCellState.FILLED)
            }

            if (leftBar.start == rightBar.start){
                improvedStates.setState(intersectionBar.start-1, NonogramCellState.EMPTY)
                improvedStates.setState(intersectionBar.end+1, NonogramCellState.EMPTY)
            }

        }


        if (debug) {
            println("Improved: \t${Line(improvedStates.states).print()}")
        }

        return Line(improvedStates.states, line.clues)


    }

}