package com.egobook.app.store.data

import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.store.data.local.LocalShopDataSource
import com.egobook.app.store.data.model.ItemStatus
import com.egobook.app.store.data.model.ItemType
import com.egobook.app.store.data.model.Price
import com.egobook.app.store.data.network.ProfileImageDto
import com.egobook.app.store.data.network.RemoteShopDataSource
import com.egobook.app.store.ui.CustomItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProfileCacheTest {
    @Test
    fun `profile generated on equipment change is reused without another confirmation`() = runTest {
        val local = mockk<LocalShopDataSource>(relaxed = true)
        val remote = mockk<RemoteShopDataSource>()
        val storage = mockk<UserInfoStorage>(relaxed = true)
        val items = listOf(
            CustomItem("1", ItemType.SKIN, Price(0), ItemStatus.PURCHASED),
            CustomItem("2", ItemType.BACK, Price(0), ItemStatus.PURCHASED)
        )
        coEvery { remote.permanentEquipItem(items[0], true) } returns EquipState(true)
        coEvery { remote.confirmProfile() } returns Unit
        coEvery { remote.loadEquippedItems() } returns items
        every { remote.latestConfirmedProfile } returns ProfileImageDto("turtle.png", null)
        coEvery { storage.getProfileImages(any()) } returns ("turtle.png" to null)
        val repository = ShopRepository(local, remote).apply { userInfoStorage = storage }
        repository.equipItemPermanently(items[0], true)
        coVerify(exactly = 1) { storage.saveProfileImages(any(), "turtle.png", null) }
        assertThat(repository.getCachedProfileImages(items)?.turtleImageUrl).isEqualTo("turtle.png")
        coVerify(exactly = 1) { remote.confirmProfile() }
    }
}
