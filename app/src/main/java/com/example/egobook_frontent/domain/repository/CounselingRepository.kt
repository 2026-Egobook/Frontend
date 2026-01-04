package com.example.egobook_frontent.domain.repository

import com.example.egobook_frontent.domain.model.PraiseMessage

interface CounselingRepository {
    suspend fun getDailyPraise(): Result<List<PraiseMessage>>
}