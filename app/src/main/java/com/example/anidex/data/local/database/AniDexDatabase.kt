package com.example.anidex.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.anidex.data.local.converter.Converters
import com.example.anidex.data.local.dao.FavoriteDao
import com.example.anidex.data.local.dao.HistoryDao
import com.example.anidex.data.local.dao.UserDao
import com.example.anidex.data.local.entity.FavoriteEntity
import com.example.anidex.data.local.entity.HistoryEntity
import com.example.anidex.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        FavoriteEntity::class,
        HistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AniDexDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun historyDao(): HistoryDao
}