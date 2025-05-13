package org.example.arithmetic

import org.example.bufferedImageExtensions.Bar

fun Array<Bar>.removeOutliersFromArithmeticProgression(toleranceRatio:Double = 0.2): Array<Bar>{
    val newArray = this.removeGreatestOutlierFromArithmeticProgression(toleranceRatio);

    if (newArray.size < this.size){
        return newArray.removeOutliersFromArithmeticProgression(toleranceRatio);
    }

    return this
}

fun Array<Bar>.removeGreatestOutlierFromArithmeticProgression(toleranceRatio:Double): Array<Bar> {
    if (this.size <= 2) return this // Not enough points to evaluate

    val sorted = this.sortedBy { it.center }
    val gaps = List(sorted.size) { i ->
        when (i) {
            0 -> (sorted[1].center - sorted[0].center)
            sorted.lastIndex -> (sorted[i].center - sorted[i - 1].center)
            else -> (sorted[i + 1].center - sorted[i - 1].center) / 2.0
        }
    }

    val medianGap = gaps.sorted()[gaps.size / 2]
    val tolerance = medianGap * toleranceRatio

    // Compute absolute deviation from median
    val deviations = gaps.mapIndexed { i, gap ->
        Pair(i, kotlin.math.abs(gap - medianGap))
    }

    // Filter candidates beyond 10% deviation
    val outliers = deviations.filter { (_, dev) -> dev > tolerance }

    if (outliers.isEmpty()) return sorted.toTypedArray()

    // Remove the one with greatest deviation
    val indexToRemove = outliers.maxByOrNull { it.second }!!.first

    return sorted.filterIndexed { i, _ -> i != indexToRemove }.toTypedArray()
}