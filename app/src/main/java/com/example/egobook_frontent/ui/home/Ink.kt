package com.example.egobook_frontent.ui.home

data class Ink(val value: Int) {
    init {
        check(value >= MINIMUM_INK) { "잉크는 음수가 될 수 없습니다." }
    }

    companion object {
        const val MINIMUM_INK = 0
    }
}
