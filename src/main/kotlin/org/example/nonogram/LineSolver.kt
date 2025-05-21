package org.example.nonogram

import arrow.core.raise.Raise
import  org.example.nonogram.Nonogram.NonogramCellState
import kotlin.math.max

/** todos
 * make all line empty when some bars are determined
 * ____##_____ Clues(4) ->
 * XX__##__XXX
 *
 * _____###________________###_______ Clues (4,4,2) ->
 * XXXX_###_XXXXXXXXXXXXXX_###_______
 *
 * fill when minimum bar lenght is determined
 * _________X_###________________________________#_________ Clues(6, 10) ->
 * _________X_#####______________________________#_________
 *
 *
 */
object LineSolver{

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

        fun checkCollision(bar: IntBar): Boolean {
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



        fun getExistingBars(): IntBars {
            val bars = mutableListOf<IntBar>()
            var start = -1
            for (i in _states.indices) {
                if (_states[i] == NonogramCellState.FILLED) {
                    if (start == -1) {
                        start = i
                    }
                } else {
                    if (start != -1) {
                        bars.add(IntBar(start, i - 1))
                        start = -1
                    }
                }
            }
            if (start != -1) {
                bars.add(IntBar(start, length - 1))
            }
            return IntBars(bars)
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

    /*** Try to place clues on its leftmost possible location
     * Example: clues (3, 4)
     * __X_____X_______ ->
     * __X###__X####___
     * */
    fun Raise<Inconsistency>.placeCluesLeft(inputLine: Line, debug: Boolean = false): IntBars {
        val resultBars = mutableListOf<IntBar>()
        var start = 0

        val clueList =  inputLine.clues

        for ((index, clue) in clueList.withIndex()) {
            var placed = false
            if (debug) println("CLUES LEFT: start: $start, clue: $clue")
            while (start < inputLine.length) {
                val end = start + (clue - 1)
                if (end !in 0 until inputLine.length) break

                val range = start..end
                if (range.none { inputLine.getState(it) == NonogramCellState.EMPTY }) {
                    val before =  start - 1
                    val after =  end + 1
                    if ((before !in inputLine.states.indices || inputLine.getState(before) != NonogramCellState.FILLED) &&
                        (after !in inputLine.states.indices || inputLine.getState(after) != NonogramCellState.FILLED)
                    ) {

                        resultBars.add(IntBar(start, end))
                        start =  end + 2
                        placed = true
                        break
                    }
                }
                start += 1
            }

            if (!placed) {
                raise(
                    Inconsistency.UnexpectedInconsistency(
                        "Failed to left place clue #${index} = $clue when scanning ${inputLine.printWithClues()}"
                    )
                )
            }
        }
        return IntBars(resultBars)
    }

    /*** Try to anchor clues on its leftmost possible location
     * Example: clues (3, 4, 5)
     * _________X_#_______#_#_ ->
     * ###______X####___#####_
     * */
    fun  Raise<Inconsistency>.anchorCluesLeft(inputLine: Line, debug: Boolean = false): IntBars {
        val reversedClueList =  inputLine.clues.reversed()
        val resultBars = mutableListOf<IntBar>()
        val existingBars = inputLine.getExistingBars()


        val maxEnd = inputLine.length - 1
        var lastBarStart = maxEnd + 2


        for ((index, clue) in reversedClueList.withIndex()) {

            val intersectBars = existingBars.bars.filter { it.end < (lastBarStart - 1) }
            var placed = false



            val minEnd = intersectBars.lastOrNull()?.end ?: (clue - 1)
            val minStart = max(minEnd - (clue - 1),0)

            val maxStart = maxEnd - (clue - 1)

          if (debug)  println("ANCHOR LEFT: minEnd: ${minEnd}, minStart: ${minStart}, clue: $clue, maxEnd:$maxEnd maxStart: $maxStart")
            if (maxStart < 0) {
                raise(
                    Inconsistency.GameInconsistency(
                        "Failed to left anchor clue #${index} = $clue, maxStart < 0 when scanning ${inputLine.printWithClues()}"
                    )
                )
            }

            var start = minStart




            while (start <= maxStart) {
                val end = start + (clue - 1)
                val range = start..end
                if (range.none { inputLine.getState(it) == NonogramCellState.EMPTY }) {
                    val before =  start - 1
                    val after =  end + 1
                    if ((before !in inputLine.states.indices || inputLine.getState(before) != NonogramCellState.FILLED) &&
                        (after !in inputLine.states.indices || inputLine.getState(after) != NonogramCellState.FILLED)
                    ) {
                        if (debug)     println("ANCHOR LEFT: Placing clue #${index} = $clue anchor at $start")
                        resultBars.add(IntBar(start, end))
                      //  maxEnd = start - 2
                        lastBarStart = start
                        placed = true
                        break
                    }
                }
                start += 1
            }

            if (!placed) {
//                throw Exception()
                raise(
                    Inconsistency.UnexpectedInconsistency(
                        "Failed to left anchor clue #${index} = $clue when scanning ${inputLine.printWithClues()}"
                    )
                )
            }
        }
        return IntBars(resultBars.reversed())
    }


    fun Raise<Inconsistency>.getLeftMostPositions(line: Line): IntBars{
        val leftMostPositions = placeCluesLeft(line)
        val leftAnchors = anchorCluesLeft(line)

        if (leftMostPositions.size != leftAnchors.size){
            raise(Inconsistency.UnexpectedInconsistency("clue placement and anchors returned different number of bars")) //TODO improve error message
        }

        val actualLeftMostPositions = leftMostPositions.bars.zip(leftAnchors.bars) { leftMost, leftAnchor ->
            if (leftMost.length != leftAnchor.length) {
                raise(Inconsistency.UnexpectedInconsistency("clue placement and anchors returned different size bars")) //TODO improve error message
            }
            IntBar.fromStartAndLength(
                start = maxOf(leftMost.start, leftAnchor.start),
                length = leftMost.length
            )
        }
        return IntBars(actualLeftMostPositions)
    }

    fun Raise<Inconsistency>.getRightMostPositions(line: Line): IntBars{
        val inputReversed = Line(line.states.asReversed(), clues = line.clues.asReversed())
        val  resultReversed = getLeftMostPositions(inputReversed)
        val length = line.length
        val result = resultReversed.bars.map {
            IntBar(
                start = length - it.end - 1,
                end = length - it.start - 1
            )
        }
        return IntBars(result.reversed())

    }

    fun Raise<Inconsistency>.improveLine(line: Line, debug: Boolean = false): Line {

        val leftMostPositions = getLeftMostPositions(line)
        val rightMostPositions = getRightMostPositions(line)


        if (debug) {
            println("Original: \t${line.print()}, clues: ${line.clues}")
            println("LeftPos:    \tSize:${leftMostPositions.size} \t${leftMostPositions}")
            println("RightPos:   \tSize:${rightMostPositions.size} \t${rightMostPositions}")
        }

        if (leftMostPositions.size != rightMostPositions.size) {
            raise(Inconsistency.UnexpectedInconsistency("leftMostPositions: ${leftMostPositions.size} != rightMostPositions ${rightMostPositions.size}  placement: ${line.clues}")) //TODO improve error message
        }
        if (leftMostPositions.size != line.clues.size) {
            raise(Inconsistency.UnexpectedInconsistency("lefmostpostions ${leftMostPositions.size} != clues.size ${line.clues.size}")) //TODO improve error message
        }

        val improvedStates = MutableLines(line.states)



       val intersectionBars =  leftMostPositions.bars.zip(rightMostPositions.bars){
            leftBar, rightBar ->
            if (leftBar.length != rightBar.length) {
                raise(Inconsistency.UnexpectedInconsistency("Left to right and right to left returned different size bars")) //TODO improve error message
            }
           val intersectionBar =  IntBar(
                start = maxOf(leftBar.start, rightBar.start),
                end = minOf(leftBar.end, rightBar.end)
            )

           if (leftBar.start == rightBar.start){
               improvedStates.setState(intersectionBar.start-1, NonogramCellState.EMPTY)
               improvedStates.setState(intersectionBar.end+1, NonogramCellState.EMPTY)
           }
           intersectionBar
       }

        for (i in 0 until leftMostPositions.bars.first().start) {
            improvedStates.setState(i, NonogramCellState.EMPTY)
        }

        // 2. After last bar
        for (i in rightMostPositions.bars.last().end + 1 until line.length) {
            improvedStates.setState(i, NonogramCellState.EMPTY)
        }

        // 3. Between right end of bar N and left start of bar N+1
        for (i in 0 until leftMostPositions.size - 1) {
            val endOfPrev = rightMostPositions.bars[i].end
            val startOfNext = leftMostPositions.bars[i + 1].start
            for (j in endOfPrev + 1 until startOfNext) {
                improvedStates.setState(j, NonogramCellState.EMPTY)
            }
        }

        //fill intersections
        intersectionBars.forEach {   intersectionBar->
            for (i in intersectionBar.start..intersectionBar.end) {
            improvedStates.setState(i ,NonogramCellState.FILLED)
        }

            //match islands
            val improvedLine1 = Line(improvedStates.states, line.clues)
            val existingBars = improvedLine1.getExistingBars()

            existingBars.bars.forEach{ existingBar ->
                val indexedClues = improvedLine1.clues.mapIndexed{i, clue -> Pair(i, clue)}
                println(indexedClues)

                val possibleClues = indexedClues.filter { (index, clue) ->
                    val leftMost = leftMostPositions.bars[index]
                    val rightMost = rightMostPositions.bars[index]
                    clue >= existingBar.length &&
                            existingBar.start >= leftMost.start && existingBar.end <= rightMost.end
                }.map { it.first }

            }

        }


        if (debug) {
            println("Improved: \t${Line(improvedStates.states).print()}")
        }

        return Line(improvedStates.states, line.clues)


    }

}