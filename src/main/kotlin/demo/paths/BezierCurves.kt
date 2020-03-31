package demo.paths

import drawer.Drawer
import game.Game
import gl.models.Model
import gl.vbo.AttributeArray
import gl.vbo.pointer.VBOPointer
import input.Cursor
import input.Keyboard
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import state.State
import util.colors.rgba
import util.extensions.*
import util.units.Vector

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingState { StartBezier() }
    }
    Game.run()
}

private class StartBezier : State() {
    val renderer = CurveRenderer()

    private val maxVertices = 1000
    private val minLineLength = 0.1
    private val vbo = AttributeArray(maxVertices, VBOPointer.position)

    private val model = Model(vbo) {
        GL20.glLineWidth(3.0f)
        GL11.glPointSize(3.0f)
        GL12.glDrawArrays(GL11.GL_POINTS, 0, index)
        GL12.glDrawArrays(GL11.GL_LINE_STRIP, 0, index)
    }

    private val constructionLines = AttributeArray(maxVertices, VBOPointer.position)
    private val constructionFloats = FloatArray(9)
    private val constructionModel = Model(constructionLines) {
        GL20.glLineWidth(3.0f)
        GL11.glPointSize(8.0f)
        GL12.glDrawArrays(GL11.GL_POINTS, 0, 3)
        GL12.glDrawArrays(GL11.GL_LINE_STRIP, 0, 3)
    }

    private val transformation = Matrix4f()

    private val floats = FloatArray((maxVertices)*3)
    private var index = 0

    var p1 = vec(-6.0, -3.0)
    var p2 = vec(6.0, -3.0)
    var bezierPoint = vec(0.0, 3.0)

    init {
        bezier(p1, bezierPoint, p2)
        reset()
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4)
        GL11.glEnable(GL13.GL_MULTISAMPLE)
    }

    override fun update(delta: Double) {
    }

    override fun render(draw: Drawer) {

        if (Keyboard.MB1.isDown && Keyboard.SHIFT.isDown) {
            val cursor = vec(Cursor.viewX, Cursor.viewY)
            bezierPoint = cursor
            reset()
        } else if (Keyboard.MB1.isDown) {
            val cursor = vec(Cursor.viewX, Cursor.viewY)
            p1 = cursor
            reset()
        }else if (Keyboard.MB2.isDown) {
            val cursor = vec(Cursor.viewX, Cursor.viewY)
            p2 = cursor
            reset()
        }

        renderer.color = rgba(0.5, 0.5, 0.5, 0.5)
        renderer.render(model, transformation)

        renderer.color = rgba(0.5, 0.8, 0.5, 1.0)
        renderer.render(constructionModel, transformation)
    }

    private fun reset() {
        index = 0
        for(i in floats.indices) {
            floats[i] = 0f
        }
        bezier(p1, bezierPoint, p2)
        updateConstructionLines()
    }

    private fun bezier(p1: Vector, p2: Vector, p3: Vector) {
        val p12 = p2 - p1
        val p23 = p3 - p2
        val p13 = p3 - p1
        val iterations = p13.r/minLineLength

        val increment = 1.0/iterations
        var t = increment

        while (t < 1.0) {
            val p12t = p1 + p12*t
            val p23t = p2 + p23*t

            val relative = p23t - p12t
            val point = p12t + relative*t
            addPoint(point)
            t += increment
        }

    }

    private fun addPoint(vector: Vector) {
        if (index < maxVertices) {
            floats[index * 3] = vector.x.toFloat()
            floats[index * 3 + 1] = vector.y.toFloat()
            index++
        }

        vbo.update(floats)
    }

    private fun updateConstructionLines() {
        constructionFloats[0] = p1.x.toFloat()
        constructionFloats[1] = p1.y.toFloat()
        constructionFloats[3] = bezierPoint.x.toFloat()
        constructionFloats[4] = bezierPoint.y.toFloat()
        constructionFloats[6] = p2.x.toFloat()
        constructionFloats[7] = p2.y.toFloat()
        constructionLines.update(constructionFloats)
    }
}


