package com.egobook.app.domain.usecase.diaryusecase

import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.model.diary.entity.DiaryFilter
import com.egobook.app.domain.model.diary.entity.DiarySummary
import com.egobook.app.domain.model.diary.entity.DiaryType
import com.egobook.app.domain.repository.diary.FakeDiaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
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
    private val repository: FakeDiaryRepository
) {
    operator fun invoke(filter: DiaryFilter): Flow<List<DiarySummary>> {
        return repository.getDiaries(filter)
    }
}

class GetDiary @Inject constructor(
    private val repository: FakeDiaryRepository
) {
    suspend operator fun invoke(id: Long): Result<Diary?> {
        return repository.getDiaryById(id)
    }
}

class AddDiary @Inject constructor(
    private val repository: FakeDiaryRepository
) {
    suspend operator fun invoke(
        content: String,
        types: Set<DiaryType>,
        emotionLevel: Int?, // 1~5 사이의 감정 레벨
        createdAt: LocalDateTime // 일기가 귀속될 날짜
    ): Result<Diary> {
        return repository.addDiary(
            content = content,
            types = types,
            emotionLevel = emotionLevel,
            createdAt = createdAt
        )
    }
}

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

class DeleteDiary @Inject constructor(
    private val repository: FakeDiaryRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return repository.deleteDiaryById(id)
    }
}




