package edu.dam.notesapptyped.data

import edu.dam.notesapptyped.data.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class AppState {
    val userName = MutableStateFlow("")

    private fun defaultNotes() = listOf(
        Note(
            id = UUID.randomUUID().toString(),
            title = "Bienvenida",
            body = "Esta es tu primera nota",
            author = "invitado",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isFavorite = true
        ),
        Note(
            id = UUID.randomUUID().toString(),
            title = "Consejo",
            body = "Pulsa + para añadir otra",
            author = "invitado",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isFavorite = false
        )
    )

    val notes = MutableStateFlow(defaultNotes())

    fun addNote(title: String, body: String, isFavorite: Boolean) {
        val author = userName.value.ifBlank { "invitado" }
        addNote(title, body, isFavorite, author)
    }

    fun addNote(title: String, body: String, isFavorite: Boolean, author: String) {
        val now = System.currentTimeMillis()
        val n = Note(
            id = UUID.randomUUID().toString(),
            title = title,
            body = body,
            author = author,
            createdAt = now,
            updatedAt = now,
            isFavorite = isFavorite
        )
        notes.update { it + n }
    }

    // AppState.kt
    fun updateNote(
        id: String,
        title: String,
        body: String,
        isFavorite: Boolean? = null   // si es null, no cambia el flag
    ) {
        val now = System.currentTimeMillis()
        notes.update { list ->
            list.map { n ->
                if (n.id == id) {
                    n.copy(
                        title = title,
                        body = body,
                        isFavorite = isFavorite ?: n.isFavorite,
                        updatedAt = now
                    )
                } else n
            }
        }
    }

    fun deleteNote(id: String) {
        notes.update { list -> list.filterNot { it.id == id } }
    }

    fun clearNotes() {
        notes.value = emptyList()
    }

    fun resetForLogout() {
        userName.value = ""
        clearNotes()
    }

    fun toggleFavorite(id: String) {
        notes.update { list ->
            list.map { n ->
                if (n.id == id)
                    n.copy(isFavorite = !n.isFavorite)
                else
                    n
            }
        }
    }
}