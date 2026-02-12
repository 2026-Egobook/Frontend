package com.egobook.app.ui.diary.mapper

import com.egobook.app.domain.model.calender.CalenderDate
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * 캘린더 도메인 모델 ↔ UI 모델 변환 매퍼
 */
object CalenderEntityMapper {

    private val YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM")

    /**
     * YearMonth를 API 요청용 문자열로 변환
     * @return "2026-02" 형식의 문자열
     */
    fun yearMonthToString(yearMonth: YearMonth): String {
        return yearMonth.format(YEAR_MONTH_FORMATTER)
    }

    /**
     * 문자열을 YearMonth로 변환
     * @param yearMonthString "2026-02" 형식의 문자열
     */
    fun stringToYearMonth(yearMonthString: String): YearMonth {
        return YearMonth.parse(yearMonthString, YEAR_MONTH_FORMATTER)
    }

    /**
     * CalenderDate 리스트를 Map으로 변환 (UI에서 O(1) 조회용)
     * Key: LocalDate, Value: emotionLevel
     */
    fun toDateEmotionMap(calenderDates: List<CalenderDate>): Map<LocalDate, Int?> {
        return calenderDates.associate { it.date to it.emotionLevel }
    }

    /**
     * 특정 날짜의 감정 레벨 조회 (매핑된 Map에서)
     */
    fun getEmotionLevel(
        dateMap: Map<LocalDate, Int?>,
        date: LocalDate
    ): Int? {
        return dateMap[date]
    }
}
