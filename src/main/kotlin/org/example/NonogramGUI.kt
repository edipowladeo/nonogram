package org.example

import org.example.nonogram.Nonogram
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.GridLayout
import javax.swing.*

class NonogramGUI(
   val nonogram: Nonogram
) : JFrame("Nonogram"), Nonogram.NonogramChangeListener, CellInteractionHandler  {



    override var isDragging = false
    override var currentActionState:  Nonogram.NonogramCellState? = null

    override fun onCellStateChanged(cell: NonogramCellButton, newState: Nonogram.NonogramCellState) {
      // println("Cell state changed: $newState")
       nonogram.updateCell(cell.row, cell.col, newState)
//       nonogram.solve()

      //  TODO("Not yet implemented")
      //  // todo notifly nonogram
    }


    private val numRows = nonogram.clues.rows.size
    private val numCols = nonogram.clues.columns.size

    //state is being stored in the NonogramCellButton, this is ok?

    private val grid: Array<Array<NonogramCellButton?>> = Array(numRows) { Array(numCols) { null } }


    fun setCellState(row: Int, col: Int, state: Nonogram.NonogramCellState) {
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

                comp.border = BorderFactory.createLineBorder(Color.GRAY)
                panel.add(comp)
            }
        }

        add(panel, BorderLayout.CENTER)
        panel.size = Dimension(800, 600)
        pack()
        setLocationRelativeTo(null)
        isVisible = true
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
return         NonogramCellButton(nonogram.grid[row][col].state, this,row = row,col = col)
    }

    override fun onCellUpdated(row: Int, col: Int, state: Nonogram.NonogramCellState) {
        grid[row][col]?.state = state
        grid[row][col]?.repaint()
    }




}