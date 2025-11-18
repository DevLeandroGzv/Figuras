package com.govele.figuras.views.diseno.canvas


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.govele.figuras.domain.model.Figura
import com.govele.figuras.domain.model.Punto
import kotlin.math.sqrt

@Composable
fun FiguraCanvas(
    figura: Figura?,
    puntoSeleccionado: Int,
    onPointSelected: (Int) -> Unit,
    onPointMoved: (Int, Punto) -> Unit,
    isEditing: Boolean,
    onStopEditing: () -> Unit,
    modifier: Modifier = Modifier
) {
    var canvasSize by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .background(Color.White)
            .pointerInput(Unit) {
                if (!isEditing) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            figura?.let { fig ->
                                val puntoIndex = findClosestPoint(
                                    offset,
                                    fig.getPuntosEscalados(),
                                    canvasSize.x,
                                    canvasSize.y
                                )
                                if (puntoIndex != -1) {
                                    onPointSelected(puntoIndex)
                                }
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            if (puntoSeleccionado != -1) {
                                val newOffset = change.position
                                val logicalPoint = toLogicalCoordinates(
                                    newOffset,
                                    canvasSize.x,
                                    canvasSize.y
                                )
                                onPointMoved(puntoSeleccionado, logicalPoint)
                            }
                        },
                        onDragEnd = {
                            onStopEditing()
                        }
                    )
                }
            }
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize(),
            onDraw = {
                canvasSize = Offset(size.width, size.height)
                figura?.let { drawFigura(it, puntoSeleccionado, size) }
            }
        )
    }
}

private fun DrawScope.drawFigura(
    figura: Figura,
    puntoSeleccionado: Int,
    size: Size
) {
    val puntos = figura.getPuntosEscalados()
    if (puntos.size < 2) return

    // Dibujar líneas
    val path = Path().apply {
        val firstPoint = toCanvasCoordinates(puntos.first(), size.width, size.height)
        moveTo(firstPoint.x, firstPoint.y)

        for (i in 1 until puntos.size) {
            val point = toCanvasCoordinates(puntos[i], size.width, size.height)
            lineTo(point.x, point.y)
        }
        close()
    }

    drawPath(
        path = path,
        color = Color.Black,
        style = Stroke(width = 3.dp.toPx())
    )

    // Dibujar puntos
    puntos.forEachIndexed { index, punto ->
        val canvasPoint = toCanvasCoordinates(punto, size.width, size.height)
        val color = if (index == puntoSeleccionado) Color.Red else Color.Blue
        val radius = if (index == puntoSeleccionado) 12.dp.toPx() else 8.dp.toPx()

        drawCircle(
            color = color,
            center = canvasPoint,
            radius = radius
        )
    }
}

private fun toCanvasCoordinates(punto: Punto, width: Float, height: Float): Offset {
    return Offset(punto.x * width, punto.y * height)
}

private fun toLogicalCoordinates(offset: Offset, width: Float, height: Float): Punto {
    return Punto(
        x = (offset.x / width).coerceIn(0f, 1f),
        y = (offset.y / height).coerceIn(0f, 1f)
    )
}

private fun findClosestPoint(
    touchOffset: Offset,
    puntos: List<Punto>,
    width: Float,
    height: Float
): Int {
    var closestIndex = -1
    var minDistance = Float.MAX_VALUE
    val touchRadius = 20.dp.toPx()

    puntos.forEachIndexed { index, punto ->
        val canvasPoint = toCanvasCoordinates(punto, width, height)
        val distance = calculateDistance(touchOffset, canvasPoint)

        if (distance < touchRadius && distance < minDistance) {
            minDistance = distance
            closestIndex = index
        }
    }

    return closestIndex
}

private fun calculateDistance(p1: Offset, p2: Offset): Float {
    val dx = p1.x - p2.x
    val dy = p1.y - p2.y
    return sqrt(dx * dx + dy * dy)
}
