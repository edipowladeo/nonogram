package org.example

class Nonogram(
    val clues: Clues,
    val width: Int,
    val height: Int,
    val grid: Array<Array<NonogramCell>>,
) {
    enum class NonogramCell(i: Int) {
        EMPTY(0),
        FILLED(1),
        UNKNOWN(-1);

        companion object {
            fun fromInt(value: Int): NonogramCell {
                return when (value) {
                    0 -> EMPTY
                    1 -> FILLED
                    else -> UNKNOWN
                }
            }
        }
    }
}
