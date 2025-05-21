package org.example.gui

import org.example.GameImageParams
import org.example.nonogram.Nonogram
import org.example.gui.CellInteractionHandler.InteractionMode

import javax.swing.AbstractAction

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.GridLayout
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.ButtonGroup
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.KeyStroke
import javax.swing.SwingConstants


class NonogramGUI(
   val nonogram: Nonogram
) : JFrame("Nonogram"), Nonogram.NonogramChangeListener, CellInteractionHandler {


    override var hoveredCell:  NonogramCellButton? = null
    private var interactionMode = InteractionMode.CYCLE


    override var isDragging = false
    override var currentActionState:  Nonogram.NonogramCellState? = null

    override fun onCellStateChanged(cell: NonogramCellButton, newState: Nonogram.NonogramCellState) {
      // println("Cell state changed: $newState")
       nonogram.updateCell(cell.row, cell.col, newState)
//       nonogram.solve()

      //  TODO("Not yet implemented")
      //  // todo notify nonogram
    }

    override fun getInteractionMode(): InteractionMode {
        return interactionMode
    }




    private val numRows = nonogram.clues.rows.size
    private val numCols = nonogram.clues.columns.size

    //state is being stored in the NonogramCellButton, this is ok?

    private val grid: Array<Array<NonogramCellButton?>> = Array(numRows) { Array(numCols) { null } }


//    val radioButtons: MutableList<Int> = emptyList().toMutableList()
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
                        clue = nonogram.clues.columns[c - clueColWidth],
                        index = - clueRowHeight + r + nonogram.clues.columns[c - clueColWidth].size
                    )

                    r >= clueRowHeight && c < clueColWidth -> clueLabel(
                        clue = nonogram.clues.rows[r - clueRowHeight],
                        index = - clueColWidth + c + nonogram.clues.rows[r - clueRowHeight].size
                    )

                    else -> {
                        val rowIndex = r - clueRowHeight
                        val colIndex = c - clueColWidth
                        val button = gameCell(rowIndex, colIndex)
                        grid[rowIndex][colIndex] = button
                        button
                    }
                }

                val top = if ((r - clueRowHeight) % 5 == 0 && r >= clueRowHeight) 3 else 1
                val left = if ((c - clueColWidth) % 5 == 0 && c >= clueColWidth) 3 else 1
                val bottom = 1
                val right = 1

                comp.border = BorderFactory.createMatteBorder(top, left, bottom, right, Color.GRAY)
                panel.add(comp)
            }
        }

        add(panel, BorderLayout.CENTER)
        panel.size = Dimension(800, 600)
        pack()
        setLocationRelativeTo(null)
        isVisible = true

        // Solve button
        val solveButton = JButton("Solve").apply {
            addActionListener {
                nonogram.solve()
            }
            isFocusable = false
        }

// Interaction modes as radio buttons
        val modeGroup = ButtonGroup()
        val radioPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
        }

        InteractionMode.entries.forEach { mode ->
            val button = JRadioButton(mode.label).apply {
                isSelected = (mode == interactionMode)
                addActionListener {
                    interactionMode = mode
                }
            }
            button.isFocusable = false
            modeGroup.add(button)
            radioPanel.add(button)
            radioPanel.add(Box.createHorizontalStrut(10))
        }

        val bottomPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(Box.createHorizontalStrut(10))
            add(JLabel("Interaction mode:"))
            add(Box.createHorizontalStrut(10))
            add(radioPanel)
            add(Box.createHorizontalStrut(10))

            // Container to allow the solveButton to grow
            val solveContainer = JPanel(BorderLayout()).apply {
                minimumSize = Dimension(100, 30) // Minimum size
                preferredSize = Dimension(150, 30)
                add(solveButton, BorderLayout.CENTER)
            }

            add(solveContainer)
            add(Box.createHorizontalStrut(10))
        }


        add(bottomPanel, BorderLayout.SOUTH)

        fun registerInteractionModeShortcut(keyStroke: String, mode: InteractionMode) {
            val actionKey = "setMode_$mode"

            rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyStroke), actionKey)
            rootPane.actionMap.put(actionKey, object : AbstractAction() {
                override fun actionPerformed(e: java.awt.event.ActionEvent?) {
                    interactionMode = mode
                    println("Interaction mode set to: $mode")
                    radioPanel.components.filter { it is JRadioButton }.forEach { button ->
                        if (button is JRadioButton) {
                            button.isSelected = (button.text == mode.label)
                        }
                    }
                    hoveredCell?.let { cell ->
                        cell.previewState = cell.calculatePreviewState()
                        cell.repaint()
                    }
                }
            })
        }

// Register shortcuts
        registerInteractionModeShortcut("pressed Z", InteractionMode.CYCLE)
        registerInteractionModeShortcut("pressed X", InteractionMode.SET_EMPTY)
        registerInteractionModeShortcut("pressed SPACE", InteractionMode.SET_UNKNOWN)
        registerInteractionModeShortcut("pressed V", InteractionMode.SET_FILLED)

    val solveKey = "solveNonogram"

    rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), solveKey)
    rootPane.actionMap.put(solveKey, object : AbstractAction() {
        override fun actionPerformed(e: java.awt.event.ActionEvent?) {
            nonogram.solve()
            println("Solve triggered via Enter key")
        }
    })
    }

    private fun clueLabel(clue: List<Int>, index: Int): JLabel {
        val label = JLabel().apply {
            horizontalAlignment = SwingConstants.CENTER
            verticalAlignment = SwingConstants.CENTER
            background = GameImageParams.CLUE_CELL_BACKGROUND_COLOR
            isOpaque = true
            font = Font("Monospaced", Font.BOLD, 12)
        }
        if ((index < clue.size)&& (index >= 0)) {
            label.text = clue[index].toString()
        }
      //  preferredSize = java.awt.Dimension(10, 10) // Match game cell size
        return label
    }

    private fun gameCell(row: Int, col: Int): NonogramCellButton {
return NonogramCellButton(nonogram.grid[row][col].state, this, row = row, col = col)
    }

    override fun onCellUpdated(row: Int, col: Int, state: Nonogram.NonogramCellState) {
        grid[row][col]?.state = state
        grid[row][col]?.repaint()
    }
}