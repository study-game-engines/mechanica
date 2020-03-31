package game.view

import game.Game
import game.configuration.GameSetup
import util.units.MutableVector
import util.units.Vector

class GameView(data: GameSetup): View {
    private var _width: Double = data.viewWidth
    override var width: Double
        get() = _width
        set(value) {
            _width = value
            if (lockRatio) {
                _height = value/ratio
            } else {
                ratio = value/_height
            }
            gameMatrices.updateView(this)
        }

    private var _height: Double = data.viewHeight
    override var height: Double
        get() = _height
        set(value) {
            _height = value
            if (lockRatio) {
                _width = value*ratio
            } else {
                ratio = _width/value
            }
            gameMatrices.updateView(this)
        }

    override var x: Double = data.viewX
        set(value) {
            field = value
            gameMatrices.updateView(this)
        }
    override var y: Double = data.viewY
        set(value) {
            field = value
            gameMatrices.updateView(this)
        }

    override var ratio: Double = height/width
        private set
        get() {
            return if (lockRatio) {
                Game.window.aspectRatio
            } else field
        }
    var lockRatio = true

    override val center: Vector = MutableVector(x + width/2.0, y + height/2.0)
        get() {
            val vector = field as MutableVector
            vector.x = x + width/2.0
            vector.y = y + height/2.0
            return vector
        }

    private val gameMatrices: GameMatrices
        get() = Game.matrices as GameMatrices
}