package com.example.anidex.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.anidex.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity): Long

    @Query("DELETE FROM favorites WHERE userId = :userId AND mediaId = :mediaId AND type = :type")
    suspend fun deleteFavorite(userId: Int, mediaId: Int, type: String)

    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY addedAt DESC")
    fun getFavoritesByUser(userId: Int): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorites WHERE userId = :userId AND type = :type ORDER BY addedAt DESC")
    fun getFavoritesByUserAndType(userId: Int, type: String): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND mediaId = :mediaId AND type = :type)")
    suspend fun isFavorite(userId: Int, mediaId: Int, type: String): Boolean

    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId")
    fun countFavorites(userId: Int): Flow<Int>
}