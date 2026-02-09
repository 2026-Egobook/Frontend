package com.egobook.app.ui.shop

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

private fun String.toItemType(): ItemType = when(this) {
    "BACK" -> ItemType.BACK
    "SKIN" -> ItemType.SKIN
    "DECOR_ONE" -> ItemType.DECO_1
    "DECOR_TWO" -> ItemType.DECO_2
    "BACKGROUND" -> ItemType.BACKGROUND
    else -> throw IllegalArgumentException("${this}은 알 수 없는 아이템 타입 이름입니다")
}

interface StoreRepository {
    fun loadStoreItems(itemType: ItemType): Flow<CustomItem>
    suspend fun loadEquippedItems(): List<CustomItem>
}

data class BaseResponse<T>(
    val code: String,
    val message: String,
    val status: Int,
    val data: T
)

interface NetworkStoreItemService {
    @GET("/shop/items")
    suspend fun loadItemsResponse(
        @Query("category") category: String,
        @Query("page") slice: Int,
        @Query("size") size: Int
    ): BaseResponse<CustomItemGroupDto>
}

interface NetworkEquippedItemService {
    @GET("/shop/items/equipped")
    suspend fun loadEquippedItemsResponse(): BaseResponse<List<EquippedItemDto>>
}

data class EquippedItemDto(
    val itemId: Int,
    val itemCategory: String,
    val imageUrl: String,
    val price: Int,
    val isPurchased: Boolean,
    val isEquipped: Boolean
) {
    fun toDomain(): CustomItem? {
        val itemType = itemCategory.toItemType()
        if(itemType == ItemType.DECO_1 && itemId == 17) {
            return null
        }
        if(itemType == ItemType.DECO_2 && itemId == 25) {
            return null
        }
        return CustomItem(
            id = itemId.toString(),
            type = itemType,
            price = Price(price),
            itemStatus = if (isPurchased) ItemStatus.PURCHASED else ItemStatus.PURCHASABLE,
            image = null,
            outfitImage = ItemImage.Url(imageUrl)
        )
    }
}

data class CustomItemDto(
    val itemId: Int,
    val itemCategory: String?,
    val shopImageUrl: String,
    val myImageUrl: String,
    val price: Int,
    val isPurchased: Boolean,
    val isEquipped: Boolean
)

private fun ItemType.toDto(): String =
    when (this) {
        ItemType.BACK -> "BACK"
        ItemType.SKIN -> "SKIN"
        ItemType.DECO_1 -> "DECOR_ONE"
        ItemType.DECO_2 -> "DECOR_TWO"
        ItemType.BACKGROUND -> "BACKGROUND"
    }

data class CustomItemGroupDto(
    val content: List<CustomItemDto>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean
)


@Singleton
class NetworkStoreRepository @Inject constructor(
    private val retrofit: Retrofit
) : StoreRepository {

    private val storeService by lazy {
        retrofit.create(NetworkStoreItemService::class.java)
    }

    private val equippedItemService by lazy {
        retrofit.create(NetworkEquippedItemService::class.java)
    }

    override fun loadStoreItems(itemType: ItemType): Flow<CustomItem> {
        return flow {
            var currentPage = 1
            while (true) {
                try {
                    val response = storeService.loadItemsResponse(
                        category = itemType.toDto(),
                        slice = currentPage,
                        size = 6
                    ).data

                    response.content.forEach { dto ->
                        emit(
                            CustomItem(
                                dto.itemId.toString(),
                                itemType,
                                Price(dto.price),
                                if (dto.isPurchased) ItemStatus.PURCHASED else ItemStatus.PURCHASABLE,
                                ItemImage.Url(dto.shopImageUrl),
                                ItemImage.Url(dto.myImageUrl)
                            )
                        )
                    }

                    if (!response.hasNext) {
                        break
                    }
                    currentPage++

                } catch (e: Exception) {
                    android.util.Log.e(
                        "StoreRepository",
                        "페이지 $currentPage 로드 중 에러 발생: $e"
                    )
                    break
                }
            }
        }
    }

    override suspend fun loadEquippedItems(): List<CustomItem> {
        val equippedItemsResponse: BaseResponse<List<EquippedItemDto>> =
            equippedItemService.loadEquippedItemsResponse()
        return equippedItemsResponse.data.mapNotNull { it.toDomain() }
    }
}

