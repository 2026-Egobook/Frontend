package com.egobook.app.ui.square

import androidx.annotation.DrawableRes
import com.egobook.app.R
import java.util.Locale

@DrawableRes
fun tendencyIconDrawable(topAbilityName: String?): Int = when (topAbilityName?.trim()?.uppercase(Locale.ROOT)) {
    "공감성", "EMPATHY" -> R.drawable.ic_radar_heart
    "자존감", "SELF_ESTEEM" -> R.drawable.ic_radar_diamond
    "성실성", "성실함", "DILIGENCE" -> R.drawable.ic_radar_clover
    "긍정사고", "긍정적 사고", "POSITIVE_THINKING" -> R.drawable.ic_radar_sun
    "감정조절", "EMOTION_REGULATION" -> R.drawable.ic_radar_star
    else -> R.drawable.ic_square_friend_answer_star
}
