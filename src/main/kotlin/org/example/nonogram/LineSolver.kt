package org.example.nonogram

import arrow.core.raise.Raise
import org.example.Clues
import  org.example.nonogram.Nonogram.NonogramCellState
import kotlin.math.max
import kotlin.math.min


object LineSolver{
    data class Bar(val start: Int, val end: Int){
        val length: Int = end - start + 1
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


        // Função auxiliar para tentar preencher a linha em uma direção
        fun placeClues(inputLine: Line, fromLeft: Boolean): List<Bar> {

            val result = MutableList(inputLine.length) { NonogramCellState.UNKNOWN }
            var pos = if (fromLeft) 0 else inputLine.length - 1
            val step = if (fromLeft) 1 else -1
            val clueIterator = if (fromLeft) inputLine.clues.iterator() else inputLine.clues.asReversed().iterator()

            val resultBars = mutableListOf<Bar>()
            while (clueIterator.hasNext()) {
                val clue = clueIterator.next()
                var validPos = false

                // Procura uma posição válida para o bloco atual
                while (pos >= 0 && pos < line.length) {
                    val end = pos + (clue - 1) * step
                    if (end < 0 || end >= line.length) break

                    val range = if (fromLeft) pos..end else end..pos
                    // Verifica se podemos colocar o bloco nesta posição
                    if (range.none { inputLine.getState(it) == NonogramCellState.EMPTY }) {
                        // Verifica se não viola regras de separação
                        val before = if (fromLeft) pos - 1 else pos + 1
                        val after = if (fromLeft) end + 1 else end - 1
                        if ((before < 0 || inputLine.getState(before) != NonogramCellState.FILLED) &&
                            (after >= line.length || inputLine.getState(after) != NonogramCellState.FILLED)) {
                            validPos = true
                            // Preenche o bloco


                            resultBars.add(Bar(min(pos,end), max(pos,end)))
                            pos = if (fromLeft) end + 2 else end - 2
                            break
                        }
                    }
                    pos += step
                }
                if (!validPos) break
            }
            return resultBars.sortedBy { it.start }
        }

        // Tenta resolver da esquerda para direita e da direita para esquerda
        val leftToRight = placeClues(line, true)
        val rightToLeft = placeClues(line, false)

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
            println("Original: \t${line.print()}, clues: ${line.clues}")
            println("Left->Right: \t${leftToRight}")
            println("Right->Left: \t${rightToLeft}")
            //println("Left->Right: \t${Line(leftToRight, line.clues).print()}")
            //println("Right->Left: \t${Line(rightToLeft).print()}")
            println("Improved: \t${Line(improvedStates.states).print()}")
        }

        return Line(improvedStates.states, line.clues)


    }

}