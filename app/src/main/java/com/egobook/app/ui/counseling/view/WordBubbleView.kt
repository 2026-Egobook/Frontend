package com.egobook.app.ui.counseling.view

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
        val colors = listOf("#FFEBEE", "#E3F2FD", "#FFF3E0", "#E8F5E9", "#F3E5F5", "#FCF8E8")
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
        // 간격을 더 크게 확보 (기본 gap을 20dp 정도로 높이고 스케일 영향 고려)
        val gap = 20f * resources.displayMetrics.density

        // 1. 초기 배치 (나선형으로 배치하되 간격을 넉넉히 둠)
        bubbles.forEachIndexed { i, bubble ->
            if (i == 0) {
                bubble.x = centerX
                bubble.y = centerY
            } else {
                var angle = i * 1.5f
                var distance = bubble.radius + gap // 시작 거리를 본인 반지름만큼 띄우고 시작
                while (true) {
                    val nx = centerX + (distance * cos(angle.toDouble())).toFloat()
                    val ny = centerY + (distance * sin(angle.toDouble())).toFloat()

                    val overlap = bubbles.take(i).any {
                        hypot((nx - it.x).toDouble(), (ny - it.y).toDouble()) < (bubble.radius + it.radius + gap)
                    }

                    if (!overlap) {
                        bubble.x = nx; bubble.y = ny; break
                    }
                    distance += 10f // 탐색 거리를 더 빠르게 증가
                    angle += 0.2f
                }
            }
        }

        // 2. 전체 경계 계산
        val minX = bubbles.minOf { it.x - it.radius }
        val maxX = bubbles.maxOf { it.x + it.radius }
        val minY = bubbles.minOf { it.y - it.radius }
        val maxY = bubbles.maxOf { it.y + it.radius }

        val contentWidth = maxX - minX
        val contentHeight = maxY - minY
        val contentCenterX = (minX + maxX) / 2f
        val contentCenterY = (minY + maxY) / 2f

        // 3. 스케일 계산 (여백을 0.75로 줄여서 전체적으로 더 작게 만들어 간격이 잘 보이게 함)
        val paddingScale = 0.75f
        val scale = minOf(
            (width * paddingScale) / contentWidth,
            (height * paddingScale) / contentHeight,
            1.0f
        )

        // 4. 최종 좌표 적용 및 '고정된' 간격 추가 보정
        bubbles.forEach { bubble ->
            // 중심점 기준으로 스케일 적용하여 이동
            bubble.x = centerX + (bubble.x - contentCenterX) * scale
            bubble.y = centerY + (bubble.y - contentCenterY) * scale

            // 반지름은 줄어들지만, 위에서 gap을 크게 잡았기 때문에
            // 시각적으로는 원들 사이의 거리가 유지됩니다.
            bubble.radius *= scale
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