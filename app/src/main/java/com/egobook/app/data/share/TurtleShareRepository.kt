package com.egobook.app.data.share

import com.egobook.app.store.data.ShopRepository
import com.egobook.app.store.ui.CustomItem
import com.egobook.app.store.data.model.ItemType
import com.egobook.app.store.data.network.ProfileImageDto
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TurtleShareRepository @Inject constructor(
    private val shopRepository: ShopRepository,
    private val renderer: TurtleShareRenderer,
    private val storage: TurtleShareStorage
) {
    private val generationMutex = Mutex()
    private var lastSource: Pair<ProfileImageDto?, List<CustomItem>>? = null
    private var lastFile: String? = null

    suspend fun generate(items: List<CustomItem>): String = generationMutex.withLock {
        // Reading the saved result must never regenerate/upload the server profile.
        val profile = shopRepository.getCachedProfileImages(items)
        val source = profile to items.filter { it.type != ItemType.LETTER_PAPER }.sortedBy { it.type.ordinal }
        lastFile?.takeIf { lastSource == source && runCatching { storage.previewFile(it) }.isSuccess }
            ?.let { return@withLock it }
        storage.writePreview(renderer.render(profile, items)).also {
            lastSource = source
            lastFile = it
        }
    }
}
