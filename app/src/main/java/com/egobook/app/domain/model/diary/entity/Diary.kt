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


enum class DiaryType(val value: String, val displayType: String) {
    EMOTION("EMOTION", "감정"),
    CONCERN("CONCERN", "고민"),
    PRAISE("PRAISE", "칭찬"),
    GRATITUDE("GRATITUDE", "감사");
    companion object {
        /**
         * API value로 일기 타입 찾기 (예: "EMOTION", "CONCERN")
         */
        fun from(value: String): DiaryType {
            return entries.find { it.value == value }
                ?: throw IllegalArgumentException("Unknown diary type: $value")
        }

        /**
         * 한글 displayType으로 DiaryType 찾기 (예: "감정", "고민")
         */
        fun fromDisplayType(displayType: String): DiaryType {
            return entries.find { it.displayType == displayType }
                ?: throw IllegalArgumentException("Unknown display type: $displayType")
        }

    }
}
