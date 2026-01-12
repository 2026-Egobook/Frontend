package com.example.egobook_frontent.domain.repository

import com.example.egobook_frontent.domain.model.PraiseMessage
import com.example.egobook_frontent.domain.model.WeeklyReport

interface CounselingRepository {
    suspend fun getDailyPraise(): Result<List<PraiseMessage>>
    suspend fun getWeeklyReport(): Result<List<WeeklyReport>>
}