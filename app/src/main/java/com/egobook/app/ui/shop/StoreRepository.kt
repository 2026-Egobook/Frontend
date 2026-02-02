package com.egobook.app.ui.shop

import com.egobook.app.data.interceptor.AuthInterceptor
import com.egobook.app.di.NetworkModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.ConcurrentHashMap
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
        @Query("slice") slice: Int,
        @Query("size") size: Int
    ): BaseResponse<CustomItemGroupDto>
}

data class CustomItemDto(
    val itemId: Int,
    val imageUrl: String,
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


class NetworkStoreRepository : StoreRepository {
    override fun loadStoreItems(itemType: ItemType): Flow<CustomItem> {
        val httpClient = NetworkModule.provideOkHttpClient(AuthInterceptor())
        val retrofit = NetworkModule.provideRetrofit(httpClient, GsonConverterFactory.create())
        val storeService = retrofit.create(NetworkStoreService::class.java)
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
                                ItemImage.Url(dto.imageUrl)
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

