package edu.dam.notesapptyped.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    indices = [Index("updatedAt"), Index("isFavorite")]
)
data class NoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String?,
    val author: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isFavorite: Boolean
)
