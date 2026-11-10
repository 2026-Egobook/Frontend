package com.egobook.app.data.share

import android.graphics.Bitmap
import com.egobook.app.store.data.ShopRepository
import com.egobook.app.store.data.network.ProfileImageDto
import com.egobook.app.store.ui.CustomItem
import io.mockk.every
import java.io.File
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TurtleShareRepositoryTest {
    @Test
    fun `sharing reuses profile without equipping or regenerating it`() = runTest {
        val shop = mockk<ShopRepository>()
        val renderer = mockk<TurtleShareRenderer>()
        val storage = mockk<TurtleShareStorage>()
        val items = emptyList<CustomItem>()
        val profile = ProfileImageDto("https://example.invalid/turtle.png", null)
        val bitmap = mockk<Bitmap>()
        coEvery { shop.getCachedProfileImages(items) } returns profile
        coEvery { renderer.render(profile, items) } returns bitmap
        coEvery { storage.writePreview(bitmap) } returns "preview.png"
        every { storage.previewFile("preview.png") } returns File("preview.png")
        val repository = TurtleShareRepository(shop, renderer, storage)
        assertThat(repository.generate(items)).isEqualTo("preview.png")
        assertThat(repository.generate(items)).isEqualTo("preview.png")
        coVerify(exactly = 2) { shop.getCachedProfileImages(items) }
        coVerify(exactly = 1) { renderer.render(profile, items) }
        every { storage.previewFile("preview.png") } throws IllegalStateException("Cache evicted")
        assertThat(repository.generate(items)).isEqualTo("preview.png")
        coVerify(exactly = 2) { renderer.render(profile, items) }
        coVerify(exactly = 0) { shop.equipItemPermanently(any(), any()) }
    }
}
