package org.example.nonogram

import arrow.core.raise.either
import org.junit.jupiter.api.Assertions.assertEquals

import org.example.nonogram.LineSolver.improveLine
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.fail

class LineSolverTest {
    val debug = true

    enum class Direction(val flag: Boolean) {
        FORWARD(true),
        BACKWARD(false)
    }

    fun assertDirection(lineBeforeTest: String, expectedResult: String, clues: List<Int>, direction: Direction) {
        val line = LineSolver.Line.fromString(string = lineBeforeTest, clues = clues)
        val reverseLine = LineSolver.Line.fromString(string = lineBeforeTest.reversed(), clues = clues.reversed())
        when (direction) {
            Direction.FORWARD -> assertLine(line, expectedResult)
            Direction.BACKWARD -> assertLine(reverseLine, expectedResult.reversed())
        }
    }

    fun assertLine(line: LineSolver.Line, expectedResult: String) {
        val result = either { improveLine(line, debug) }
        result.fold(
            { fail(it.reason) },
            { line ->
                assertEquals(
                    expectedResult,
                    line.print()
                )
            }
        )
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test improveLine with one simple clues greater than half a line`(direction: Direction) {
        val clues = listOf(3)
        val lineBeforeTest = "_____"
        val expectedResult = "__#__"
        assertDirection(lineBeforeTest, expectedResult, clues, direction)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test improveLine with one simple clues smaller than half a line`(direction: Direction) {
        val clues = listOf(3)
        val lineBeforeTest = "_______"
        val expectedResult = "_______"
        assertDirection(lineBeforeTest, expectedResult, clues, direction)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test improveLine with one simple clues smaller than half of available space`(direction: Direction) {
        val clues = listOf(4)
        val lineBeforeTest = "_X_____"
        val expectedResult = "_X_###_"
        assertDirection(lineBeforeTest, expectedResult, clues, direction)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `When 2 clues, each one fits in one space`(direction: Direction) {
        val clues = listOf(3, 4)
        val lineBeforeTest = "____X_______"
        val expectedResult = "_##_X___#___"
        assertDirection(lineBeforeTest, expectedResult, clues, direction)
    }


    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `When 2 clues, but 3 spaces`(direction: Direction) {
        val clues = listOf(3, 5)
        val lineBeforeTest = "____X____X_______"
        val expectedResult = "____X____X__###__"
        assertDirection(lineBeforeTest, expectedResult, clues, direction)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `When 2 clues, but 3 spaces, and only middle one fits right clue`(direction: Direction) {
        val clues = listOf(3, 5)
        val lineBeforeTest = "____X_______X____"
        val expectedResult = "_##_X__###__X____"
        assertDirection(lineBeforeTest, expectedResult, clues, direction)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `When 2 clues overlap, but does not determine nothing`(direction: Direction) {
        val clues = listOf(2, 3)
        val lineBeforeTest = "__________"
        val expectedResult = "__________"
        assertDirection(lineBeforeTest, expectedResult, clues, direction)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `When line has a filed point near the corner and a big clue exists`(direction: Direction) {
        val clues = listOf(4)
        val lineBeforeTest = "_#________"
        val expectedResult = "_###______"
        assertDirection(lineBeforeTest, expectedResult, clues, direction)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `column 5`(direction: Direction) {
        val clues = listOf(2, 2, 2, 1, 1, 3, 1, 1, 1, 3)
        val lineBeforeTest = "_________________#____________"
        val expectedResult = "_________________#____________"
        assertDirection(lineBeforeTest, expectedResult, clues, direction)
    }
}