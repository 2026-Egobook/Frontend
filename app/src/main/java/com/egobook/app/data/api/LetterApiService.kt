package com.egobook.app.data.api

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.square.letter.ArrivedPendingLetterResponse
import com.egobook.app.data.model.square.letter.ReplyLetterRequest
import com.egobook.app.data.model.square.letter.ReplyLetterResponse
import com.egobook.app.data.model.square.letter.ReportLetterRequest
import com.egobook.app.data.model.square.letter.SendLetterRequest
import com.egobook.app.data.model.square.letter.SendLetterResponse
import com.egobook.app.data.model.square.letter.SentLetterResponse
import com.egobook.app.data.model.square.letter.SentLetterWithReplyResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LetterApiService {
    @POST("/plaza/letters")
    suspend fun sendLetter(@Body request: SendLetterRequest): ApiResponse<SendLetterResponse>

    @GET("/plaza/letters/inbox/next")
    suspend fun fetchArrivedPendingLetter(): ApiResponse<ArrivedPendingLetterResponse>

    @POST("/plaza/letters/{letterId}/reply")
    suspend fun replyLetter(
        @Path("letterId") letterId: Long,
        @Body request: ReplyLetterRequest
    ): ApiResponse<ReplyLetterResponse>

    @POST("/plaza/letters/{letterId}/defer")
    suspend fun deferReplyLetter(
        @Path("letterId") letterId: Long
    ): ApiResponse<Unit>

    @POST("/plaza/letters/{letterId}/give-up")
    suspend fun giveUpReplyLetter(
        @Path("letterId") letterId: Long
    ): ApiResponse<Unit>

    @GET("/plaza/letters/sent")
    suspend fun fetchSentLetters(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ApiResponse<SentLetterResponse>

    @GET("/plaza/letters/{letterId}")
    suspend fun fetchSentLetterWithReply(
        @Path("letterId") letterId: Long
    ): ApiResponse<SentLetterWithReplyResponse>

    @POST("/plaza/letters/{replyId}/report")
    suspend fun reportRepliedLetter(
        @Path("replyId") replyId: Long,
        @Body request: ReportLetterRequest
    ): ApiResponse<Unit>

    @DELETE("/plaza/letters/threads/{threadId}")
    suspend fun deleteLetterThread(
        @Path("threadId") threadId: Long
    ): ApiResponse<Unit>
}