package com.egobook.app.ui.diary.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.egobook.app.domain.model.diary.entity.DiarySummary
import com.egobook.app.domain.usecase.diaryusecase.DiaryUseCases
import com.egobook.app.domain.usecase.diaryusecase.GetDailyCount
import com.egobook.app.domain.usecase.diaryusecase.GetDiaries
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@ExperimentalCoroutinesApi
class DiariesViewModelTest {

    // JUnit 테스트에서 LiveData/StateFlow를 동기적으로 처리하기 위한 규칙
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: DiariesViewModel
    private lateinit var diaryUseCases: DiaryUseCases
    private lateinit var getDailyCount: GetDailyCount
    private lateinit var getDiaries: GetDiaries
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // UseCase 모킹
        getDailyCount = mockk()
        getDiaries = mockk()

        // GetDiaries 모킹 설정
        every { getDiaries.invoke(any()) } returns flowOf(PagingData.empty())

        diaryUseCases = DiaryUseCases(
            getDiaries = getDiaries,
            getDiary = mockk(),
            addDiary = mockk(),
            updateDiary = mockk(),
            deleteDiary = mockk(),
            getDailyCount = getDailyCount,
            exportDiary = mockk(),
        )

        viewModel = DiariesViewModel(diaryUseCases)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `dailyCount가 48 미만이면 해당 값을 반환한다`() = runTest {
        // Given
        val expectedCount = 47
        coEvery { getDailyCount.invoke(any()) } returns Result.success(expectedCount)

        // When
        val result = viewModel.getDailyCountWithCache()

        // Then
        assertEquals(expectedCount, result)
        assertTrue(result < 48)
    }

    @Test
    fun `dailyCount가 48이면 48을 반환하고 스낵바 표시 조건이 충족된다`() = runTest {
        // Given
        val expectedCount = 48
        coEvery { getDailyCount.invoke(any()) } returns Result.success(expectedCount)

        // When
        val result = viewModel.getDailyCountWithCache()

        // Then
        assertEquals(expectedCount, result)
        assertTrue(result >= 48) // 스낵바 표시 조건
    }

    @Test
    fun `dailyCount가 48 초과면 해당 값을 반환하고 스낵바 표시 조건이 충족된다`() = runTest {
        // Given
        val expectedCount = 50
        coEvery { getDailyCount.invoke(any()) } returns Result.success(expectedCount)

        // When
        val result = viewModel.getDailyCountWithCache()

        // Then
        assertEquals(expectedCount, result)
        assertTrue(result >= 48) // 스낵바 표시 조건
    }

    @Test
    fun `dailyCount가 0이면 해당 값을 반환한다`() = runTest {
        // Given
        val expectedCount = 0
        coEvery { getDailyCount.invoke(any()) } returns Result.success(expectedCount)

        // When
        val result = viewModel.getDailyCountWithCache()

        // Then
        assertEquals(expectedCount, result)
        assertTrue(result < 48)
    }

    @Test
    fun `API 호출 실패 시 현재 state의 dailyCount를 반환한다`() = runTest {
        // Given: API 호출 실패, state에는 이미 0이 있음 (init에서 설정)
        coEvery { getDailyCount.invoke(any()) } returns Result.failure(Exception("Network error"))

        // When
        val result = viewModel.getDailyCountWithCache()

        // Then: getOrDefault로 인해 state의 dailyCount(0) 반환
        assertEquals(0, result)
    }

    @Test
    fun `날짜 변경 시 새로운 날짜로 dailyCount를 조회한다`() = runTest {
        // Given
        val expectedCount = 5
        coEvery { getDailyCount.invoke(any()) } returns Result.success(expectedCount)

        // When: 날짜 변경
        viewModel.onEvent(DiariesEvent.ChangeDate(2026, 2, 10))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val result = viewModel.getDailyCountWithCache()
        assertEquals(expectedCount, result)
    }
}
