package com.egobook.app.domain.model.diary.mapper

import android.util.Log
import com.egobook.app.data.model.diary.response.CalenderData
import com.egobook.app.data.model.diary.response.CalenderDay
import com.egobook.app.domain.model.calender.CalenderDate
import java.time.LocalDate

/**
 * Data Layer ↔ Domain Layer 변환 Mapper
 */
object CalenderMapper {

    /**
     * CalenderData를 도메인 객체 리스트로 변환
     */
    fun dataToDomainList(calenderData: CalenderData): List<CalenderDate> {
        return calenderData.days?.map { day ->
            Log.d("CalenderMapper", "Mapping day: date=${day.date}, emotionLevel=${day.emotionLevel}")
            dayToDomain(day)
        } ?: emptyList()
    }

    /**
     * 개별 날짜 데이터를 도메인 객체로 변환
     */
    private fun dayToDomain(day: CalenderDay): CalenderDate {
        return CalenderDate(
            date = LocalDate.parse(day.date),
            emotionLevel = day.emotionLevel
        )
    }
}
