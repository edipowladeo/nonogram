package org.example


import org.example.nonogram.Nonogram
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
    var currentActionState: Nonogram.NonogramCellState?
    fun onCellStateChanged(cell: NonogramCellButton, newState:  Nonogram.NonogramCellState)
}

class NonogramCellButton(
    var state:  Nonogram.NonogramCellState,
    val interactionHandler: CellInteractionHandler,
    val row: Int,
    val col: Int,
) : JButton() {
    init {
        fun nextState(state:  Nonogram.NonogramCellState):  Nonogram.NonogramCellState {
            println("State: $state")
            return when (state) {
                Nonogram.NonogramCellState.UNKNOWN -> Nonogram.NonogramCellState.FILLED
                Nonogram.NonogramCellState.FILLED -> Nonogram.NonogramCellState.EMPTY
                Nonogram.NonogramCellState.EMPTY -> Nonogram.NonogramCellState.UNKNOWN
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
                interactionHandler.onCellStateChanged(this@NonogramCellButton, state)
            }

            override fun mouseReleased(e: MouseEvent) {
                interactionHandler.isDragging = false
                interactionHandler. currentActionState = null

            }

            override fun mouseEntered(e: MouseEvent) {
                if (interactionHandler.isDragging && interactionHandler.currentActionState != null) {
                    state = interactionHandler.currentActionState!!
                    repaint()
                    interactionHandler.onCellStateChanged(this@NonogramCellButton, state)
                }
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        when (state) {
            Nonogram.NonogramCellState.FILLED -> {
                background = Color.BLACK
            }

            Nonogram.NonogramCellState.UNKNOWN -> {
                background = Color.WHITE
            }

            Nonogram.NonogramCellState.EMPTY -> {
                background = Color.WHITE
                val g2 = g as Graphics2D
                g2.color = Color.GRAY
                g2.stroke = BasicStroke(2f)

                val margin = 0
                g2.drawLine(margin, margin, width - margin, height - margin)
                g2.drawLine(width - margin, margin, margin, height - margin)
            }
        }
    }
}