package org.example

import org.example.JFrameExtensions.launchOnMonitor
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel

import java.awt.Color
import java.awt.event.MouseEvent
import java.awt.event.MouseAdapter

class Window(
    val image: BufferedImage,
    val title: String = "",
    private val monitorIndex : Int = 0,

    val x: Int = 0,
    val y: Int = 0
){
    val frame = JFrame(title)
    val label = JLabel(ImageIcon(image))

    init {
        //frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        label.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val x = e.x
                val y = e.y

                if (x in 0 until image.width && y in 0 until image.height) {
                    val rgb = image.getRGB(x, y)
                    val color = Color(rgb)
                    println("WINDOW: $title, Clicked at ($x, $y) - RGB: (${color.red}, ${color.green}, ${color.blue}), Brightness: ${(color.red * 0.3 + color.green * 0.59 + color.blue * 0.11)/255.0}")
                }
            }
        })
        frame.contentPane.add(label)
        frame.pack()

        frame.launchOnMonitor(monitorIndex, dx = x, dy = y)
    }
}

