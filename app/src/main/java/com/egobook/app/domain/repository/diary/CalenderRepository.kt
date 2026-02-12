package com.egobook.app.domain.repository.diary

import com.egobook.app.domain.model.calender.CalenderDate
import java.time.LocalDate

interface CalenderRepository {

    suspend fun getCalender(yearMonth: LocalDate): Result<List<CalenderDate>>
}