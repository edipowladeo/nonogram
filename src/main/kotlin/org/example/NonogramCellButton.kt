package org.example

import CellState

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.BorderFactory
import javax.swing.JButton

interface CellInteractionHandler {
    var isDragging: Boolean
    var currentActionState: CellState?
    fun onCellStateChanged(cell: NonogramCellButton, newState: CellState)
}

class NonogramCellButton(var state: CellState, val interactionHandler: CellInteractionHandler) : JButton() {
    init {
        fun nextState(state: CellState): CellState {
            println("State: $state")
            return when (state) {
                CellState.WHITE -> CellState.BLACK
                CellState.BLACK -> CellState.X
                CellState.X -> CellState.WHITE
            }
        }

        isFocusPainted = false
        background = Color.WHITE
        isOpaque = true
        border = BorderFactory.createLineBorder(Color.GRAY)

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                interactionHandler.isDragging = true
                // Toggle to next state and remember it for dragging
                state = nextState(state)
                interactionHandler.currentActionState = state
                repaint()
            }

            override fun mouseReleased(e: MouseEvent) {
                interactionHandler.isDragging = false
                interactionHandler. currentActionState = null
            }

            override fun mouseEntered(e: MouseEvent) {
                if (interactionHandler.isDragging && interactionHandler.currentActionState != null) {
                    state = interactionHandler.currentActionState!!
                    repaint()
                }
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        when (state) {
            CellState.BLACK -> {
                background = Color.BLACK
            }

            CellState.WHITE -> {
                background = Color.WHITE
            }

            CellState.X -> {
                background = Color.LIGHT_GRAY
                val g2 = g as Graphics2D
                g2.color = Color.RED
                g2.stroke = BasicStroke(2f)

                val margin = 0
                g2.drawLine(margin, margin, width - margin, height - margin)
                g2.drawLine(width - margin, margin, margin, height - margin)
            }
        }
    }
}