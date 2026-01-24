package com.example.egobook_frontent.ui.square.model

data class ReplyModel(
    val id: Int,
    val image: Int? = null,
    val level: Int? = null,
    val date: String,
    val question: String,
    val answer: String
)
