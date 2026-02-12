package com.egobook.app.domain.model.calender

import java.time.LocalDate

/**
 * 캘린더의 각 날짜 정보
 * @param date 날짜 (ISO-8601)
 * @param emotionLevel 감정 단계 (1~4, null이면 기록 없음)
 *                 UI에서 1~4 값에 해당하는 이미지로 매핑
 *                 1: 매우 나쁨, 2: 나쁨, 3: 보통, 4: 좋음
 */
data class CalenderDate(
    val date: LocalDate, //2026-02 형식
    val emotionLevel: Int?
) {
    /** 해당 월의 몇 일인지 (1~31) */
    val dayOfMonth: Int
        get() = date.dayOfMonth

    /** 주말 여부 (토요일=6, 일요일=7) */
    val isWeekend: Boolean
        get() = date.dayOfWeek.value >= 6

    /** 일요일 여부 (텍스트 색상용) */
    val isSunday: Boolean
        get() = date.dayOfWeek.value == 7

    /** 토요일 여부 */
    val isSaturday: Boolean
        get() = date.dayOfWeek.value == 6

    /** 오늘 날짜인지 */
    val isToday: Boolean
        get() = date == LocalDate.now()

    /** 감정 기록이 있는지 (null이 아닌지) */
    val hasEmotion: Boolean
        get() = emotionLevel != null

}
