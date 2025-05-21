package org.example

import arrow.core.raise.either
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import org.example.gui.NonogramGUI
import org.example.nonogram.Nonogram
import java.io.File
import javax.swing.SwingUtilities


    fun main() {
        SwingUtilities.invokeLater {

            val loaded = Cbor.decodeFromByteArray<Clues>(File("clues50.dat").readBytes())
            println("loaded")
            println(loaded)
            val nonogram = Nonogram(
                clues = loaded
            )

            val gui = NonogramGUI(nonogram)


            nonogram.forceCheck()
            nonogram.solve()

        }
    }
