package com.egobook.app.domain.usecase.diaryusecase

import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.repository.DiaryRepository
import javax.inject.Inject

class DeleteDiary @Inject constructor(
    private val repository: DiaryRepository
) {
    suspend operator fun invoke(diary: Diary): Result<Unit> {
        return repository.deleteDiary(diary)
    }
}
