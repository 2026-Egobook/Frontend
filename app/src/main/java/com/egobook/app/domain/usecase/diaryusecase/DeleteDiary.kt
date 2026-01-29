package com.egobook.app.domain.usecase.diaryusecase

import com.egobook.app.domain.repository.DiaryRepository
import javax.inject.Inject

class DeleteDiary @Inject constructor(
    private val repository: DiaryRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return repository.deleteDiaryById(id)
    }
}