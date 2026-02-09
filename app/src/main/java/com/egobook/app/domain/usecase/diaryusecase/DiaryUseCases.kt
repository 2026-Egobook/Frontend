package com.egobook.app.domain.usecase.diaryusecase

import androidx.paging.PagingData
import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.model.diary.entity.DiaryFilter
import com.egobook.app.domain.model.diary.entity.DiarySummary
import com.egobook.app.domain.model.diary.entity.DiaryType
import com.egobook.app.domain.repository.diary.DiaryRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

// 의존성 주입을 쉽게 하기 위한 래퍼 클래스
data class DiaryUseCases @Inject constructor (
    val getDiaries: GetDiaries,
    val getDiary: GetDiary,
    val addDiary: AddDiary,
    val updateDiary: UpdateDiary,
    val deleteDiary: DeleteDiary
)

// 각 유스케이스들 정의

class GetDiaries @Inject constructor(
    private val repository: DiaryRepository
) {
    operator fun invoke(filter: DiaryFilter): Flow<PagingData<DiarySummary>> {
        return repository.getDiaries(filter)
    }
}

class GetDiary @Inject constructor(
    private val repository: DiaryRepository
) {
    suspend operator fun invoke(id: Long): Result<Diary> {
        return repository.getDiaryById(id)
    }
}

class AddDiary @Inject constructor(
    private val repository: DiaryRepository
) {
    suspend operator fun invoke(diary: Diary): Result<Unit> {
        return repository.addDiary(diary)
    }
}

class UpdateDiary @Inject constructor(
    private val repository: DiaryRepository
) {
    suspend operator fun invoke(
        diaryId: Long,
        diary: Diary
    ): Result<Unit> {
        return repository.updateDiary(diaryId, diary)
    }
}

class DeleteDiary @Inject constructor(
    private val repository: DiaryRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return repository.deleteDiaryById(id)
    }
}




