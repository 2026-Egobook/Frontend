package com.egobook.app.store.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.egobook.app.store.data.local.dao.ShopItemDao
import com.egobook.app.store.data.local.entities.ShopItemEntity

@Database(entities = [ShopItemEntity::class], version = 2)
abstract class ShopDatabase : RoomDatabase() {
    abstract fun shopItemDao(): ShopItemDao
}
