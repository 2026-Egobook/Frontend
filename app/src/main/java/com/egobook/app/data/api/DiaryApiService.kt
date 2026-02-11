package com.egobook.app.data.api

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.diary.request.DiaryCreateRequest
import com.egobook.app.data.model.diary.request.DiaryUpdateRequest
import com.egobook.app.data.model.diary.response.DiariesResponse
import com.egobook.app.data.model.diary.response.DiaryCreateResponse
import com.egobook.app.data.model.diary.response.DiaryDeleteResponse
import com.egobook.app.data.model.diary.response.DiaryEntryResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DiaryApiService {

    //일기 목록 불러오기
    @GET("/diaries")
    suspend fun getDiaries(
        @Query("date") date: String,
        @Query("type") type: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): ApiResponse<DiariesResponse>

    //일기 상세 확인
    @GET("/diaries/{diaryId}")
    suspend fun getDiary(
        @Path("diaryId") diaryId: Long
    ): ApiResponse<DiaryEntryResponse>

    //일기 추가
    @POST("/diaries")
    suspend fun addDiary(
        @Body request: DiaryCreateRequest
    ): ApiResponse<DiaryCreateResponse>

    //일기 삭제
    @DELETE("/diaries/{diaryId}")
    suspend fun deleteDiary(
        @Path("diaryId") diaryId: Long
    ): ApiResponse<DiaryDeleteResponse>

    //일기 수정
    @PATCH("/diaries/{diaryId}")
    suspend fun updateDiary(
        @Path("diaryId") diaryId: Long,
        @Body request: DiaryUpdateRequest
    ): ApiResponse<DiaryEntryResponse>

}