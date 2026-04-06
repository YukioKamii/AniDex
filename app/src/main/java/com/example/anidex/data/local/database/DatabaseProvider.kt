package com.example.anidex.data.local.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AniDexDatabase? = null

    fun getDatabase(context: Context): AniDexDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AniDexDatabase::class.java,
                "anidex_database"
            ).build()

            INSTANCE = instance
            instance
        }
    }
}