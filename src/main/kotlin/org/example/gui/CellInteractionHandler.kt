package org.example.gui

import org.example.nonogram.Nonogram

interface CellInteractionHandler {
    var isDragging: Boolean
    var currentActionState: Nonogram.NonogramCellState?
    fun onCellStateChanged(cell: NonogramCellButton, newState:  Nonogram.NonogramCellState)
    fun getInteractionMode(): InteractionMode
    var hoveredCell: NonogramCellButton? //todo this violates the purpose of interface

    enum class InteractionMode(val label: String) {
        CYCLE("Cycle"),
        SET_EMPTY("X"),
        SET_UNKNOWN("_"),
        SET_FILLED("#");

        override fun toString() = label
    }
}