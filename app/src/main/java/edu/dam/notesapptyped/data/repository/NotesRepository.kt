package edu.dam.notesapptyped.data.repository

import edu.dam.notesapptyped.data.local.dao.NotesDao
import edu.dam.notesapptyped.data.mappers.toDomain
import edu.dam.notesapptyped.data.mappers.toEntity
import edu.dam.notesapptyped.data.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.UUID

interface NotesRepository {
    fun observeAll(): Flow<List<Note>>
    fun observeFavorites(): Flow<List<Note>>
    fun observeById(id: String): Flow<Note?>

    suspend fun addNote(title: String, body: String?, author: String, isFavorite: Boolean)
    suspend fun updateNote(id: String, title: String, body: String?)
    suspend fun toggleFavorite(id: String)
    suspend fun deleteNote(id: String)
}

class NotesRepositoryImpl(
    private val dao: NotesDao
) : NotesRepository {

    override fun observeAll(): Flow<List<Note>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun observeFavorites(): Flow<List<Note>> =
        dao.getFavorites().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: String): Flow<Note?> =
        dao.getById(id).map { entity -> entity?.toDomain() }

    override suspend fun addNote(
        title: String,
        body: String?,
        author: String,
        isFavorite: Boolean
    ) {
        val now = System.currentTimeMillis()
        val entity = Note(
            id = UUID.randomUUID().toString(),
            title = title,
            body = body.orEmpty(),
            author = author,
            createdAt = now,
            updatedAt = now,
            isFavorite = isFavorite
        ).toEntity()
        dao.insert(entity)
    }

    override suspend fun updateNote(id: String, title: String, body: String?) {
        val existing = dao.getById(id).firstOrNull() ?: return
        val updated = existing.copy(
            title = title,
            body = body,
            updatedAt = System.currentTimeMillis()
        )
        dao.update(updated)
    }

    override suspend fun toggleFavorite(id: String) {
        dao.toggleFavorite(id)
    }

    override suspend fun deleteNote(id: String) {
        dao.deleteById(id)
    }
}
