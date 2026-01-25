package com.egobook.app.domain.usecase.diaryusecase

import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.model.DiaryType
import com.egobook.app.domain.repository.DiaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class GetDiaries @Inject constructor(
    private val repository: DiaryRepository
) {
    operator fun invoke(
        selectedDate: LocalDateTime = LocalDateTime.now(),
        types: Set<DiaryType>? = null // null = 전체 탭
    ): Flow<List<Diary>> {
        return repository.getDiaries()
            .map { diaries ->
                diaries
                    .filter { diary ->
                        val isSameDate = diary.createdAt.year == selectedDate.year &&
                                diary.createdAt.month == selectedDate.month &&
                                diary.createdAt.dayOfMonth == selectedDate.dayOfMonth
                        val isCorrectType = types == null || diary.types.any { it in types }

                        isSameDate && isCorrectType
                    }
                    .sortedByDescending { it.updatedAt }
            }
    }
}
