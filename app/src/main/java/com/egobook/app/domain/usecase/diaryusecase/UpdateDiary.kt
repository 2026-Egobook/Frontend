package com.egobook.app.domain.usecase.diaryusecase

import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.model.DiaryType
import com.egobook.app.domain.repository.FakeDiaryRepository
import javax.inject.Inject

class UpdateDiary @Inject constructor(
    private val repository: FakeDiaryRepository
) {
    suspend operator fun invoke(
        id: Long,
        content: String,
        types: Set<DiaryType>,
        emotionLevel: Int?
    ): Result<Diary> {
        return repository.updateDiary(
            id = id,
            content = content,
            types = types,
            emotionLevel = emotionLevel
        )
    }
}
