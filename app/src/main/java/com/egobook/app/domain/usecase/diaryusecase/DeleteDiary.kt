package com.egobook.app.domain.usecase.diaryusecase

import com.egobook.app.domain.repository.FakeDiaryRepository
import javax.inject.Inject

class DeleteDiary @Inject constructor(
    private val repository: FakeDiaryRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return repository.deleteDiaryById(id)
    }
}