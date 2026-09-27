package com.egobook.app.store.data

import com.egobook.app.store.data.local.LocalShopDataSource
import com.egobook.app.store.data.local.entities.ShopItemEntity
import com.egobook.app.store.data.model.ItemStatus
import com.egobook.app.store.data.model.ItemType
import com.egobook.app.store.data.model.Price
import com.egobook.app.store.data.network.RemoteShopDataSource
import com.egobook.app.store.ui.CustomItem
import com.egobook.app.store.ui.ItemImage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
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
    private val defaultBack = shopItem(id = 10, type = "BACK", price = 0, imageUrl = "https://cdn/BACK/Default.png")
    private val purchasedBack = shopItem(id = 11, type = "BACK", price = 300, imageUrl = "https://cdn/BACK/yellow.png")
    private val defaultSkin = shopItem(id = 20, type = "SKIN", price = 0, imageUrl = "https://cdn/SKIN/Default.png")

    private fun shopItem(id: Int, type: String, price: Int, imageUrl: String) = ShopItemEntity(
        id = id,
        itemType = type,
        price = price,
        thumbnailImageUrl = imageUrl,
        equippedImageUrl = imageUrl,
        isPurchased = true
    )

    private fun givenLocalItems(vararg items: ShopItemEntity) {
        every { local.itemStream } returns flowOf(items.toList())
    }

    @Test
    fun `successful equip confirms profile before reloading equipped items`() = runTest {
        givenLocalItems(defaultBack, defaultSkin)
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

    @Test
    fun `unequipping purchased back equips default back instead`() = runTest {
        givenLocalItems(defaultBack, purchasedBack, defaultSkin)
        val purchased = purchasedBack.toDomain()
        val default = defaultBack.toDomain()
        coEvery { remote.permanentEquipItem(default, true) } returns EquipState(true)
        coEvery { remote.confirmProfile() } returns Unit
        coEvery { remote.loadEquippedItems() } returns listOf(default, defaultSkin.toDomain())

        repository.equipItemPermanently(purchased, false)

        coVerify(exactly = 1) { remote.permanentEquipItem(default, true) }
        coVerify(exactly = 0) { remote.permanentEquipItem(purchased, false) }
    }

    @Test
    fun `unequipping default back keeps it equipped`() = runTest {
        givenLocalItems(defaultBack, defaultSkin)
        val default = defaultBack.toDomain()
        coEvery { remote.loadEquippedItems() } returns listOf(default, defaultSkin.toDomain())

        val equipped = repository.equipItemPermanently(default, false)

        coVerify(exactly = 0) { remote.permanentEquipItem(any(), any()) }
        assertThat(equipped).contains(default)
    }

    @Test
    fun `missing back and skin are filled with default items`() = runTest {
        givenLocalItems(defaultBack, purchasedBack, defaultSkin)
        coEvery { remote.loadEquippedItems() } returns emptyList()

        val equipped = repository.loadEquippedItems()

        assertThat(equipped.map { it.id }).containsExactlyInAnyOrder("10", "20")
        assertThat(equipped.map { (it.outfitImage as ItemImage.Url).path })
            .allMatch { it.contains("Default") }
    }
}
