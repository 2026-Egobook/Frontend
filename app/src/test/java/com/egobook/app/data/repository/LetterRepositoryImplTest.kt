package com.egobook.app.data.repository

import com.egobook.app.data.api.AIApiService
import com.egobook.app.data.api.LetterApiService
import com.egobook.app.data.model.square.letter.DetectAbusiveContentRequest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LetterRepositoryImplTest {
    private val letterApiService = mockk<LetterApiService>()
    private val aiApiService = mockk<AIApiService>()
    private val repository = LetterRepositoryImpl(letterApiService, aiApiService)

    @Test
    fun `abusive content analysis cancellation is propagated`() = runTest {
        coEvery {
            aiApiService.detectAbusiveContent(DetectAbusiveContentRequest("content"))
        } throws CancellationException("cancelled")

        val cancellation = try {
            repository.detectAbusiveContent("content")
            null
        } catch (error: CancellationException) {
            error
        }

        assertThat(cancellation).isNotNull()
    }
}
