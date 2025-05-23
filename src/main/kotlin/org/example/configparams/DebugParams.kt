package org.example.configparams

object DebugParams {
    /** Debug mode, if true, will show debug windows for each step of the OCR process **/
    const val DEBUG_MASTER = false

    const val DEBUG_BOUNDARIES = true

    const val DEBUG_REMOVE_OUTLIERS = false
    const val FORCE_GLITCH = false
    const val DEBUG_VERTICAL_BARS = false
    const val DEBUG_HORIZONTAL_BARS = false
    const val DEBUG_NUMERALS = false
    const val DEBUG_NUMERAL_COMPARISONS = true

    val DEBUG_NUMERAL_COMPARISONS_COLUMN_RANGE = 0..1
    val DEBUG_NUMERAL_COMPARISONS_ROW_RANGE = 0 until 0

}