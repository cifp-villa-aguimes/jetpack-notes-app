package edu.dam.notesapptyped.data.mappers

import edu.dam.notesapptyped.data.local.entity.NoteEntity
import edu.dam.notesapptyped.data.model.Note

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    body = body.orEmpty(),
    author = author,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isFavorite = isFavorite
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    body = body.takeIf { it.isNotBlank() },
    author = author,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isFavorite = isFavorite
)
