package com.artharyoung.text2pic

import java.awt.*
import java.awt.font.FontRenderContext
import java.awt.font.TextAttribute
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.File
import java.text.AttributedCharacterIterator
import java.text.AttributedString
import javax.imageio.ImageIO

class TextCanvas(
        private val padding: Int = 20,
        private val textColor: Color = Color(94, 87, 77),
        private val background: Color = Color(253, 253, 245),
        private val lineColor: Color = Color(230, 230, 220)
) {

    private val contents by lazy {
        mutableListOf<TextView>()
    }

    private val defaultFont by lazy {
        textFont()
    }

    fun addString(src: String, font: Font = defaultFont): TextCanvas {
        val srcArray = src.split("\n")
        for (s in srcArray) {
            if (s.isNotEmpty()) {
                val area = measureText(s, font)
                val attr = textAttr(s, font)
                contents.add(TextView(attr, area))
            } else {
                val area = measureText(" ", font)
                val attr = textAttr(" ", font)
                contents.add(TextView(attr, area))
            }
        }
        return this
    }

    fun writeTo(file: File) {
        if (contents.isEmpty()) {
            throw IllegalArgumentException("text canvas has no content")
        }

        var maxWidth = 0
        var maxHeight = 0

        contents.forEach {
            maxWidth = it.area.first.coerceAtLeast(maxWidth)
            maxHeight += it.area.second
        }

        val image = createImage(maxWidth, maxHeight)
        val canvas = image.createGraphics()
        val width = image.width
        val height = image.height

        fillBackground(canvas, background, width, height)
        drawRect(canvas, lineColor, margin = 6, width = width, height = height)
        drawRect(canvas, lineColor, margin = 10, width = width, height = height)

        var textPadding = padding
        contents.forEach { textView ->
            drawText(canvas, textColor, textView.src, padding, padding + textPadding)
            textPadding += textView.area.second
        }
        canvas.dispose()
        ImageIO.write(image, "png", file)
    }

    private fun createImage(width: Int, height: Int): BufferedImage {
        return BufferedImage(width + padding * 2, height + padding * 2, BufferedImage.TYPE_INT_RGB)
    }

    private fun measureText(src: String, font: Font): Pair<Int, Int> {
        val frc = FontRenderContext(AffineTransform(), true, true)
        val rec = font.getStringBounds(src, frc).bounds
        return Pair(rec.width, rec.height)
    }

    private fun drawRect(g: Graphics2D, color: Color, stroke: Float = 1.0f, margin: Int, width: Int, height: Int) {
        g.color = color
        g.stroke = BasicStroke(stroke)
        g.drawLine(margin, margin, margin, height - margin)
        g.drawLine(margin, margin, width - margin, margin)
        g.drawLine(width - margin, height - margin, margin, height - margin)
        g.drawLine(width - margin, height - margin, width - margin, margin)
    }

    private fun fillBackground(g: Graphics2D, color: Color, width: Int, height: Int) {
        g.color = color
        g.fillRect(0, 0, width, height)
    }

    private fun drawText(g: Graphics2D, color: Color, ats: AttributedCharacterIterator, x: Int, y: Int) {
        g.color = color
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g.drawString(ats, x, y)
    }

    private fun textFont(fontType: String = Font.SERIF, fontStyle: Int = Font.PLAIN, fontSize: Int = 20): Font {
        return Font(fontType, fontStyle, fontSize)
    }

    private fun textAttr(src: String, font: Font): AttributedCharacterIterator {
        val ats = AttributedString(src)
        ats.addAttribute(TextAttribute.FONT, font, 0, src.length)
        return ats.iterator
    }

    data class TextView(val src: AttributedCharacterIterator, val area: Pair<Int, Int>)
}