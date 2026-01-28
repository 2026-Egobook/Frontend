package com.egobook.app.domain.model


import java.time.LocalDateTime


data class Diary(
    val id: Long, //일기 id
    val content: String, //내용
    val types: Set<DiaryType>, //중복 방지
    val createdAt: LocalDateTime, //최초 생성 시각
    val writtenAt: LocalDateTime, //마지막 수정 시각
    val emotionLevel: Int? //감정 레벨 (1: 매우 나쁨, 2: 나쁨, 3: 보통, 4: 좋음, 5: 매우 좋음). null일 수도 있음
) {
    init {
        //일기 타입에 감정이 포함되어 있어야만 기분 선택 가능 -> 도메인 규칙으로 정의
        if (DiaryType.EMOTION !in types && emotionLevel != null) {
            throw IllegalStateException(
                "emotionLevel은 EMOTION 타입이 포함된 경우에만 설정할 수 있습니다."
            )
        }
        
        // emotionLevel이 null이 아닌 경우 1~5 범위 체크
        if (emotionLevel != null && emotionLevel !in 1..5) {
            throw IllegalArgumentException(
                "emotionLevel은 1~5 사이의 값이어야 합니다. 현재 값: $emotionLevel"
            )
        }
    }
}

enum class DiaryType(val value: String, val displayType: String) {
    EMOTION("EMOTION", "감정"),
    WORRY("WORRY", "고민"),
    PRAISE("PRAISE", "칭찬"),
    THANKS("THANKS", "감사");


    companion object {
        fun fromDisplayType(displayType: String): DiaryType {
            return entries.find { it.displayType == displayType }
                ?: throw IllegalArgumentException("Unknown display type: $displayType")
        }

    }
}
