package org.example.nonogram

data class IntBar(val start: Int, val end: Int){
    val length: Int = end - start + 1
    override fun toString(): String {
        return "Bar(start=$start, end=$end, length=$length)"
    }

    companion object{
        fun fromStartAndLength(start: Int, length: Int): IntBar {
            val end = start + length - 1
            return IntBar(start, end)
        }
    }

}

data class IntBars(
    private val _bars: List<IntBar>
) {
    val bars = _bars//.sortedWith(barComparator)
    val size: Int = bars.size

    companion object{
        val barComparator = compareBy<IntBar> { it.start }
            .thenBy { it.length }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IntBars) return false
        return bars == other.bars
    }

    override fun hashCode(): Int {
        return bars.hashCode()
    }

    override fun toString(): String = "$bars"

}
