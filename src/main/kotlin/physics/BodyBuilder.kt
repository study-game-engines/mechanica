@file:Suppress("unused") // There will be many functions here that go unused most of the time

package physics


import display.Game
import org.jbox2d.collision.shapes.EdgeShape
import org.jbox2d.collision.shapes.Shape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.FixtureDef

class BodyBuilder<T : Shape>(private val shape: T) {
    private var position = Vec2(0f, 0f)
    private var density = 1.0f
    private var friction = 0.3f
    private var fixedRotation = false
    private var bodyType = BodyType.DYNAMIC
    private var userData: Int? = null
    private var vertices: Array<Vec2>? = null
    private var maskBits = 0xFFFF
    private var categoryBits = 0x0001

    fun setPosition(position: Vec2): BodyBuilder<T> {
        this.position = position
        return this
    }

    fun setDensity(density: Float): BodyBuilder<T> {
        this.density = density
        return this
    }

    fun setFriction(friction: Float): BodyBuilder<T> {
        this.friction = friction
        return this
    }

    fun setFixedRotation(fixedRotation: Boolean): BodyBuilder<T> {
        this.fixedRotation = fixedRotation
        return this
    }

    fun setUserData(userData: Int?): BodyBuilder<T> {
        this.userData = userData
        return this
    }

    fun setBodyType(type: BodyType): BodyBuilder<T> {
        this.bodyType = type
        return this
    }

    fun setVertices(vertices: Array<Vec2>): BodyBuilder<T> {
        this.vertices = vertices
        return this
    }

    fun setShape(setter: T.() -> Unit): BodyBuilder<T> {
        setter(shape)
        return this
    }

    fun setCollisionFilter(maskBits: Int = 0x0001, categoryBits: Int = 0xFFFF): BodyBuilder<T> {
        this.maskBits = maskBits
        this.categoryBits = categoryBits
        return this
    }

    fun build(): Body {
        val bodyDef = BodyDef()
        val fixtureDef = FixtureDef()

        bodyDef.position.set(position)
        bodyDef.type = bodyType
        bodyDef.fixedRotation = fixedRotation
        val body = Game.world.createBody(bodyDef)

        fixtureDef.shape = shape
        fixtureDef.density = density
        fixtureDef.friction = friction
        fixtureDef.filter.categoryBits = categoryBits
        fixtureDef.filter.maskBits = maskBits

        val vertices = this.vertices
        if (vertices != null && shape is EdgeShape) {
            for (i in 0..vertices.size - 2) {
                shape.set(vertices[i], vertices[i + 1])
                body.createFixture(fixtureDef)
            }
        } else {
            body.createFixture(fixtureDef)
        }

        if (userData != null) body.userData = userData

        return body
    }

}