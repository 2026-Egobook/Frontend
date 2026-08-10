package com.egobook.app.data.repository.diary

import com.egobook.app.data.api.CalenderApiService
import com.egobook.app.data.model.diary.response.CalenderData
import com.egobook.app.data.util.safeApiCall
import com.egobook.app.domain.model.calender.CalenderDate
import com.egobook.app.domain.model.diary.mapper.CalenderMapper
import com.egobook.app.domain.repository.diary.CalenderRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CalenderRepositoryImpl @Inject constructor(
    private val apiService: CalenderApiService
) : CalenderRepository {

    override suspend fun getCalender(yearMonth: LocalDate): Result<List<CalenderDate>> {
        val monthString = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        val result = safeApiCall(
            apiCall = { apiService.getCalender(monthString) },
            transform = { calenderData: CalenderData ->
                CalenderMapper.dataToDomainList(calenderData)
            }
        )
        return result
    }
}
