package org.example

import java.awt.Color

object OcrParams {
    /** characters will be scaled to this height in pixels before comparison,
     * keep low for performance
     */
    const val COMPARISON_IMAGE_HEIGHT = 50.0

    /** the character must be at least this thick compared to the image size
     *  its thinnest part to be considered a continuous character,
     *  to avoid detecting noise pixels as characters,
     *  set to 1.0 to disable this filter **/
    val MIN_DENSITY_THRESHOLD = if (DebugParams.FORCE_GLITCH) 1.00 else 0.96 //todo  check if 0.94 works for 30x30

    /** when aggregating pixels for character boundary detection,
     * ignore all groups of pixels that are smaller than this percentage of the image size,
     * to avoid detection of grid lines as characters,
     * set this to 0.0 to disable this filter **/
    val MIN_EXPECTED_HEIGHT = if (DebugParams.FORCE_GLITCH) 0.00 else 0.25
    const val MIN_EXPECTED_WIDTH = 0.15

    const val PADDING_PIXELS = 1


}

object GameImageParams {
    const val REDUCTION_DENSITY_THRESHOLD = 0.70 //todo Rename

    /** Percentage of the image size that is considered a bounding box,
     * to avoid detection of grid lines as characters,
     * set this to 1.0 get exactly the calculated cell **/
    val BOUNDING_BOX_SIZE = 1.0//if (DebugParams.FORCE_GLITCH) 0.90 else 0.85

    val CROP_X_OFFSET = 1
    val CROP_Y_OFFSET = 1

    /** Clue cell background color, measured from a screenshot
     * used to determine if a cell is a clue cell **/
    val CLUE_CELL_BACKGROUND_COLOR = Color(236, 227, 180)

    /** Tolerance (0-255) for the clue cell background color,
     * used to determine if a cell is a clue cell **/
    const val CLUE_CELL_COLOR_MATCH_TOLERANCE = 15 // TODO this is arbitrary, find a better way to calculate it
}

object DebugParams {
    /** Debug mode, if true, will show debug windows for each step of the OCR process **/
    const val DEBUG_MODE = true
    const val DEBUG_BOUNDARIES = true
    const val FORCE_GLITCH = false
    const val DEBUG_VERTICAL_BARS = false
    const val DEBUG_HORIZONTAL_BARS = true
    const val DEBUG_NUMERALS = false
    const val DEBUG_NUMERAL_COMPARISONS = true

    /** Debug mode, if true, will show debug windows for each step of the OCR process **/
    const val DEBUG_MODE_OCR = false

    /** Debug mode, if true, will show debug windows for each step of the OCR process **/
    const val DEBUG_MODE_GAME_IMAGE = false
}