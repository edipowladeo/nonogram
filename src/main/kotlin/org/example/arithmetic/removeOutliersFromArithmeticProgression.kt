package org.example.arithmetic

import org.example.Window
import org.example.bufferedImageExtensions.Bar
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
        Window(proofBars, "Debug Remove outliers: Size: ${this.size}", monitorIndex = 1)
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


    if (debug) println("\tOutliers: $outliers")

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
        val newModeGap = newGaps.calculateMode(1.5)
        val newDeviations = newGaps.mapIndexed { i, gap ->
            val diff = gap-newModeGap
            val deviation =  if (diff >0) diff else -diff*5.0
            Pair(i, deviation)
        }
        val averageDeviation = newDeviations.map { it.second }.average()
        if (debug) println("Removing index: $indexToRemove, new average deviation: $averageDeviation")
         averageDeviation
    }





    // Remove the one with greatest deviation
    val indexToRemove = outlierToBeRemoved.first

    if (debug){
        println("${this.size}\tGaps: $gaps")
        println("\rGaps Sorted: ${gaps.sorted()}")
        println("\tmedianGap: $modeGap, tolerance: $tolerance")

        println("\tOutliers: $outliers")
        println("\tIndex to remove: $indexToRemove")
    }

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
