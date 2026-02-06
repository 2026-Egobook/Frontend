package com.egobook.app.domain.usecase.diaryusecase

import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.repository.FakeDiaryRepository
import javax.inject.Inject

class GetDiary @Inject constructor(
    private val repository: FakeDiaryRepository
) {
    suspend operator fun invoke(id: Long): Result<Diary?> {
        return repository.getDiaryById(id)
    }
}
