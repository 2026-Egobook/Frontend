package com.egobook.app.domain.usecase.diaryusecase

import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.repository.DiaryRepository
import javax.inject.Inject

class GetDiary @Inject constructor(
    private val repository: DiaryRepository
) {
    suspend operator fun invoke(id: Long): Result<Diary?> {
        return repository.getDiaryById(id)
    }
}
