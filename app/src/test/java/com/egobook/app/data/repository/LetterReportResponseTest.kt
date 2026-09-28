package com.egobook.app.data.repository

import com.egobook.app.data.api.LetterApiService
import com.egobook.app.domain.model.square.letter.ReportContent
import com.egobook.app.domain.model.square.letter.ReportLetterType
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LetterReportResponseTest {
    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `string report response is handled as success`(reply: Boolean) = runTest {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(
                    """{"code":"SUCCESS","message":"OK","status":200,"data":"Reported"}"""
                        .toResponseBody("application/json".toMediaType())
                )
                .build()
        }.build()
        val service = Retrofit.Builder()
            .baseUrl("https://example.invalid/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LetterApiService::class.java)
        val repository = LetterRepositoryImpl(service, mockk())
        val report = ReportContent(ReportLetterType.ABUSE)

        val result = if (reply) repository.reportRepliedLetter(1L, report)
        else repository.reportArrivedLetter(1L, report)

        assertThat(result.getOrThrow()).isEqualTo(Unit)
    }
}
