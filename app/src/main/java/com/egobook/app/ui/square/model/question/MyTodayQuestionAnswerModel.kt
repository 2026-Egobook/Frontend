package com.egobook.app.ui.square.model.question

import com.egobook.app.domain.model.square.question.MyTodayQuestionAnswer
import com.egobook.app.domain.model.square.question.MyTodayQuestionAnswerItem

data class MyTodayQuestionAnswerModel(
    val content: List<MyTodayQuestionAnswerItemModel>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean
)

data class MyTodayQuestionAnswerItemModel(
    val questionId: Long,
    val questionDate: String,
    val questionContent: String,
    val answerId: Long,
    val answerContent: String,
)

fun MyTodayQuestionAnswer.toPresentation(): MyTodayQuestionAnswerModel = MyTodayQuestionAnswerModel(
    content = content.map { it.toPresentation() },
    page = page,
    size = size,
    hasNext = hasNext
)

fun MyTodayQuestionAnswerItem.toPresentation(): MyTodayQuestionAnswerItemModel = MyTodayQuestionAnswerItemModel(
    questionId = questionId,
    questionDate = questionDate,
    questionContent = questionContent,
    answerId = answerId,
    answerContent = answerContent
)


