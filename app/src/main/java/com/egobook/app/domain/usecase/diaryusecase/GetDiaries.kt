package com.egobook.app.domain.usecase.diaryusecase

import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.repository.DiaryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDiaries @Inject constructor(
    private val repository: DiaryRepository
) {
    operator fun invoke(): Flow<List<Diary>> {
        return repository.getDiaries()
    }
}
