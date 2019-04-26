package physics

import display.Game
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.joints.Joint
import org.jbox2d.dynamics.joints.JointDef
import org.jbox2d.dynamics.joints.PrismaticJointDef
import org.jbox2d.dynamics.joints.RevoluteJointDef
//https://www.iforce2d.net/b2dtut/joints-overview


//https://www.iforce2d.net/b2dtut/joints-revolute
fun Body.attachRevoluteJoint(
        body: Body,
        anchorA: Vec2,
        anchorB: Vec2 = Vec2(),
        collision: Boolean = false,
        motorTorque: Double? = null,
        motorRadsPerSec: Double? = null // Radians per second
        ): JointProperties<RevoluteJointDef> {

    val def = RevoluteJointDef()

    def.collideConnected = collision
    def.bodyA = this
    def.bodyB = body

    def.localAnchorA = anchorA
    def.localAnchorB = anchorB
    if (motorTorque != null || motorRadsPerSec != null) {
        def.enableMotor = true
        def.maxMotorTorque = (motorTorque?: 0.0).toFloat()
        def.motorSpeed = (motorRadsPerSec?: 0.0).toFloat()
    }

    return JointProperties(def)
}

//https://www.iforce2d.net/b2dtut/joints-prismatic
fun Body.attachPrismaticJoint(
        body: Body,
        anchorA: Vec2,
        anchorB: Vec2 = Vec2(),
        collision: Boolean = false,
        motorForce: Double? = null,
        motorSpeed: Double? = null // Radians per second
): JointProperties<PrismaticJointDef> {

    val def = PrismaticJointDef()

    def.collideConnected = collision
    def.bodyA = this
    def.bodyB = body

    def.localAnchorA.set(anchorA)
    def.localAnchorB.set(anchorB)
    if (motorForce != null || motorSpeed != null) {
        def.enableMotor = true
        def.maxMotorForce = (motorForce?: 0.0).toFloat()
        def.motorSpeed = (motorSpeed?: 0.0).toFloat()
    }

    return JointProperties(def)
}

class JointProperties<T : JointDef>(val jointDef: T) {
    val joint: Joint = Game.world.createJoint(jointDef)

    var motorSpeed: Float
        get() = if (joint is RevoluteJointDef) {
            joint.motorSpeed
        } else 0f
        set(value) {
            if (joint is RevoluteJointDef) {
                joint.motorSpeed = value
            }
        }
}
