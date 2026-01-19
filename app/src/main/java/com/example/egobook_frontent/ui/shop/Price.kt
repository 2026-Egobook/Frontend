package com.example.egobook_frontent.ui.shop

@JvmInline
value class Price(val value: Int) {
    init {
        require(value >= 0) { "가격은 음수일 수 없습니다" }
    }
    override fun toString() = value.toString()
}
