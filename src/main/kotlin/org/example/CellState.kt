import org.example.nonogram.Nonogram
import org.example.nonogram.Nonogram.NonogramChangeListener
import java.awt.*
import java.awt.event.*
import javax.swing.*

enum class CellState { WHITE, BLACK, X }

var isDragging = false
var currentActionState: CellState? = null

class NonogramGUI(
     private val nonogram: Nonogram
) : JFrame("Nonogram"), NonogramChangeListener {

    private val numRows = nonogram.clues.rows.size
    private val numCols = nonogram.clues.columns.size
    private val grid:Array<Array<NonogramCellButton?>> =  Array(numRows) { Array(numCols) { null } }


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

        add(panel, BorderLayout.CENTER)
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
        return NonogramCellButton(CellState.WHITE)

    }

    override fun onCellUpdated(row: Int, col: Int, cell: Nonogram.NonogramCell) {
        grid[row][col]?.state = cell.state.toGUIstate()
        grid[row][col]?.repaint()
    }

    fun Nonogram.NonogramCellState.toGUIstate() = when
        (this) {
            Nonogram.NonogramCellState.EMPTY -> CellState.X
            Nonogram.NonogramCellState.FILLED -> CellState.BLACK
            Nonogram.NonogramCellState.UNKNOWN -> CellState.WHITE
        }


}



class NonogramCellButton(var state: CellState) : JButton() {
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
                isDragging = true
                // Toggle to next state and remember it for dragging
                state = nextState(state)
                currentActionState = state
                repaint()
            }

            override fun mouseReleased(e: MouseEvent) {
                isDragging = false
                currentActionState = null
            }

            override fun mouseEntered(e: MouseEvent) {
                if (isDragging && currentActionState != null) {
                    state = currentActionState!!
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

