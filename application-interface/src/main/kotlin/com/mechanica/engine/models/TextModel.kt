package com.mechanica.engine.models

import com.mechanica.engine.text.Font
import com.mechanica.engine.text.Text
import com.mechanica.engine.context.loader.GLLoader
import com.mechanica.engine.shader.qualifiers.Attribute
import com.mechanica.engine.utils.createIndicesArrayForQuads
import com.mechanica.engine.vertices.AttributeArray
import com.mechanica.engine.vertices.IndexArray
import com.mechanica.engine.vertices.FloatBufferMaker
import kotlin.math.max

class TextModel(text: Text,
                positionBufferMaker: FloatBufferMaker = Attribute(0).vec3(),
                texCoordsBufferMaker: FloatBufferMaker = Attribute(1).vec2()) : Model(
        positionBufferMaker.createBuffer(text.positions),
        texCoordsBufferMaker.createBuffer(text.texCoords),
        IndexArray.create(*createIndicesArrayForQuads(max(text.positions.size/2, 20))),
        Image.invoke(text.font.atlas.id),
        draw = { model ->
            GLLoader.graphicsLoader.drawElements(model)
        }
) {
    private val positionAttribute = inputs[0] as AttributeArray
    private val texCoordsAttribute = inputs[1] as AttributeArray
    private val indexArray = inputs[2] as IndexArray

    private var textHolder: Text = text

    var string: String
        get() = textHolder.string
        set(value) {
            textHolder.string = value
            updateTextHolder(textHolder)
        }

    val lineCount: Int
        get() = textHolder.lineCount

    constructor(text: String): this(Text(text))
    constructor(text: String, font: Font): this(Text(text, font))

    init {
        vertexCount = textHolder.vertexCount
    }

    fun setText(text: Text) {
//        if (text.string != this.string || textHolder != text) {
            this.textHolder = text
            updateTextHolder(text)
//        }
    }

    private fun updateTextHolder(text: Text) {
        positionAttribute.set(text.positions, 0, text.vertexCount)
        texCoordsAttribute.set(text.texCoords, 0, text.vertexCount)
        vertexCount = text.vertexCount

        if (vertexCount > indexArray.vertexCount) {
            indexArray.set(createIndicesArrayForQuads(vertexCount))
        }
    }

    fun getLine(index: Int) = textHolder.getLine(index)

    fun getClosestCharacterPosition(x: Double, line: Int) = textHolder.getClosestCharacterPosition(x, line)

    fun getEndOfLinePosition(line: Int) = textHolder.getEndOfLinePosition(line)

    fun getCharacterIndex(x: Double, line: Int) = textHolder.searchCharacter(x, line)

    fun getCharacterPosition(index: Int) = textHolder.getCharacterPosition(index)

}