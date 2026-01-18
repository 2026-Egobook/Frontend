package com.example.egobook.ui.counseling.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class WordBubbleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val bubbles = mutableListOf<Bubble>()
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
    }

    data class Bubble(val text: String, var radius: Float, val color: Int, var x: Float = 0f, var y: Float = 0f)

    fun setWords(words: List<Pair<String, Int>>) {
        val colors = listOf("#FFEBEE", "#E3F2FD", "#FFF3E0", "#E8F5E9", "#F3E5F5")
        bubbles.clear()
        words.sortedByDescending { it.second }.forEachIndexed { i, pair ->
            val radius = pair.second * 12f + 50f
            bubbles.add(Bubble(pair.first, radius, Color.parseColor(colors[i % colors.size])))
        }
        // 데이터가 바뀌면 위치를 다시 계산하도록 함
        calculateLayout()
        invalidate()
    }

    // View의 크기가 결정되거나 변경될 때 레이아웃 재계산
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateLayout()
    }

    private fun calculateLayout() {
        if (bubbles.isEmpty() || width == 0 || height == 0) return

        val centerX = width / 2f
        val centerY = height / 2f

        // 1. 먼저 원래 크기로 배치
        bubbles.forEachIndexed { i, bubble ->
            if (i == 0) {
                bubble.x = centerX
                bubble.y = centerY
            } else {
                var angle = i * 1.5f
                var distance = 10f
                while (true) {
                    val nx = centerX + (distance * cos(angle.toDouble())).toFloat()
                    val ny = centerY + (distance * sin(angle.toDouble())).toFloat()
                    val overlap = bubbles.take(i).any {
                        hypot((nx - it.x).toDouble(), (ny - it.y).toDouble()) < (bubble.radius + it.radius + 8)
                    }
                    if (!overlap) {
                        bubble.x = nx; bubble.y = ny; break
                    }
                    distance += 5f
                    angle += 0.1f
                }
            }
        }

        // 2. 뷰 크기에 맞춰 스케일 조정
        val minX = bubbles.minOf { it.x - it.radius }
        val maxX = bubbles.maxOf { it.x + it.radius }
        val minY = bubbles.minOf { it.y - it.radius }
        val maxY = bubbles.maxOf { it.y + it.radius }

        val contentWidth = maxX - minX
        val contentHeight = maxY - minY

        // 뷰의 90% 크기 안에 들어오도록 비율 계산
        val scale = minOf((width * 0.9f) / contentWidth, (height * 0.9f) / contentHeight, 1.0f)

        // 모든 좌표와 반지름을 스케일에 맞춰 재설정
        bubbles.forEach { bubble ->
            bubble.x = centerX + (bubble.x - centerX) * scale
            bubble.y = centerY + (bubble.y - centerY) * scale
            bubble.radius *= scale // 반지름도 줄임
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bubbles.forEach {
            circlePaint.color = it.color
            canvas.drawCircle(it.x, it.y, it.radius, circlePaint)

            textPaint.textSize = it.radius * 0.35f
            // 텍스트 수직 중앙 정렬 보정
            val fontMetrics = textPaint.fontMetrics
            val baseline = it.y - (fontMetrics.ascent + fontMetrics.descent) / 2f
            canvas.drawText(it.text, it.x, baseline, textPaint)
        }
    }
}