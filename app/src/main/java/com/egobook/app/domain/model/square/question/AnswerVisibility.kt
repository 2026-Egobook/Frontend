package com.egobook.app.domain.model.square.question

enum class AnswerVisibility(val value: String, val title: String) {
    PUBLIC("PUBLIC", title = "전체 공개"),
    FRIEND("FRIEND", title = "친구 공개"),
    PRIVATE("PRIVATE", title = "비공개")
}