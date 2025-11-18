package com.govele.figuras.views.diseno

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.govele.figuras.domain.model.Figura
import com.govele.figuras.domain.model.Punto
import kotlin.math.sqrt

class FiguraSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    private var figura: Figura? = null
    private var onPointSelected: ((Int) -> Unit)? = null
    private var onPointMoved: ((Int, Punto) -> Unit)? = null

    private val pointPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val selectedPointPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val linePaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
    }

    private val pointRadius = 20f
    private var selectedPointIndex = -1

    init {
        holder.addCallback(this)
    }

    fun setFigura(figura: Figura, onPointSelected: (Int) -> Unit, onPointMoved: (Int, Punto) -> Unit) {
        this.figura = figura
        this.onPointSelected = onPointSelected
        this.onPointMoved = onPointMoved
        drawFigura()
    }

    fun setSelectedPoint(index: Int) {
        selectedPointIndex = index
        drawFigura()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        drawFigura()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        drawFigura()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    private fun drawFigura() {
        val canvas = holder.lockCanvas() ?: return
        try {
            drawOnCanvas(canvas)
        } finally {
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun drawOnCanvas(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        val figura = this.figura ?: return

        val points = figura.getPuntosEscalados()
        if (points.size < 2) return

        val path = Path()
        val firstPoint = points.first()
        val screenFirstPoint = toScreenCoordinates(firstPoint, canvas.width, canvas.height)
        path.moveTo(screenFirstPoint.x, screenFirstPoint.y)

        for (i in 1 until points.size) {
            val screenPoint = toScreenCoordinates(points[i], canvas.width, canvas.height)
            path.lineTo(screenPoint.x, screenPoint.y)
        }

        path.close()

        canvas.drawPath(path, linePaint)

        points.forEachIndexed { index, point ->
            val screenPoint = toScreenCoordinates(point, canvas.width, canvas.height)
            val paint = if (index == selectedPointIndex) selectedPointPaint else pointPaint
            canvas.drawCircle(screenPoint.x, screenPoint.y, pointRadius, paint)
        }
    }

    private fun toScreenCoordinates(point: Punto, width: Int, height: Int): android.graphics.Point {
        return android.graphics.Point(
            (point.x * width).toInt(),
            (point.y * height).toInt()
        )
    }

    private fun toLogicalCoordinates(screenPoint: android.graphics.Point, width: Int, height: Int): Punto {
        return Punto(
            screenPoint.x.toFloat() / width,
            screenPoint.y.toFloat() / height
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val touchPoint = android.graphics.Point(event.x.toInt(), event.y.toInt())
                val figura = this.figura ?: return true

                var closestIndex = -1
                var minDistance = Float.MAX_VALUE

                figura.getPuntosEscalados().forEachIndexed { index, logicalPoint ->
                    val screenPoint = toScreenCoordinates(logicalPoint, width, height)
                    val distance = calculateDistance(touchPoint, screenPoint)

                    if (distance < pointRadius * 2 && distance < minDistance) {
                        minDistance = distance
                        closestIndex = index
                    }
                }

                if (closestIndex != -1) {
                    selectedPointIndex = closestIndex
                    onPointSelected?.invoke(closestIndex)
                    drawFigura()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (selectedPointIndex != -1) {
                    val newScreenPoint = android.graphics.Point(event.x.toInt(), event.y.toInt())
                    val newLogicalPoint = toLogicalCoordinates(newScreenPoint, width, height)

                    onPointMoved?.invoke(selectedPointIndex, newLogicalPoint)
                    drawFigura()
                }
            }
            MotionEvent.ACTION_UP -> {
                selectedPointIndex = -1
            }
        }
        return true
    }

    private fun calculateDistance(p1: android.graphics.Point, p2: android.graphics.Point): Float {
        val dx = p1.x - p2.x.toFloat()
        val dy = p1.y - p2.y.toFloat()
        return sqrt(dx * dx + dy * dy)
    }
}