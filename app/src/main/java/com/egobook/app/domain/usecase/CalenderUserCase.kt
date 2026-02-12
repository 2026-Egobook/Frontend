package com.egobook.app.domain.usecase

import com.egobook.app.domain.model.calender.CalenderDate
import com.egobook.app.domain.repository.diary.CalenderRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * 캘린더 관련 유스케이스 래퍼 클래스
 * 의존성 주입을 쉽게 하기 위한 래퍼
 */
data class CalenderUseCases @Inject constructor(
    val getCalender: GetCalender
)

/**
 * 특정 월의 캘린더 데이터를 가져오는 유스케이스
 */
class GetCalender @Inject constructor(
    private val repository: CalenderRepository
) {
    suspend operator fun invoke(yearMonth: LocalDate): Result<List<CalenderDate>> {
        return repository.getCalender(yearMonth)
    }
}
