package com.egobook.app.store.data.network

import com.egobook.app.store.data.BaseResponse
import com.egobook.app.store.data.EquippedItemDto
import com.egobook.app.store.data.PermanentEquipRequest
import com.egobook.app.store.data.PurchaseRequest
import com.egobook.app.store.data.network.dto.ShopItemGroupDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface ShopApiService {
    @GET("/shop/items")
    suspend fun loadAllItems(
        @Query("category") category: String,
        @Query("page") slice: Int,
        @Query("size") size: Int
    ): BaseResponse<ShopItemGroupDto>

    @POST("/shop/purchase")
    suspend fun purchaseItem(@Body request: PurchaseRequest): BaseResponse<EquippedItemDto>

    @GET("/shop/items/equipped")
    suspend fun loadEquippedItems(): BaseResponse<List<EquippedItemDto>>

    @PATCH("/shop/equip")
    suspend fun equipItemPermanently(@Body request: PermanentEquipRequest): BaseResponse<EquippedItemDto>
}
