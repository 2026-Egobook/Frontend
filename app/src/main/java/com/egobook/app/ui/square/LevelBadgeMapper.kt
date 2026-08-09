package com.egobook.app.ui.square

import androidx.annotation.DrawableRes
import com.egobook.app.R
import com.egobook.app.ui.home.user.Level
import com.egobook.app.ui.home.user.LevelType

@DrawableRes
fun levelBadgeDrawable(level: Long): Int {
    require(level in Level.MINIMUM_LEVEL.toLong()..Level.MAXIMUM_LEVEL.toLong())
    return when (LevelType.of(level.toInt())) {
        LevelType.ONE -> R.drawable.level_type_1
        LevelType.TWO -> R.drawable.level_type_2
        LevelType.THREE -> R.drawable.level_type_3
        LevelType.FOUR -> R.drawable.level_type_4
        LevelType.FIVE -> R.drawable.level_type_5
        LevelType.SIX -> R.drawable.level_type_6
        LevelType.SEVEN -> R.drawable.level_type_7
        LevelType.EIGHT -> R.drawable.level_type_8
    }
}
