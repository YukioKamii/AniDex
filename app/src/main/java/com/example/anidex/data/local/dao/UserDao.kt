package com.example.anidex.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.anidex.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun observeUserById(userId: Int): Flow<UserEntity?>

    @Query("""
        UPDATE users
        SET displayName = :displayName,
            username = :username,
            bio = :bio,
            darkThemeEnabled = :darkThemeEnabled,
            notificationsEnabled = :notificationsEnabled
        WHERE id = :userId
    """)
    suspend fun updateProfile(
        userId: Int,
        displayName: String,
        username: String,
        bio: String,
        darkThemeEnabled: Boolean,
        notificationsEnabled: Boolean
    )
}