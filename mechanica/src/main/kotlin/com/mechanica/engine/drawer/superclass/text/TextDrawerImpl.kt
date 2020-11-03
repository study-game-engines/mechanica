package com.mechanica.engine.drawer.superclass.text

import com.mechanica.engine.drawer.state.DrawState
import com.mechanica.engine.game.Game
import com.mechanica.engine.models.TextModel
import com.mechanica.engine.text.Text
import com.mechanica.engine.unit.vector.InlineVector
import com.mechanica.engine.unit.vector.vec

class TextDrawerImpl(
        private val state: DrawState) : TextDrawer {

    override fun text(text: String) {
        val model = state.stringHolderModel
        model.string = text
        drawText(model)
    }

    override fun text(text: Text) {
        val model = state.textHolderModel
        model.setText(text)
        drawText(model)
    }

    private fun drawText(model: TextModel) {
        state.blend = 0f
        state.alphaBlend = 1f
        state.colorPassthrough = true

        if (!state.viewMatrixWasSet) state.viewMatrix = Game.matrices.uiCamera

        val topLeft = vec(0.0, -1.0)
        val bottomRight = bottomRight(model)

        val height = bottomRight.y - topLeft.y
        val width = bottomRight.x - topLeft.x

        val origin = state.origin.normalized
        val oX = origin.x
        val oY = origin.y

        origin.set(oX*width, oY*height - bottomRight.y)
        state.draw(model)
    }


    override fun text(text: Text, size: Number, x: Number, y: Number) {
        positionAndScale(size, x, y)
        text(text)
    }

    override fun text(text: String, size: Number, x: Number, y: Number) {
        positionAndScale(size, x, y)
        text(text)
    }

    private fun positionAndScale(size: Number, x: Number, y: Number) {
        state.setTranslate(x.toFloat(), y.toFloat())
        state.setScale(size.toFloat(), size.toFloat())
    }

    private fun bottomRight(model: TextModel): InlineVector {
        val lc = model.lineCount
        val y = lc.toDouble()
        var longest = 0.0
        for (i in 0 until lc) {
            val length = model.getEndOfLinePosition(i)
            if (length > longest) {
                longest = length
            }
        }

        val x = longest

        return vec(x, y - 1.0)
    }
}