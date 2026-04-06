package com.example.anidex.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.anidex.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity): Long

    @Query("SELECT * FROM history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getHistoryByUser(userId: Int): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentHistoryByUser(userId: Int, limit: Int): Flow<List<HistoryEntity>>

    @Query("DELETE FROM history WHERE userId = :userId")
    suspend fun clearHistory(userId: Int)
}