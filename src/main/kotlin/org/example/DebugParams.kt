package org.example

object DebugParams {
    /** Debug mode, if true, will show debug windows for each step of the OCR process **/
    const val DEBUG_MASTER = true

    const val DEBUG_BOUNDARIES = true

    const val DEBUG_REMOVE_OUTLIERS = false
    const val FORCE_GLITCH = false
    const val DEBUG_VERTICAL_BARS = true
    const val DEBUG_HORIZONTAL_BARS = true
    const val DEBUG_NUMERALS = false
    const val DEBUG_NUMERAL_COMPARISONS = true

    val DEBUG_NUMERAL_COMPARISONS_COLUMN_RANGE = 1..2
    val DEBUG_NUMERAL_COMPARISONS_ROW_RANGE = 0 until 0

}