package com.egobook.app.ui.square.model.question

import com.egobook.app.domain.model.square.question.AnswerVisibility
import com.egobook.app.domain.model.square.question.TodayAnswer

data class TodayAnswerModel(
    val content: String,
    val visibilityType: AnswerVisibility
)

fun TodayAnswer.toPresentation(): TodayAnswerModel = TodayAnswerModel(
    content = content,
    visibilityType = visibilityType
)

fun TodayAnswerModel.toDomain(): TodayAnswer = TodayAnswer(
    content = content,
    visibilityType = visibilityType
)