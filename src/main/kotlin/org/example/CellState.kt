import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import org.example.Clues
import org.example.Nonogram
import org.example.NonogramDrawer
import java.awt.*
import java.awt.event.*
import java.io.File
import javax.swing.*

enum class CellState { WHITE, BLACK, X }

var isDragging = false
var currentActionState: CellState? = null

class NonogramGUI(
    private val rowClues: List<List<Int>>, private val colClues: List<List<Int>>
) : JFrame("Nonogram") {

    private val numRows = rowClues.size
    private val numCols = colClues.size
    private val grid = Array(numRows) { Array(numCols) { CellState.WHITE } }


    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()

        val totalRows = rowClues.maxOf { it.size } + numRows
        val totalCols = colClues.maxOf { it.size } + numCols
        val clueRowHeight = colClues.maxOf { it.size }
        val clueColWidth = rowClues.maxOf { it.size }

        val panel = JPanel(GridLayout(totalRows, totalCols))
        panel.background = Color.WHITE

        // Fill grid with appropriate components
        for (r in 0 until totalRows) {
            for (c in 0 until totalCols) {
                val comp: JComponent = when {
                    r < clueRowHeight && c < clueColWidth -> JLabel() // top-left corner
                    r < clueRowHeight && c >= clueColWidth -> clueLabel(
                        colClues[c - clueColWidth], clueRowHeight - r - 1
                    )

                    r >= clueRowHeight && c < clueColWidth -> clueLabel(
                        rowClues[r - clueRowHeight], clueColWidth - c - 1, vertical = false
                    )

                    else -> gameCell(r - clueRowHeight, c - clueColWidth)
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

    private fun gameCell(row: Int, col: Int): JButton {
        return NonogramCellButton(CellState.WHITE)

    }


}

fun main() {
    SwingUtilities.invokeLater {
        // You can load these from file or generate them programmatically
        val loaded = Cbor.decodeFromByteArray<Clues>(File("clues.dat").readBytes())
        println("loaded")
        println(loaded)
        //game.getGameCell(7,2,0.85).convertToGrayscale().toBlackAndWhite(128).also { ImageIO.write(it, "png", File("bw_4.png"))}
        val nonogram = Nonogram(
            clues = loaded,
            width = loaded.columns.size,
            height = loaded.rows.size,
            grid = Array(loaded.rows.size) { Array(loaded.columns.size) { Nonogram.NonogramCell.UNKNOWN } })
        val nonogramDrawer = NonogramDrawer()
        println(nonogramDrawer.drawNonogram(nonogram))

        NonogramGUI(colClues = loaded.columns, rowClues = loaded.columns)
    }
}

class NonogramCellButton(var state: CellState) : JButton() {
    init {
        fun nextState(state: CellState): CellState {
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
        })


        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                isDragging = true
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
