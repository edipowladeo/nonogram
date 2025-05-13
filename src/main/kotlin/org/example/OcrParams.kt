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
     *  set to 0.0 to disable this filter **/
    const val MIN_DENSITY_THRESHOLD = 0.04

    /** when aggregating pixels for character boundary detection,
     * ignore all groups of pixels that are smaller than this percentage of the image size,
     * to avoid detection of grid lines as characters,
     * set this to 0.0 to disable this filter **/
    const val MIN_EXPECTED_HEIGHT = 0.0 //0.25
    const val MIN_EXPECTED_WIDTH = 0.15


}

object GameImageParams{
    /** Percentage of the image size that is considered a bounding box,
     * to avoid detection of grid lines as characters,
     * set this to 1.0 get exactly the calculated cell **/
    const val BOUNDING_BOX_SIZE = 0.85

    /** Clue cell background color, measured from a screenshot
    * used to determine if a cell is a clue cell **/
    val CLUE_CELL_BACKGROUND_COLOR = Color(236, 227, 180)

    /** Tolerance for the clue cell background color,
     * used to determine if a cell is a clue cell **/
    const val CLUE_CELL_COLOR_MATCH_TOLERANCE = 15 // TODO this is arbitrary, find a better way to calculate it

}