package org.example.arithmetic

import org.example.Window
import org.example.bufferedImageExtensions.drawVerticalBars
import org.example.bufferedImageExtensions.resize

fun Array<Bar>.removeOutliersFromArithmeticProgression(
    toleranceRatio: Double = 0.2,
    debug: Boolean = false
): Array<Bar> {
    val newArray = this.removeGreatestOutlierFromArithmeticProgression(toleranceRatio,debug);
    if (debug) {
        val proofBars =
            drawVerticalBars(newArray, 3000).resize(height = 500)
        Window(proofBars, "Debug Remove outliers: Size: ${this.size}", y = (50-this.size)*30,  monitorIndex = 1)
    }
    if (newArray.size < this.size) {
       if (debug) println("Removed outlier, new size: ${ newArray.size}")
        return newArray.removeOutliersFromArithmeticProgression(toleranceRatio,debug);
    }

    return this
}

fun Array<Bar>.removeGreatestOutlierFromArithmeticProgression(toleranceRatio: Double, debug: Boolean = false): Array<Bar> {
    if (this.size <= 2) return this // Not enough points to evaluate

    val sortedBars = this.sortedBy { it.center }
    val gaps = List(sortedBars.size) { i ->
        when (i) {
            0 -> (sortedBars[1].center - sortedBars[0].center)
            sortedBars.lastIndex -> (sortedBars[i].center - sortedBars[i - 1].center)
            else -> (sortedBars[i + 1].center - sortedBars[i - 1].center) / 2.0
        }
    }


    val modeGap = gaps.calculateMode(1.5)

    val tolerance = modeGap * toleranceRatio

    // Compute absolute deviation from median
    val deviations = gaps.mapIndexed { i, gap ->
        Pair(i, kotlin.math.abs(gap - modeGap))
    }

    // Filter candidates beyond 10% deviation
    val outliers = deviations.filter { (_, dev) -> dev > tolerance }
    val debug_detailed =  (this.size == 44)

    if (debug_detailed) println("\tOutliers: $outliers")
    if (debug_detailed){
        println("Sorted Bars: ${sortedBars.map { it.center }}")
        println("${this.size}\tGaps: $gaps")
        println("\rGaps Sorted: ${gaps.sorted()}")
        println("\tmedianGap: $modeGap, tolerance: $tolerance")

        println("\tOutliers: $outliers")
    }
    if (outliers.isEmpty()) return sortedBars.toTypedArray()



    val outlierToBeRemoved =  outliers.minBy {
        val indexToRemove = it.first
        val newBars = sortedBars.filterIndexed { i, _ -> i != indexToRemove }.toTypedArray()
        val newGaps = List(newBars.size) { i ->
            when (i) {
                0 -> (newBars[1].center - newBars[0].center)
                newBars.lastIndex -> (newBars[i].center - newBars[i - 1].center)
                else -> (newBars[i + 1].center - newBars[i - 1].center) / 2.0
            }
        }
        val newDeviations = List(newBars.size) { i ->
                val leftSpacing = when (i) {
                    0 -> 0.0
                    else -> (newBars[i].center - newBars[i - 1].center)
                }
                val rightSpacing = when (i) {
                    newBars.lastIndex -> 0.0
                    else -> (newBars[i + 1].center - newBars[i].center)
                }
                val leftDeviation= leftSpacing-modeGap
                val rightDeviation = rightSpacing-modeGap
                val deviationLeftSq = (if (leftDeviation > 0) leftDeviation else -leftDeviation)
                val deviationRightSq = (if (rightDeviation > 0) rightDeviation else -rightDeviation)
            Pair(i, deviationLeftSq*deviationLeftSq + deviationRightSq*deviationRightSq) // todo Review this . TEst for this




        }
        val newModeGap = newGaps.calculateMode(1.5)
        val newDeviationsOld = newGaps.mapIndexed { i, gap ->
            val diff = gap-newModeGap
            val deviation =  if (diff >0) diff else -diff//*0.550
            Pair(i, deviation*deviation)
        }
        val sqrDeviation = newDeviations.map { it.second }.sum()
        if (debug_detailed) println("Removing index: $indexToRemove, new average deviation: $sqrDeviation")
        sqrDeviation
    }

    if (debug_detailed) {
        println("\tOutlier to be removed: $outlierToBeRemoved")
        println("\tOutlier to be removed index: ${outlierToBeRemoved.first}")
    }




    // Remove the one with greatest deviation
    val indexToRemove = outlierToBeRemoved.first



    return sortedBars.filterIndexed { i, _ -> i != indexToRemove }.toTypedArray()
}


fun List<Double>.calculateMode(k: Double): Double {
    if (this.isEmpty()) throw IllegalArgumentException("Array is empty")

    val groupWidth = this[this.size*2/3] * 0.2 //todo arbitrary

    val groupCounts = mutableMapOf<Double, Int>().withDefault { 0 }

    for (value in this) {
        for (center in this) {
            val distance = kotlin.math.abs(value - center)
            if (distance <= groupWidth / 2) {
                groupCounts[center] = groupCounts.getValue(center) + 1
            }
        }
    }

  //  println("${this.size}\tValues: $sortedList")
  //  println("\tminPosition: $minPosition, maxPosition: $maxPosition")
  //  println("\tGroup Centers: $groupCenters")
  //  println("\tGroup Width: $groupWidth")
  //  println("\tGroup Counts: $groupCounts")
    val maxKey = groupCounts.maxByOrNull { it.value }?.key
    ?: throw IllegalStateException("No group matched any value")
   // println("\tmode: $maxKey")
    return maxKey
}
