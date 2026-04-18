package com.egobook.app.store.data.local

import android.content.Context
import androidx.room.Room
import com.egobook.app.store.data.local.entities.ShopItemEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalShopDataSource @Inject constructor(
    @ApplicationContext applicationContext: Context
) {
    private val database = Room.databaseBuilder(
        applicationContext,
        ShopDatabase::class.java,
        "shop-database"
    ).fallbackToDestructiveMigration().build()

    val itemStream = database.shopItemDao().loadAllItems()

    suspend fun initializeItems(items: List<ShopItemEntity>) {
        database.shopItemDao().resetAllItems()
        database.shopItemDao().upsertItems(items)
    }
}

