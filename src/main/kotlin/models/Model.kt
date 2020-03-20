package models

import gl.vbo.VBO
import graphics.Image
import org.lwjgl.opengl.GL11

open class Model(protected vararg val vbos: VBO,
            draw: ((Model) -> Unit)? = null) : Iterable<VBO> {
    var image = Image(-1)
    val maxVertices: Int
        get() {
            var max = 0
            for (vbo in vbos) {
                if (vbo.vertexCount > max) max = vbo.vertexCount
            }
            return max
        }
    var vertexCount = maxVertices
    val draw = draw?: {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, it.image.id)
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, it.vertexCount)
    }

    fun bind() {
        for (vbo in vbos) {
            vbo.bind()
        }
    }

    override fun iterator() = vbos.iterator()

}