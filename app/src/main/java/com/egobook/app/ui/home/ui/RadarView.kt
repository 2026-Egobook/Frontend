package com.egobook.app.ui.home.ui

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class RadarView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val diameters = listOf(34f.toPx(), 67f.toPx(), 100f.toPx(), 134f.toPx(), 168f.toPx())
    private val maxDiameter = diameters.max()

    private val colors = listOf(
        Color.parseColor("#DDE6C4"),
        Color.parseColor("#C8DCF0"),
        Color.parseColor("#F9E8B0"),
        Color.parseColor("#C6E9E4"),
        Color.parseColor("#EEC0C0")
    )

    private val strokePaint = Paint().apply {
        color = Color.parseColor("#DBDBDB")
        style = Paint.Style.STROKE
        strokeWidth = 1f.toPx()
        isAntiAlias = true
    }
    private val fillPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val pieceCount = 5
    private val sweepAngle = 360f / pieceCount
    private val rectF = RectF()

    private var levels: List<Int> = (1..5).map { 0 }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        diameters.forEach { canvas.drawCircle(centerX, centerY, it / 2f, strokePaint) }

        val maxRadius = maxDiameter / 2f
        for (i in 0 until pieceCount) {
            val angle = Math.toRadians((i * sweepAngle - 54).toDouble())
            val stopX = centerX + (maxRadius * cos(angle)).toFloat()
            val stopY = centerY + (maxRadius * sin(angle)).toFloat()
            canvas.drawLine(centerX, centerY, stopX, stopY, strokePaint)
        }

        fillRadar(canvas, centerX, centerY )
    }

    private fun fillRadar(canvas: Canvas, centerX: Float, centerY: Float) {
        levels.forEachIndexed { target, level ->
            if (level != 0) {
                val radius = diameters[level - 1] / 2f
                rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

                val startAngle = (target * sweepAngle) - 54f

                fillPaint.color = colors[target]
                fillPaint.alpha = 200
                canvas.drawArc(rectF, startAngle, sweepAngle, true, fillPaint)
            }
        }
    }

    fun setRadarData(levels: List<Int>) {
        this.levels = levels
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = (maxDiameter + strokePaint.strokeWidth).toInt()

        val width = resolveSize(desiredSize, widthMeasureSpec)
        val height = resolveSize(desiredSize, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }

    private fun Float.toPx() = (this * Resources.getSystem().displayMetrics.density)
}
