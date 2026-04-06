package com.example.anidex.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["userId", "mediaId", "type"], unique = true)
    ]
)
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val mediaId: Int,
    val title: String,
    val imageUrl: String? = null,
    val type: String,
    val subtitle: String? = null,
    val score: Int? = null,
    val episodes: Int? = null,
    val chapters: Int? = null,
    val volumes: Int? = null,
    val seasonOrStatus: String? = null,
    val genres: List<String> = emptyList(),
    val addedAt: Long = System.currentTimeMillis()
)