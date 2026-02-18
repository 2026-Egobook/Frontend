package com.egobook.app.domain.model.diary.entity


import java.time.LocalDate
import java.time.LocalDateTime


data class DayDiaries(
    val dailyCount: Int,
    val diaries: DiaryList
)

/**
 * 일기 페이징 사용 용도의 도메인 엔티티
 */
data class DiaryList(
    val content: List<DiarySummary>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean
)
/**
 * 일기 간단 정보 (목록용)
 */
data class DiarySummary(
    val diaryId: Long,
    val writtenAt: LocalDateTime,
    val types: Set<DiaryType>,
    val emotionLevel: Int?,
    val content: String,
)

/**
 * 일기 상세 정보 도메인 엔티티
 */
data class Diary(
    val diaryId: Long, //일기 id
    val date: LocalDate, //종속 날짜
    val writtenAt: LocalDateTime, //마지막 수정 시각
    val types: Set<DiaryType>, //중복 방지
    val emotionLevel: Int?, //감정 레벨 (1: 매우 나쁨, 2: 나쁨, 3: 보통, 4: 좋음, 5: 매우 좋음). null일 수도 있음
    val content: String, //내용
    val createdAt: LocalDateTime, //최초 생성 시각
) {

}


