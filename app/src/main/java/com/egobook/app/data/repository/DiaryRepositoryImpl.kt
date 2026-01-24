package com.egobook.app.data.repository

import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.model.DiaryType
import com.egobook.app.domain.model.EmotionLevel
import com.egobook.app.domain.repository.DiaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalDateTime

class DiaryRepositoryImpl : DiaryRepository {

    //더미데이터 삽입
    private val diariesFlow = MutableStateFlow(
        listOf(
            Diary(
                id = 1L,
                content = "오늘은 생각보다 괜찮은 하루였다.",
                types = setOf(DiaryType.EMOTION),
                date = LocalDate.now(),
                time = LocalDateTime.now().minusHours(2),
                emotionLevel = EmotionLevel.GOOD // OK
            ),
            Diary(
                id = 2L,
                content = "진로에 대해 계속 고민만 하다 하루가 갔다.",
                types = setOf(DiaryType.WORRY),
                date = LocalDate.now().minusDays(1),
                time = LocalDateTime.now().minusDays(1).withHour(22),
                emotionLevel = null // EMOTION 없음 → null
            ),
            Diary(
                id = 3L,
                content = "오늘 나 자신을 조금은 칭찬해주고 싶다.",
                types = setOf(DiaryType.PRAISE, DiaryType.THANKS),
                date = LocalDate.now().minusDays(2),
                time = LocalDateTime.now().minusDays(2).withHour(21),
                emotionLevel = null
            ),
            Diary(
                id = 4L,
                content = "프로젝트를 잘 할 수 있을까 너무 걱정돼요",
                types = setOf(DiaryType.EMOTION, DiaryType.WORRY),
                date = LocalDate.now().minusDays(2),
                time = LocalDateTime.now().minusDays(2).withHour(21),
                emotionLevel = EmotionLevel.VERY_BAD
            )
        )
    )

    override fun getDiaries(): Flow<List<Diary>> =
        diariesFlow.asStateFlow()

    override suspend fun getDiaryById(id: Long): Result<Diary?> =
        runCatching {
            diariesFlow.value.find { it.id == id }
        }

    override suspend fun addDiary(diary: Diary): Result<Unit> =
        runCatching {
            diariesFlow.update { current ->
                // id 중복 방지 (서버 붙기 전 임시)
                val nextId =
                    (current.maxOfOrNull { it.id } ?: 0L) + 1

                current + diary.copy(id = nextId)
            }
        }

    override suspend fun deleteDiary(diary: Diary): Result<Unit> =
        runCatching {
            diariesFlow.update { current ->
                current.filterNot { it.id == diary.id }
            }
        }
}
