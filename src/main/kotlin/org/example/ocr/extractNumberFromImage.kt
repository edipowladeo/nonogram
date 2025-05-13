package org.example.ocr

import net.sourceforge.tess4j.Tesseract
import java.awt.image.BufferedImage

fun extractNumberFromImage(image: BufferedImage): Int? {
    val tesseract = Tesseract().apply {
        setDatapath("/opt/homebrew/share/tessdata")
        //setDatapath("tessdata") // Path to tessdata folder
        setLanguage("eng")
        //  setTessVariable("tessedit_char_whitelist", "0123456789")
    }

    return try {
        val result = tesseract.doOCR(image).trim()
        println("partial Result : [$result]")
        val number = result
            //  .filter { it.isDigit() }
            .toIntOrNull()
        if (number in 0..50) number else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}