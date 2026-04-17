package com.egobook.app.store.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.egobook.app.store.data.local.entities.ShopItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopItemDao {
    @Query("SELECT * FROM shop_item")
    fun loadAllItems(): Flow<List<ShopItemEntity>>

    @Query("DELETE FROM shop_item")
    suspend fun resetAllItems()

    @Upsert
    suspend fun upsertItems(items: List<ShopItemEntity>)
}
