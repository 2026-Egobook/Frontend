package com.egobook.app.store.ui

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.egobook.app.analytics.AnalyticsLogger
import com.egobook.app.store.data.ShopRepository
import com.egobook.app.store.data.model.ItemStatus
import com.egobook.app.store.data.model.ItemType
import com.egobook.app.store.data.model.Price
import com.egobook.app.ui.home.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class StoreViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var shopRepository: ShopRepository
    private lateinit var userRepository: UserRepository
    private lateinit var analyticsLogger: AnalyticsLogger
    private lateinit var viewModel: StoreViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        shopRepository = mockk()
        userRepository = mockk()
        analyticsLogger = mockk(relaxed = true)
        every { shopRepository.itemStream } returns flowOf(emptyList())
        viewModel = StoreViewModel(
            shopRepository = shopRepository,
            userRepository = userRepository,
            analyticsLogger = analyticsLogger
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `initialize 도중 예외가 발생해도 크래시 없이 토스트 이벤트를 방출한다`() = runTest {
        // Given
        coEvery { userRepository.load() } throws RuntimeException("network error")
        coEvery { shopRepository.initialize() } throws RuntimeException("HTTP 401")

        val toastEvents = mutableListOf<String>()
        val job = launch { viewModel.toastEvent.toList(toastEvents) }
        runCurrent()

        // When
        viewModel.initialize()

        // Then
        job.cancel()
        assertEquals(listOf("상점 정보를 불러오지 못했습니다."), toastEvents)
    }

    @Test
    fun `initialize 성공 시 장착 아이템 목록을 반영한다`() = runTest {
        // Given
        val expectedItems = listOf(
            CustomItem(
                id = "1",
                type = ItemType.SKIN,
                price = Price(100),
                itemStatus = ItemStatus.PURCHASED
            )
        )
        coEvery { userRepository.load() } throws RuntimeException("ink load skipped for this test")
        coEvery { shopRepository.initialize() } returns Unit
        coEvery { shopRepository.loadEquippedItems() } returns expectedItems

        // When
        viewModel.initialize()

        // Then
        assertEquals(expectedItems, viewModel.equippedItems.value)
    }

    private val purchasedBack = CustomItem(
        id = "11",
        type = ItemType.BACK,
        price = Price(300),
        itemStatus = ItemStatus.PURCHASED
    )
    private val previewBack = CustomItem(
        id = "12",
        type = ItemType.BACK,
        price = Price(500),
        itemStatus = ItemStatus.PURCHASABLE
    )

    private suspend fun initializeWith(equipped: List<CustomItem>) {
        coEvery { userRepository.load() } throws RuntimeException("ink load skipped for this test")
        coEvery { shopRepository.initialize() } returns Unit
        coEvery { shopRepository.loadEquippedItems() } returns equipped
        viewModel.initialize()
    }

    @Test
    fun `미리보기 후 장착 중인 구매 아이템을 누르면 해제하지 않고 원래 아이템으로 돌아온다`() = runTest {
        // Given
        initializeWith(listOf(purchasedBack))
        viewModel.equipItem(previewBack)

        // When
        viewModel.equipItem(purchasedBack)
        runCurrent()

        // Then
        assertEquals(listOf(purchasedBack), viewModel.equippedItems.value)
        coVerify(exactly = 0) { shopRepository.equipItemPermanently(any(), any()) }
    }

    @Test
    fun `화면에 장착된 구매 아이템을 다시 누르면 해제를 요청한다`() = runTest {
        // Given
        initializeWith(listOf(purchasedBack))
        coEvery { shopRepository.equipItemPermanently(purchasedBack, false) } returns emptyList()

        // When
        viewModel.equipItem(purchasedBack)
        runCurrent()

        // Then
        coVerify(exactly = 1) { shopRepository.equipItemPermanently(purchasedBack, false) }
    }
}
