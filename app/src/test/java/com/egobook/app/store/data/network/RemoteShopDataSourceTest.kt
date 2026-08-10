package com.egobook.app.store.data.network

import com.egobook.app.store.data.PermanentEquipRequest
import com.egobook.app.store.data.model.ItemStatus
import com.egobook.app.store.data.model.ItemType
import com.egobook.app.store.data.model.Price
import com.egobook.app.store.ui.CustomItem
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import retrofit2.Retrofit

class RemoteShopDataSourceTest {
    private val apiService = mockk<ShopApiService>()
    private val retrofit = mockk<Retrofit> {
        every { create(ShopApiService::class.java) } returns apiService
    }
    private val dataSource = RemoteShopDataSource(retrofit)
    private val item = CustomItem(
        id = "1",
        type = ItemType.SKIN,
        price = Price(0),
        itemStatus = ItemStatus.PURCHASED
    )

    @Test
    fun `equip cancellation is propagated`() = runTest {
        coEvery {
            apiService.equipItemPermanently(PermanentEquipRequest(itemId = 1, isEquipped = true))
        } throws CancellationException("cancelled")

        val cancellation = try {
            dataSource.permanentEquipItem(item, true)
            null
        } catch (error: CancellationException) {
            error
        }

        assertThat(cancellation).isNotNull()
    }

    @Test
    fun `ordinary equip exception becomes a failed state`() = runTest {
        coEvery {
            apiService.equipItemPermanently(PermanentEquipRequest(itemId = 1, isEquipped = true))
        } throws IllegalStateException("network failure")

        assertThat(dataSource.permanentEquipItem(item, true).isSuccess).isFalse()
    }
}
