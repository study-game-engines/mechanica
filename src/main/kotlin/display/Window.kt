package display

import gl.utils.ImageData
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.opengl.GL
import org.lwjgl.stb.STBImage
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import resources.Resource
import java.nio.ByteBuffer


class Window private constructor(width: Int, height: Int, val title: String, monitor: Monitor?) {
    val id: Long= glfwCreateWindow(width, height, title, monitor?.id ?: MemoryUtil.NULL, MemoryUtil.NULL)

    val width: Int
        get() = resolution.width
    val height: Int
        get() = resolution.height

    var isFocused: Boolean
        get() = glfwGetWindowAttrib(id, GLFW_FOCUSED) == GLFW_TRUE
        set(value) { if (value) glfwFocusWindow(id) }

    var isIconified: Boolean
        get() = glfwGetWindowAttrib(id, GLFW_ICONIFIED ) == GLFW_TRUE
        set(value) {
            if (value) glfwIconifyWindow(id)
            else glfwRestoreWindow(id) }
    var isMaximized: Boolean
        get() = glfwGetWindowAttrib(id, GLFW_MAXIMIZED ) == GLFW_TRUE
        set(value) {
            if (value) glfwMaximizeWindow(id)
            else glfwRestoreWindow(id) }

    val isHovered: Boolean
        get() = glfwGetWindowAttrib(id, GLFW_HOVERED ) == GLFW_TRUE
    var isVisible: Boolean
        get() = glfwGetWindowAttrib(id, GLFW_VISIBLE ) == GLFW_TRUE
        set(value) {
            if (value) glfwShowWindow(id)
            else glfwHideWindow(id) }
    var isResizable: Boolean
        get() = glfwGetWindowAttrib(id, GLFW_RESIZABLE ) == 1
        set(value) { glfwSetWindowAttrib(id, GLFW_RESIZABLE, if (value) GLFW_TRUE else GLFW_FALSE ) }
    var isDecorated: Boolean
        get() = glfwGetWindowAttrib(id, GLFW_DECORATED ) == 1
        set(value) { glfwSetWindowAttrib(id, GLFW_DECORATED, if (value) GLFW_TRUE else GLFW_FALSE ) }
    var isFloating: Boolean
        get() = glfwGetWindowAttrib(id, GLFW_FLOATING ) == 1
        set(value) { glfwSetWindowAttrib(id, GLFW_FLOATING, if (value) GLFW_TRUE else GLFW_FALSE ) }

    var opacity: Float
        get() = glfwGetWindowOpacity(id)
        set(value) = glfwSetWindowOpacity(id, value)

    var monitor: Monitor? = monitor
        get() {
            val monitor = field
            val monitorId = glfwGetWindowMonitor(id)
            val foundMonitor = Monitor.allMonitors.firstOrNull { it.id == monitorId }
            return if (monitorId == MemoryUtil.NULL){
                field = null
                null
            } else if (monitor != null && monitorId == monitor.id) {
                monitor
            } else foundMonitor?.also { field = it }
        }
        set(value) {
            if (value != null && value != field) {
                setFullscreen(value)
            } else if (value != field) {
                exitFullscreen()
            }
            field = value
        }

    val resolution: Dimension by lazy {
        setResolution(DimensionImpl(0, 0))
    }

    val size: Dimension by lazy {
        setWindowSize(DimensionImpl(0, 0))
    }

    init {
        if (id == MemoryUtil.NULL)
            throw RuntimeException("Failed to create the GLFW window")

        glfwMakeContextCurrent(id)

        GL.createCapabilities()


    }

    fun requestAttention() {
        glfwRequestWindowAttention(id)
    }

    private fun setWindowSize(out: DimensionImpl): Dimension {
        val widthArray = IntArray(1)
        val heightArray = IntArray(1)
        glfwGetWindowSize(id, widthArray, heightArray)
        out.width = widthArray[0]
        out.height = heightArray[0]
        return out
    }

    private fun setResolution(out: DimensionImpl): Dimension {
        val widthArray = IntArray(1)
        val heightArray = IntArray(1)
        glfwGetFramebufferSize(id, widthArray, heightArray)
        out.width = widthArray[0]
        out.height = heightArray[0]
        return out
    }

    fun setIcon(resource: Resource) {
        val image = ImageData(resource)

        if (image.data != null) {
            setIcon(image.width, image.height, image.data)
        }

        image.free()
    }

    fun setIcon(width: Int, height: Int, imageBuffer: ByteBuffer) {
        val image: GLFWImage = GLFWImage.malloc()
        val imagebf: GLFWImage.Buffer = GLFWImage.malloc(1)
        image.set(width, height, imageBuffer)
        imagebf.put(0, image)
        glfwSetWindowIcon(id, imagebf)
    }

    fun setFullscreen(monitor: Monitor) {
        val vidMode = monitor.currentVideoMode
        glfwSetWindowMonitor(id, monitor.id, 0, 0, vidMode.width(), vidMode.height(), vidMode.refreshRate())
    }

    fun setFullscreen(monitor: Monitor, width: Int, height: Int, refreshRate: Int = 60) {
        glfwSetWindowMonitor(id, monitor.id, 0, 0, width, height, refreshRate)
    }

    fun exitFullscreen() {
        if (this.monitor != null) {
            val vidMode = Monitor.getPrimaryMonitor().currentVideoMode
            glfwSetWindowMonitor(id, MemoryUtil.NULL, 0, 0, vidMode.width(), vidMode.height(), 0)
        }
    }

    interface Dimension {
        val width: Int
        val height: Int
    }
    private data class DimensionImpl(override var width: Int, override var height: Int) : Dimension

    companion object {
        fun create(title: String, width: Int, height: Int): Window {
            return Window(width, height, title, null)
        }

        fun create(title: String, monitor: Monitor): Window {
            val vidMode = monitor.currentVideoMode
            val width = vidMode.width()
            val height = vidMode.height()
            glfwWindowHint(GLFW_RED_BITS, vidMode.redBits())
            glfwWindowHint(GLFW_GREEN_BITS, vidMode.greenBits())
            glfwWindowHint(GLFW_BLUE_BITS, vidMode.blueBits())
            glfwWindowHint(GLFW_REFRESH_RATE, vidMode.refreshRate())

            return Window(width, height, title, monitor)
        }

        fun create(title: String, width: Int, height: Int, monitor: Monitor): Window {
            return Window(width, height, title, monitor)
        }
    }
}