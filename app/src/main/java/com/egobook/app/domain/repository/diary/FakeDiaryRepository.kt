package com.egobook.app.domain.repository.diary

import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.model.diary.entity.DiaryType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * 테스트 및 개발용 일기 저장소 인터페이스
 *
 * 백엔드 API 연동 전에 더미 데이터로 개발하기 위한 임시 인터페이스입니다.
 *
 * 실제 백엔드 API 연동 시:
 * - 새로운 DiaryRepository 인터페이스를 생성
 * - DiaryRepositoryImpl에서 실제 API 호출 구현
 * - 모든 UseCase에서 FakeDiaryRepository → DiaryRepository로 변경
 *
 * @see FakeDiaryRepositoryImpl
 */
interface FakeDiaryRepository {

    fun getDiaries(): Flow<List<Diary>>

    suspend fun getDiaryById(id: Long): Result<Diary?>

    suspend fun addDiary(
        content: String,
        types: Set<DiaryType>,
        emotionLevel: Int?, // 1~5 사이의 감정 레벨
        createdAt: LocalDateTime // 일기가 귀속될 날짜
    ): Result<Diary>

    suspend fun updateDiary(
        id: Long,
        content: String,
        types: Set<DiaryType>,
        emotionLevel: Int?
    ): Result<Diary>

    suspend fun deleteDiaryById(id: Long): Result<Unit>
}