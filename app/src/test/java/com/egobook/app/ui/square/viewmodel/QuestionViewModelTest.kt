package com.egobook.app.ui.square.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.egobook.app.domain.usecase.UpdateMarketingConsentUseCase
import com.egobook.app.util.UiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class QuestionViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: QuestionViewModel
    private lateinit var updateMarketingConsentUseCase: UpdateMarketingConsentUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        updateMarketingConsentUseCase = mockk()

        viewModel = QuestionViewModel(
            getTodayQuestionUseCase = mockk(),
            submitTodayAnswerUseCase = mockk(),
            getMyRepliesHistoryUseCase = mockk(),
            getTodayFriendsRepliesUseCase = mockk(),
            getTodayAllUserRepliesUseCase = mockk(),
            updateTodayAnswerUseCase = mockk(),
            deleteMyQuestionAnswerUseCase = mockk(),
            reportTodayQuestionAnswerUseCase = mockk(),
            updateMarketingConsentUseCase = updateMarketingConsentUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateMarketingConsent 성공 시 Loading 후 변경된 값으로 Success를 방출한다`() = runTest {
        // Given
        coEvery { updateMarketingConsentUseCase(true) } returns Result.success(Unit)
        val results = mutableListOf<UiState<Boolean>>()
        val job = launch { viewModel.updateMarketingConsentResult.toList(results) }
        runCurrent()

        // When
        viewModel.updateMarketingConsent(true)
        advanceUntilIdle()
        job.cancel()

        // Then
        assertEquals(listOf(UiState.Loading, UiState.Success(true)), results)
    }

    @Test
    fun `updateMarketingConsent 실패 시 Loading 후 Failure를 방출한다`() = runTest {
        // Given
        coEvery { updateMarketingConsentUseCase(false) } returns Result.failure(Exception("Network error"))
        val results = mutableListOf<UiState<Boolean>>()
        val job = launch { viewModel.updateMarketingConsentResult.toList(results) }
        runCurrent()

        // When
        viewModel.updateMarketingConsent(false)
        advanceUntilIdle()
        job.cancel()

        // Then
        assertEquals(2, results.size)
        assertEquals(UiState.Loading, results[0])
        assertTrue(results[1] is UiState.Failure)
    }
}
