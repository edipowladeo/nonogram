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

    val sorted = this.sortedBy { it.center }
    val gaps = List(sorted.size) { i ->
        when (i) {
            0 -> (sorted[1].center - sorted[0].center)
            sorted.lastIndex -> (sorted[i].center - sorted[i - 1].center)
            else -> (sorted[i + 1].center - sorted[i - 1].center) / 2.0
        }
    }

    val modeGap = gaps.sorted().calculateMode(1.5)
    val tolerance = modeGap * toleranceRatio

    // Compute absolute deviation from median
    val deviations = gaps.mapIndexed { i, gap ->
        Pair(i, kotlin.math.abs(gap - modeGap))
    }

    // Filter candidates beyond 10% deviation
    val outliers = deviations.filter { (_, dev) -> dev > tolerance }

    if (outliers.isEmpty()) return sorted.toTypedArray()

    // Remove the one with greatest deviation
    val indexToRemove = outliers.maxByOrNull { it.second }!!.first

    if (debug){
        println("${this.size}\tGaps: $gaps")
        println("\rGaps Sorted: ${gaps.sorted()}")
        println("\tmedianGap: $modeGap, tolerance: $tolerance")
        println("\tDeviations: $deviations")
        println("\tOutliers: $outliers")
        println("\tIndex to remove: $indexToRemove")
    }

    return sorted.filterIndexed { i, _ -> i != indexToRemove }.toTypedArray()
}


fun List<Double>.calculateMode(k: Double): Double {
    if (this.isEmpty()) throw IllegalArgumentException("Array is empty")

    val sortedList = this.sorted()
    val n = this.size
    val minPosition = this.first()
    val maxPosition = this.last()
    val baseSpacing = (maxPosition - minPosition) / n
    val groupWidth = baseSpacing * k

    // Equally spaced group centers between minPosition and maxPosition
    val groupCenters = List(n) { i -> minPosition + i * baseSpacing + baseSpacing / 2 }

    // Count how many points fall within the window of each group center
    val groupCounts = mutableMapOf<Double, Int>().withDefault { 0 }

    for (value in this) {
        for (center in groupCenters) {
            val distance = kotlin.math.abs(value - center)
            if (distance <= groupWidth / 2) {
                groupCounts[center] = groupCounts.getValue(center) + 1
            }
        }
    }

    println("${this.size}\tValues: $sortedList")
    println("\tGroup Centers: $groupCenters")
    println("\tGroup Width: $groupWidth")
    println("\tGroup Counts: $groupCounts")
val maxKey = groupCounts.maxByOrNull { it.value }?.key
    ?: throw IllegalStateException("No group matched any value")
    println("\tmode: $maxKey")
    return maxKey
}
