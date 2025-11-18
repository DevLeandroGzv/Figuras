// ui/diseno/canvas/FiguraCanvas.kt
package com.govele.figuras.ui.diseno.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import com.govele.figuras.domain.model.Figura
import com.govele.figuras.domain.model.Punto
import kotlin.math.sqrt
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

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
    var canvasSize by remember { mutableStateOf(Size(0f, 0f)) }

    Box(
        modifier = modifier
            .background(Color.White)
            .pointerInput(figura, puntoSeleccionado) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main)
                        val changes = event.changes

                        for (change in changes) {
                            val position = change.position

                            when {
                                // PRIMER TOQUE - Seleccionar punto
                                change.pressed && !change.previousPressed -> {
                                    println("👆 TOUCH START: $position")
                                    figura?.let { fig ->
                                        val puntoIndex = findClosestPoint(
                                            position,
                                            fig.getPuntosEscalados(),
                                            canvasSize.width,
                                            canvasSize.height
                                        )
                                        if (puntoIndex != -1) {
                                            println("🎯 PUNTO SELECCIONADO: $puntoIndex")
                                            onPointSelected(puntoIndex)
                                        }
                                    }
                                }

                                // MOVIMIENTO - Arrastrar punto
                                change.pressed && change.positionChanged() -> {
                                    if (puntoSeleccionado != -1) {
                                        val logicalPoint = toLogicalCoordinates(
                                            position,
                                            canvasSize.width,
                                            canvasSize.height
                                        )
                                        // ⚡ ACTUALIZACIÓN EN TIEMPO REAL
                                        onPointMoved(puntoSeleccionado, logicalPoint)
                                    }
                                }

                                // FINALIZAR
                                !change.pressed && change.previousPressed -> {
                                    println("🏁 TOUCH END")
                                    onStopEditing()
                                }
                            }

                            change.consume()
                        }
                    }
                }
            }
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize(),
            onDraw = {
                canvasSize = size
                drawFigura(figura, puntoSeleccionado, size)
            }
        )
    }
}


private fun DrawScope.drawFigura(
    figura: Figura?,
    puntoSeleccionado: Int,
    size: Size,
    isDragging: Boolean = false,
    dragPosition: Offset = Offset.Zero
) {
    if (figura == null) return

    val puntos = figura.getPuntosEscalados()

    // Dibujar líneas entre puntos
    if (puntos.size >= 2) {
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
            style = Stroke(width = 4.dp.toPx())
        )
    }

    // Dibujar puntos
    puntos.forEachIndexed { index, punto ->
        val canvasPoint = toCanvasCoordinates(punto, size.width, size.height)
        var color = when {
            index == puntoSeleccionado && isDragging -> Color.Green
            index == puntoSeleccionado -> Color.Red
            else -> Color.Blue
        }
        val radius = if (index == puntoSeleccionado) 35f else 25f

        drawCircle(
            color = color,
            center = canvasPoint,
            radius = radius
        )

        // Número del punto
        drawContext.canvas.nativeCanvas.drawText(
            index.toString(),
            canvasPoint.x + 40f,
            canvasPoint.y + 12f,
            android.graphics.Paint().apply {
                color = Color.Black
                textSize = 32f
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
    }

    // Dibujar preview durante arrastre (feedback visual instantáneo)
    if (isDragging && puntoSeleccionado != -1) {
        drawCircle(
            color = Color.Green.copy(alpha = 0.3f),
            center = dragPosition,
            radius = 40f
        )
    }
}

private fun toCanvasCoordinates(punto: Punto, width: Float, height: Float): Offset {
    return Offset(punto.x * width, punto.y * height)
}

private fun toLogicalCoordinates(offset: Offset, width: Float, height: Float): Punto {
    return Punto(
        x = (offset.x / width).coerceIn(0.02f, 0.98f),
        y = (offset.y / height).coerceIn(0.02f, 0.98f)
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
    val touchRadius = 120f

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