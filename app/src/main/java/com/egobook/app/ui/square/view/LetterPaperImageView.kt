package com.egobook.app.ui.square.view

import android.content.Context
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/** Keep artwork full-width and anchored at the bottom, even when the letter height changes. */
class LetterPaperImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {
    init {
        scaleType = ScaleType.MATRIX
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        positionArtwork()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        positionArtwork()
    }

    private fun positionArtwork() {
        val art = drawable ?: return
        if (width <= 0 || height <= 0 || art.intrinsicWidth <= 0 || art.intrinsicHeight <= 0) return
        val scale = width.toFloat() / art.intrinsicWidth
        imageMatrix = Matrix().apply {
            setScale(scale, scale)
            postTranslate(0f, height - art.intrinsicHeight * scale)
        }
    }
}
