package com.example.anidex.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val password: String,
    val displayName: String,
    val username: String,
    val bio: String,
    val darkThemeEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true
)