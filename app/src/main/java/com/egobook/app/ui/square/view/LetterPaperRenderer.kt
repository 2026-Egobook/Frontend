package com.egobook.app.ui.square.view

import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import coil.load
import com.egobook.app.R
import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.google.android.material.card.MaterialCardView

/** Render the sender's paper independently of the viewer's purchased items. */
internal fun MaterialCardView.renderLetterPaper(color: LetterBackgroundColor, imageUrl: String?) {
    val colorRes = when (color) {
        LetterBackgroundColor.WHITE -> R.color.letter_bg_beige
        LetterBackgroundColor.PINK -> R.color.letter_bg_pink
        LetterBackgroundColor.GREEN -> R.color.letter_bg_green
        LetterBackgroundColor.BLUE -> R.color.letter_bg_blue
        LetterBackgroundColor.PURPLE -> R.color.letter_bg_purple
    }
    setCardBackgroundColor(ContextCompat.getColor(context, colorRes))
    val image = findViewWithTag<ImageView>(PAPER_IMAGE_TAG) ?: LetterPaperImageView(context).also {
        // Keep text padding while letting the artwork reach the paper's edges.
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            child.setPadding(
                child.paddingLeft + contentPaddingLeft,
                child.paddingTop + contentPaddingTop,
                child.paddingRight + contentPaddingRight,
                child.paddingBottom + contentPaddingBottom
            )
        }
        setContentPadding(0, 0, 0, 0)
        it.tag = PAPER_IMAGE_TAG
        it.importantForAccessibility = ImageView.IMPORTANT_FOR_ACCESSIBILITY_NO
        addView(it, 0, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
    }
    image.load(imageUrl?.takeIf { it.isNotBlank() })
}

private const val PAPER_IMAGE_TAG = "letter_paper_background"
