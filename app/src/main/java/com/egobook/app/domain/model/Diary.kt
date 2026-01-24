package com.egobook.app.domain.model


import java.time.LocalDate
import java.time.LocalDateTime


data class Diary(
    val id: Long, //일기 id
    val content: String, //내용
    val types: Set<DiaryType>, //중복 방지
    val date: LocalDate, //날짜
    val time: LocalDateTime, //시간
    val emotionType: EmotionType? //없을 수도 있음
)

enum class DiaryType(val value: String, val displayType: String) {
    EMOTION("EMOTION", "감정"),
    WORRY("WORRY", "고민"),
    PRAISE("PRAISE", "칭찬"),
    THANKS("THANKS", "감사");


    /**
     * API value로 DiaryType 찾기 (예: "EMOTION", "WORRY") -> 추후 작성 예정
     * UI 문자열 형식도 지원 (예: "EMOTION(감정)", "고민")
     */
    companion object {
        fun fromDisplayType(displayType: String): DiaryType {
            return entries.find { it.displayType == displayType }
                ?: throw IllegalArgumentException("Unknown display type: $displayType")
        }

    }
}
