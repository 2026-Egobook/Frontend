package com.egobook.app.domain.usecase.diaryusecase

import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.model.DiaryType
import com.egobook.app.domain.repository.DiaryRepository
import javax.inject.Inject

class AddDiary @Inject constructor(
    private val repository: DiaryRepository
) {
    suspend operator fun invoke(
        content: String,
        types: Set<DiaryType>,
        emotionLevel: Int? // 1~5 사이의 감정 레벨
    ): Result<Diary> {
        return repository.addDiary(
            content = content,
            types = types,
            emotionLevel = emotionLevel
        )
    }
}