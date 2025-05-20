package org.example.nonogram

import arrow.core.raise.either
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

import org.example.nonogram.LineSolver.improveLine
import kotlin.test.assertTrue
import kotlin.test.fail

class LineSolverTest {
    val debug  = true
    @Test
    fun `test improveLine with one simple clues greater than half a line`() {
        val line = LineSolver.Line.fromString(
            string = "_____",
            clues = listOf(3)
        )
        val result = either {  improveLine(line, debug)}

        result.fold(
            { fail("Expected result to be Right")},
            { line ->
                assertEquals(
                    "__#__",
                    line.print()
                )
            }
        )
    }

    @Test
    fun `test improveLine with one simple clues smaller than half a line`() {
        val line = LineSolver.Line.fromString(
            string = "_______",
            clues = listOf(3)
        )
        val result = either {  improveLine(line,debug)}

        result.fold(
            { fail("Expected result to be Right")},
            { line ->
                assertEquals(
                    "_______",
                    line.print()
                )
            }
        )
    }

    @Test
    fun `test improveLine with one simple clues smaller than half of available space`() {
        val line = LineSolver.Line.fromString(
            string = "_X_____",
            clues = listOf(4)
        )
        val result = either {  improveLine(line,debug)}

        result.fold(
            { fail("Expected result to be Right")},
            { line ->
                assertEquals(
                    "_X_###_",
                    line.print()
                )
            }
        )
    }

    @Test
    fun `When 2 clues, each one fits in one space`() {
        val line = LineSolver.Line.fromString(
            string = "____X_______",
            clues = listOf(3,4)
        )
        val result = either {  improveLine(line,debug)}

        result.fold(
            { fail("Expected result to be Right")},
            { line ->
                assertEquals(
                    "_##_X___#___",
                    line.print()
                )
            }
        )
    }


    @Test
    fun `When 2 clues, but 3 spaces`() {
        val line = LineSolver.Line.fromString(
            string = "____X____X_______",
            clues = listOf(3,5)
        )
        val result = either {  improveLine(line,debug)}

        result.fold(
            { fail("Expected result to be Right")},
            { line ->
                assertEquals(
                    "____X____X__###__",
                    line.print()
                )
            }
        )
    }


    @Test
    fun `When 2 clues, but 3 spaces, and only middle one fits right clue`() {
        val line = LineSolver.Line.fromString(
            string = "____X_______X____",
            clues = listOf(3,5)
        )
        val result = either {  improveLine(line,debug)}

        result.fold(
            { fail("Expected result to be Right")},
            { line ->
                assertEquals(
                    "_##_X__###__X____",
                    line.print()
                )
            }
        )
    }

    @Test
    fun `When 2 clues overlap, but does not determine nothing`() {
        val line = LineSolver.Line.fromString(
            string = "__________",
            clues = listOf(2,3)
        )
        val result = either {  improveLine(line,debug)}

        result.fold(
            { fail("Expected result to be Right")},
            { line ->
                assertEquals(
                    "__________",
                    line.print()
                )
            }
        )
    }

}