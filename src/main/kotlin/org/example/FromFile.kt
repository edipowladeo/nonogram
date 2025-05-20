package org.example

import arrow.core.raise.either
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import org.example.nonogram.Nonogram
import java.io.File
import javax.swing.SwingUtilities


    fun main() {
        SwingUtilities.invokeLater {
            // You can load these from file or generate them programmatically
            val loaded = Cbor.decodeFromByteArray<Clues>(File("clues30.dat").readBytes())
            println("loaded")
            println(loaded)
            //game.getGameCell(7,2,0.85).convertToGrayscale().toBlackAndWhite(128).also { ImageIO.write(it, "png", File("bw_4.png"))}
            val nonogram = Nonogram(
                clues = loaded
            )
         //   val nonogramDrawer = NonogramDrawer()
          //  println(nonogramDrawer.drawNonogram(nonogram))


//            nonogram.updateCell(0, 0, Nonogram.NonogramCellState.EMPTY)

            val gui = NonogramGUI(nonogram)


  //          nonogram.updateCell(0, 1, Nonogram.NonogramCellState.FILLED)
            nonogram.forceCheck()
            nonogram.solve()


        }
    }
