package com.egobook.app.ui.home.user

import com.egobook.app.ui.home.user.LevelType

data class Level(val number: Int) {
    val type: LevelType = LevelType.Companion.of(number)
    init {
        check(number in MINIMUM_LEVEL..MAXIMUM_LEVEL) { "올바른 레벨 범위가 아닙니다." }
    }
    companion object {
        const val MAXIMUM_LEVEL = 1500
        const val MINIMUM_LEVEL = 0
    }
}
