package com.egobook.app.domain.usecase.diaryusecase

import javax.inject.Inject

// 의존성 주입을 쉽게 하기 위한 래퍼 클래스
data class DiaryUseCases @Inject constructor (
    val getDiaries: GetDiaries,
    val getDiary: GetDiary,
    val addDiary: AddDiary,
    val deleteDiary: DeleteDiary
)
