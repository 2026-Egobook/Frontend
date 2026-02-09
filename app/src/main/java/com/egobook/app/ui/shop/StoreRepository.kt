package com.egobook.app.ui.shop

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

interface StoreRepository {
    fun loadStoreItems(itemType: ItemType): Flow<CustomItem>
}

data class BaseResponse<T>(
    val code: String,
    val message: String,
    val status: Int,
    val data: T
)

interface NetworkStoreService {
    @GET("/shop/items")
    suspend fun loadItemsResponse(
        @Query("category") category: String,
        @Query("page") slice: Int,
        @Query("size") size: Int
    ): BaseResponse<CustomItemGroupDto>
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
        retrofit.create(NetworkStoreService::class.java)
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
                                ItemImage.Url(dto.shopImageUrl)
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
}

