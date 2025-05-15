package org.example

import CellState
import org.example.nonogram.Nonogram
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Container
import java.awt.Font
import java.awt.GridLayout
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

import java.awt.*
import java.awt.event.*
import javax.swing.*

class NonogramGUI(
    nonogram: Nonogram
) : JFrame("Nonogram"), Nonogram.NonogramChangeListener, CellInteractionHandler  {

    override var isDragging = false
    override var currentActionState: CellState? = null

    override fun onCellStateChanged(cell: NonogramCellButton, newState: CellState) {
        TODO("Not yet implemented")
        // todo notifly nonogram
    }


    private val numRows = nonogram.clues.rows.size
    private val numCols = nonogram.clues.columns.size

    //state is being stored in the NonogramCellButton, this is ok?

    private val grid: Array<Array<NonogramCellButton?>> = Array(numRows) { Array(numCols) { null } }


    fun setCellState(row: Int, col: Int, state: CellState) {
        //grid[row][col]?.state = state
        //grid[row][col]?.repaint()

    }

    init {
        nonogram.addListener(this)
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()

        val maxColClues = nonogram.clues.columns.maxOf { it.size }
        val maxRowClues = nonogram.clues.rows.maxOf { it.size }
        val totalRows = maxColClues + numRows
        val totalCols = maxRowClues + numCols
        val clueRowHeight = maxColClues
        val clueColWidth = maxRowClues

        val panel = JPanel(GridLayout(totalRows, totalCols))
        panel.background = Color.WHITE

        // Fill grid with appropriate components
        for (r in 0 until totalRows) {
            for (c in 0 until totalCols) {
                val comp: JComponent = when {
                    r < clueRowHeight && c < clueColWidth -> JLabel() // top-left corner
                    r < clueRowHeight && c >= clueColWidth -> clueLabel(
                        nonogram.clues.columns[c - clueColWidth], clueRowHeight - r - 1
                    )

                    r >= clueRowHeight && c < clueColWidth -> clueLabel(
                        nonogram.clues.rows[r - clueRowHeight], clueColWidth - c - 1, vertical = false
                    )

                    else -> {
                        val rowIndex = r - clueRowHeight
                        val colIndex = c - clueColWidth
                        val button = gameCell(rowIndex, colIndex)
                        grid[rowIndex][colIndex] = button
                        button
                    }
                }

                comp.border = BorderFactory.createLineBorder(Color.GRAY)
                panel.add(comp)
            }
        }

        Container.add(panel, BorderLayout.CENTER)
        pack()
        setLocationRelativeTo(null)
        isVisible = true
    }

    private fun clueLabel(clue: List<Int>, index: Int, vertical: Boolean = true): JLabel {
        val label = JLabel().apply {
            horizontalAlignment = SwingConstants.CENTER
            verticalAlignment = SwingConstants.CENTER
            background = Color(255, 255, 180) // light yellow
            isOpaque = true
            font = Font("Monospaced", Font.PLAIN, 12)
        }
        if (index < clue.size) {
            label.text = clue[index].toString()
        }
        return label
    }

    private fun gameCell(row: Int, col: Int): NonogramCellButton {
        return NonogramCellButton(CellState.WHITE, interactionHandler = this)

    }

    override fun onCellUpdated(row: Int, col: Int, state: Nonogram.NonogramCellState) {
        grid[row][col]?.state = state.toGUIstate()
        grid[row][col]?.repaint()
    }

    fun Nonogram.NonogramCellState.toGUIstate() = when
                                                          (this) {
        Nonogram.NonogramCellState.EMPTY -> CellState.X
        Nonogram.NonogramCellState.FILLED -> CellState.BLACK
        Nonogram.NonogramCellState.UNKNOWN -> CellState.WHITE
    }


}