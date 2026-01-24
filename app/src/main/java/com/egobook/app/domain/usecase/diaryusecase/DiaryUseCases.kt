package com.egobook.app.domain.usecase.diaryusecase

data class DiaryUseCases(
    val getDiaries: GetDiaries,
    val getDiary: GetDiary,
    val addDiary: AddDiary,
    val deleteDiary: DeleteDiary
)
