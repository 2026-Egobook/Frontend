package com.egobook.app.ui.square.model.friend

data class ReplyModel(
    val id: Int,
    val image: Int? = null,
    val level: Int? = null,
    val date: String,
    val question: String,
    val answer: String
)
