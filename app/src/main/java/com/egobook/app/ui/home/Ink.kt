package com.egobook.app.ui.home

data class Ink(val value: Int) {
    init {
        check(value >= MINIMUM_INK) { "잉크는 음수가 될 수 없습니다." }
    }

    companion object {
        const val MINIMUM_INK = 0
    }
}
