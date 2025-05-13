package org.example.JFrameExtensions

import java.awt.GraphicsEnvironment
import javax.swing.JFrame

fun JFrame.launchOnMonitor(monitor: Int, dx: Int = 0, dy: Int = 0, debug: Boolean = false) {
    val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
    val screens = ge.screenDevices

    if (debug) println("Detected ${screens.size} screens")
    screens.forEachIndexed { i, s ->
        if (debug) println("Screen $i: W: ${s.displayMode.width} H: ${s.displayMode.height}, bounds x: ${s.defaultConfiguration.bounds.x}, bounds y: ${s.defaultConfiguration.bounds.y}")
    }

    if (screens.size <= 1) {
        if (debug) println("Only one monitor detected, showing on primary monitor.")
        this.setLocationRelativeTo(null) // center on primary
        this.isVisible = true
        return
    }

    if (monitor > (screens.size - 1)) {
        if (debug) println("Monitor available indexes: [0 - ${screens.size - 1}], fallback to default")
        this.setLocationRelativeTo(null) // center on primary
        this.isVisible = true
        return
    }
    // Use the second monitor (index 1)
    val screen = screens[monitor]
    val bounds = screen.defaultConfiguration.bounds

    // Position the frame at the top-left corner of the second screen
    if (debug) println("set to ${bounds.x}, ${bounds.y}")
    this.setLocation(bounds.x + dx, bounds.y + dy)

    this.isVisible = true
}