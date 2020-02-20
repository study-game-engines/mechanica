package demo.renderer

import display.Game
import display.GameOptions
import gl.*
import gl.renderer.PolygonRenderer
import graphics.Image
import graphics.drawer.Drawer
import input.Cursor
import matrices.TransformationMatrix
import models.Model
import org.lwjgl.opengl.GL11
import gl.script.ShaderScript
import gl.shader.ShaderImpl
import gl.utils.*
import gl.utils.startFrame
import gl.utils.startGame
import gl.vbo.VBO
import graphics.Polygon
import graphics.drawer.DrawerImpl
import input.Keyboard
import loader.contentsToString
import loader.loadModel
import loader.toBuffer
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import resources.Res
import state.State
import util.colors.hex
import util.colors.rgba
import util.extensions.degrees
import util.extensions.vec
import util.units.Vector
import java.nio.ByteBuffer


fun main() {
    val options = GameOptions()
            .setResolution(1280, 720)
//            .setFullscreen(true, true)
            .setDebugMode(true)
            .setViewPort(height = 10.0)
            .setStartingState { StartMain() }

    Game.start(options)
    Game.update()
    Game.destroy()
}

private class StartMain : State() {
    private val quad: Model = Model(-1, 0, Image(-1))
    //= loadTexturedQuad(loadImageFromResource(Res.image["colors"]), 0f, 0.5f, 0.5f, 0f)
    private val red = rgba(1.0, 0.0, 0.0, 1.0)

    private val vertex = object : ShaderScript() {
        //language=GLSL
        override val main: String =
                """
                void main(void) {
                    gl_Position = matrices(vec4(position, 1.0));
                }
                """

    }

    private val fragment = object : ShaderScript() {

        val color = uniform.vec4(hex(0xFF00FFFF))
        //language=GLSL
        override val main: String = """
            
                out vec4 out_Color;
                                
                void main(void) {
                    out_Color = $color;
                }
            """

    }

//    private val renderer: Renderer
//    private val vbo: VBO
//    private val texVbo: VBO
//    val shader: ShaderImpl
    private val transformation = TransformationMatrix()
    val draw = DrawerImpl()
//    val drawable: Drawable
//    val image: Image
    var timer = 0.0
    var score = 0
    val polygon: Polygon

    init {
        startGame()
//        shader = ShaderImpl(vertex, fragment)
        transformation.setScale(1.0, 1.0,1.0)

//        val vertices = loadQuad(0f, 1f, 1f, 0f)
//        val texVerts = loadQuad(0f, 1.0f, 1.0f, 0f)

//        vbo = VBO.create(vertices, positionAttribute)
//        texVbo = VBO.create(texVerts, texCoordsAttribute)
//        image = createTexture(Res.image["colors"])
//
//        drawable = createIndexedDrawable(0f, 1f, 1f, 0f)


        val square = listOf(
                vec(0, 0),
                vec(0, 1),
                vec(1, 1),
                vec(1, 0)
        )

        val random = listOf(
                vec(0, 0),
                vec(0, 0.4),
                vec(1, 0.5),
                vec(4, 2),
                vec(3.5, -1),
                vec(3, -1.5),
                vec(1, -1)
        )

        polygon = Polygon.create(random)

    }

    override fun update(delta: Double) {
        timer += delta
        if (Keyboard.MB1.hasBeenPressed) {
            score++
        }
        if (Keyboard.MB2.hasBeenPressed) {
            score--
        }
    }

    override fun render(draw: Drawer) {

        startFrame()

        val blend = (((Cursor.viewX / Game.viewWidth) + 0.5)*360).degrees
//
//        val adjusted = hsl(blend, red.saturation, red.lightness)
//        fragment.color.set(adjusted)
//
//        val scaleX = ((Cursor.viewX*2.0 / Game.viewWidth))
//        val scaleY = ((Cursor.viewY*2.0 / Game.viewHeight))
        transformation.setScale(4.0, 4.0, 1.0)
        transformation.setTranslate(0.0, -4.0, 0.0)

//        polygonRenderer.render(transformation.create())
//        shader.render(drawable, transformation.create())
        this.draw.blue.polygon(polygon)
//        this.draw.red.text("Score: $score", 1f + (score.toFloat()/10f), 0, 0)
    }

    private fun loadQuad(left: Float, top: Float, right: Float, bottom: Float): Array<Vector> {
        return arrayOf(
                vec(left, top),
                vec(left, bottom),
                vec(right, bottom),
                vec(left, top),
                vec(right, top),
                vec(right, bottom))

    }

    fun createIndexedDrawable(left: Float, top: Float, right: Float, bottom: Float): Drawable {
        val vertices = createIndexedQuad(left, top, right, bottom)

        val vertexVBO = VBO.create(vertices.vertices.toBuffer(), positionAttribute)
        val indices = VBO.createIndicesBuffer(vertices.indices.toBuffer())

        return Drawable(vertexVBO, indices) {
            GL11.glDrawElements(GL11.GL_TRIANGLES, it.vertexCount, GL11.GL_UNSIGNED_SHORT, 0)
        }
    }

    fun createIndexedQuad(left: Float, top: Float, right: Float, bottom: Float): IndexedVertices {
        val vertices = floatArrayOf(left, top, 0.0f, left, bottom, 0.0f, right, bottom, 0.0f, right, top, 0.0f)

        val indices = shortArrayOf(0, 1, 2, 0, 2, 3)

        return IndexedVertices(vertices, indices)
    }

    fun loadImageFromMemory(buffer: ByteBuffer): Image {
        val width = BufferUtils.createIntBuffer(1)
        val height = BufferUtils.createIntBuffer(1)
        val components = BufferUtils.createIntBuffer(1)

        val data = STBImage.stbi_load_from_memory(buffer, width, height, components, 4)
        val id = GL11.glGenTextures()
        val imageDetails = ImageDetails(data, id, width.get(), height.get(), components.get())

        loadersVersion(imageDetails)

        if (data != null) {
            STBImage.stbi_image_free(data)
        }

        return Image(id)
    }

    fun loadersVersion(details: ImageDetails) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, details.id)
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, details.width, details.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, details.data)
    }

    private data class ImageDetails(val data: ByteBuffer?, val id: Int, val width: Int, val height: Int, val components: Int)

}