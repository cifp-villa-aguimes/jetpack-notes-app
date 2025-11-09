package edu.dam.notesapptyped.data.model

data class Note(
    val id: String,
    val title: String,
    val body: String,
    val author: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isFavorite: Boolean = false
)
