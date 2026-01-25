package com.egobook.app.data.repository

import com.egobook.app.domain.model.Diary
import com.egobook.app.domain.model.DiaryType
import com.egobook.app.domain.model.EmotionLevel
import com.egobook.app.domain.repository.DiaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor() : DiaryRepository {

    //더미데이터 삽입
    private val diariesFlow = MutableStateFlow(
        listOf(
            // --- 오늘 날짜 (Today) ---
            Diary(
                id = 1L,
                content = "오늘 아침, 상쾌하게 하루를 시작했다. 기분이 좋다.",
                types = setOf(DiaryType.EMOTION, DiaryType.THANKS),
                createdAt = LocalDateTime.now().withHour(8).withMinute(30),
                updatedAt = LocalDateTime.now().withHour(8).withMinute(30),
                emotionLevel = EmotionLevel.VERY_GOOD
            ),
            Diary(
                id = 2L,
                content = "점심 먹고 나니 너무 졸리다. 오후 업무가 걱정된다.",
                types = setOf(DiaryType.EMOTION, DiaryType.WORRY),
                createdAt = LocalDateTime.now().withHour(13).withMinute(10),
                updatedAt = LocalDateTime.now().withHour(13).withMinute(15), // 5분 뒤 수정
                emotionLevel = EmotionLevel.BAD
            ),
            Diary(
                id = 3L,
                content = "오늘 저녁은 뭘 먹을지 고민이다. 하루 중 가장 큰 고민.",
                types = setOf(DiaryType.WORRY),
                createdAt = LocalDateTime.now().withHour(18).withMinute(0),
                updatedAt = LocalDateTime.now().withHour(18).withMinute(0),
                emotionLevel = null
            ),

            // --- 어제 날짜 (Yesterday) ---
            Diary(
                id = 4L,
                content = "어제는 정말 힘든 하루였다. 빨리 잊고 싶다.",
                types = setOf(DiaryType.EMOTION),
                createdAt = LocalDateTime.now().minusDays(1).withHour(23).withMinute(50),
                updatedAt = LocalDateTime.now().minusDays(1).withHour(23).withMinute(50),
                emotionLevel = EmotionLevel.VERY_BAD
            ),
            Diary(
                id = 5L,
                content = "친구에게 작은 선물을 받았는데, 정말 고마웠다.",
                types = setOf(DiaryType.THANKS),
                createdAt = LocalDateTime.now().minusDays(1).withHour(15).withMinute(0),
                updatedAt = LocalDateTime.now().minusDays(1).withHour(15).withMinute(0),
                emotionLevel = null
            ),
            Diary(
                id = 6L,
                content = "어제 내가 해낸 작은 성과에 대해 스스로를 칭찬한다.",
                types = setOf(DiaryType.PRAISE),
                createdAt = LocalDateTime.now().minusDays(1).withHour(21).withMinute(0),
                updatedAt = LocalDateTime.now().minusDays(1).withHour(22).withMinute(30), // 1시간 30분 뒤 수정
                emotionLevel = null
            ),

            // --- 2일 전 날짜 ---
            Diary(
                id = 7L,
                content = "이틀 전, 진로에 대해 계속 고민만 하다 하루가 갔다.",
                types = setOf(DiaryType.WORRY),
                createdAt = LocalDateTime.now().minusDays(2).withHour(22).withMinute(0),
                updatedAt = LocalDateTime.now().minusDays(2).withHour(22).withMinute(10), // 10분 후 수정
                emotionLevel = null
            ),

            // --- 3일 전 날짜 ---
            Diary(
                id = 8L,
                content = "3일 전, 오늘 나 자신을 조금은 칭찬해주고 싶었다.",
                types = setOf(DiaryType.PRAISE, DiaryType.THANKS),
                createdAt = LocalDateTime.now().minusDays(3).withHour(21).withMinute(0),
                updatedAt = LocalDateTime.now().minusDays(3).withHour(22).withMinute(0), // 1시간 후 수정
                emotionLevel = null
            ),

            // --- 4일 전 날짜 ---
            Diary(
                id = 9L,
                content = "프로젝트를 잘 할 수 있을까 너무 걱정됐던 날.",
                types = setOf(DiaryType.EMOTION, DiaryType.WORRY),
                createdAt = LocalDateTime.now().minusDays(4).withHour(10).withMinute(0),
                updatedAt = LocalDateTime.now().minusDays(3).withHour(11).withMinute(0), // 하루 뒤에 수정
                emotionLevel = EmotionLevel.VERY_BAD
            ),

            // --- 일주일 전 날짜 ---
            Diary(
                id = 10L,
                content = "일주일 전의 나는 무엇을 하고 있었을까? 평범하지만 괜찮은 하루였다.",
                types = setOf(DiaryType.EMOTION),
                createdAt = LocalDateTime.now().minusWeeks(1).withHour(16).withMinute(0),
                updatedAt = LocalDateTime.now().minusWeeks(1).withHour(16).withMinute(0),
                emotionLevel = EmotionLevel.GOOD
            ),
            Diary(
                id = 11L,
                content = "모든 타입이 포함된 종합 일기. 정말 많은 일이 있었다.",
                types = setOf(DiaryType.EMOTION, DiaryType.WORRY, DiaryType.PRAISE, DiaryType.THANKS),
                createdAt = LocalDateTime.now().minusWeeks(1).withHour(23).withMinute(0),
                updatedAt = LocalDateTime.now().minusWeeks(1).withHour(23).withMinute(0),
                emotionLevel = EmotionLevel.NORMAL
            )
        )
    )

    override fun getDiaries(): Flow<List<Diary>> =
        diariesFlow.asStateFlow()

    override suspend fun getDiaryById(id: Long): Result<Diary?> =
        runCatching {
            diariesFlow.value.find { it.id == id }
        }

    override suspend fun addDiary(
        content: String,
        types: Set<DiaryType>,
        emotionLevel: EmotionLevel?
    ): Result<Diary> =
        runCatching {
            //임시 반환 로직 작성
            val now = LocalDateTime.now()

            val newDiary = Diary(
                id = (diariesFlow.value.maxOfOrNull { it.id } ?: 0L) + 1,
                content = content,
                types = types,
                emotionLevel = emotionLevel,
                createdAt = now,
                updatedAt = now
            )

            diariesFlow.update { current ->
                current + newDiary
            }

            newDiary
        }


    override suspend fun deleteDiaryById(id: Long): Result<Unit> =
        runCatching {
            diariesFlow.update { current ->
                current.filterNot { it.id == id }
            }
        }
}