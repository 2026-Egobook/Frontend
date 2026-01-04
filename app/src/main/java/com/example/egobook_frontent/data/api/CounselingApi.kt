package com.example.egobook_frontent.data.api

import com.example.egobook_frontent.data.model.PraiseMessageResponse
import retrofit2.Response
import retrofit2.http.GET

interface CounselingApi {
    @GET("api/praise/daily")
    suspend fun fetchDailyPraise(): Response<List<PraiseMessageResponse>>
}