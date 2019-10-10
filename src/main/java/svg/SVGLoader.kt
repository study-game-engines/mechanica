package svg

import compatibility.Vector
import loader.loadTextFile
import org.jbox2d.common.Vec2
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import util.extensions.flipVertically
import util.extensions.scale
import util.extensions.toOrigin
import java.lang.NumberFormatException
import java.util.*
import kotlin.collections.ArrayList


fun loadPolygonCoordinatesAdjusted(fileName: String, pathId: String? = null): List<Vector> {
    val doc = loadSVGDocument(fileName)
    val element = if (pathId == null) {
        doc.getElementsByTag("path")[0]
    } else {
        doc.getElementById(pathId)
    }
    val path = loadPolygonCoordinatesAsIs(element)
    path.toOrigin()
    path.scale(1.0/100.0)
    return path
}

fun loadPolygonCoordinatesAsIs(element: Element): List<Vector> {
    var count = 0
    var mode = 0
    var closed = false
    val pathStr = element
            .attr("d")
            .also { count = it.length }
            .replace("M", "")
            .also { mode = if (count == it.length) 1 else 2 }
            .replace("m", "")
            .also { count = it.length }
            .replace("z", "").replace("Z", "")
            .also { closed = count != it.length }
            .trim()

    val lSplit = pathStr.split("L", "C")
    val listOfArrayOfVerts = lSplit.map {lString ->
        val points = lString.trim().split(",", " ").map { it.trim() }
        val size = points.size/2
        val xValues = Array(size) { points[it*2] }
        val yValues = Array(size) { points[it*2+1] }
        val vertices = Array(size) { Vector(xValues[it].toDouble(), yValues[it].toDouble())}
        vertices
    }
    val returnList = ArrayList<Vector>()
    for (array in listOfArrayOfVerts) {
        when (mode) {
            1 -> {
                val vTotal = Vector(0.0, 0.0)
                array.forEach { v ->
                        vTotal.x += v.x
                        vTotal.y += v.y
                        returnList.add(Vector(vTotal.x, vTotal.y))
                }
            }
            2 -> {
                array.forEach { returnList.add(it) }
            }
        }
    }
    if (closed) {
        val first = returnList[0]
        val last = returnList.last()
        if (first != last) {
            returnList.add(Vector(returnList[0].x, returnList[0].y))
        }
    }

    return returnList.flipVertically()
}

fun loadSVGPolygon(fileName: String, pathId: String? = null): SVGPolygon {
    val doc = loadSVGDocument(fileName)
    val element = if (pathId == null) {
        doc.getElementsByTag("path")[0]
    } else {
        doc.getElementById(pathId)
    }

    return SVGPolygon(element)
}

fun loadSVGDocument(fileName: String): Document {
    val file = loadTextFile(fileName)
    return Jsoup.parse(file)
}

fun getWidthAndHeight(element: Element): Vec2 {
    return element.getVector("width", "height")
}

fun Element.getVector(xAttr: String, yAttr: String): Vec2 {
    val xStr = this.attr(xAttr)
    val yStr = this.attr(yAttr)

    val x = try {
        Regex("\\d+").find(xStr)?.value?.toFloat() ?: 0f
    } catch (e: NumberFormatException) { 0f }
    val y = try {
        Regex("\\d+").find(yStr)?.value?.toFloat() ?: 0f
    } catch (e: NumberFormatException) { 0f }
    return Vec2(x, -y)
}
