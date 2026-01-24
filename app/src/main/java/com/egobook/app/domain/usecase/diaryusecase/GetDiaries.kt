package com.egobook.app.domain.usecase.diaryusecase

import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.model.DiaryType
import com.egobook.app.domain.repository.DiaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDiaries @Inject constructor(
    private val repository: DiaryRepository
) {
    operator fun invoke(
        types: Set<DiaryType>? = null // null = 전체 탭
    ): Flow<List<Diary>> {
        return repository.getDiaries()
            .map { diaries ->
                diaries
                    .filter { diary ->
                        types == null || diary.types.any { it in types }
                    }
                    .sortedByDescending { it.time }
            }
    }
}
