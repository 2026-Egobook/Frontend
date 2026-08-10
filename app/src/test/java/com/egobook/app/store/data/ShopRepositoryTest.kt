package com.egobook.app.store.data

import com.egobook.app.store.data.local.LocalShopDataSource
import com.egobook.app.store.data.model.ItemStatus
import com.egobook.app.store.data.model.ItemType
import com.egobook.app.store.data.model.Price
import com.egobook.app.store.data.network.RemoteShopDataSource
import com.egobook.app.store.ui.CustomItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShopRepositoryTest {
    private val local = mockk<LocalShopDataSource>(relaxed = true)
    private val remote = mockk<RemoteShopDataSource>()
    private val repository = ShopRepository(local, remote)
    private val item = CustomItem(
        id = "1",
        type = ItemType.SKIN,
        price = Price(0),
        itemStatus = ItemStatus.PURCHASED
    )

    @Test
    fun `successful equip confirms profile before reloading equipped items`() = runTest {
        coEvery { remote.permanentEquipItem(item, true) } returns EquipState(true)
        coEvery { remote.confirmProfile() } returns Unit
        coEvery { remote.loadEquippedItems() } returns emptyList()

        repository.equipItemPermanently(item, true)

        coVerifyOrder {
            remote.permanentEquipItem(item, true)
            remote.confirmProfile()
            remote.loadEquippedItems()
        }
        coVerify(exactly = 1) { remote.confirmProfile() }
    }

    @Test
    fun `failed equip does not confirm or reload profile`() = runTest {
        coEvery { remote.permanentEquipItem(item, true) } returns EquipState(false)

        val failure = try {
            repository.equipItemPermanently(item, true)
            null
        } catch (error: IllegalStateException) {
            error
        }
        assertThat(failure).isNotNull()

        coVerify(exactly = 0) { remote.confirmProfile() }
        coVerify(exactly = 0) { remote.loadEquippedItems() }
    }
}
