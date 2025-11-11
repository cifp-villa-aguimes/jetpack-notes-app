package edu.dam.notesapptyped.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.dam.notesapptyped.data.model.Note
import edu.dam.notesapptyped.data.repository.NotesRepository
import edu.dam.notesapptyped.di.ServiceLocator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class NotesViewModel(
    private val repository: NotesRepository
) : ViewModel() {

    val notes: StateFlow<List<Note>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun observeNote(id: String): Flow<Note?> = repository.observeById(id)

    suspend fun addNote(title: String, body: String?, author: String, isFavorite: Boolean): Boolean =
        runCatching { repository.addNote(title, body, author, isFavorite) }.isSuccess

    suspend fun updateNote(id: String, title: String, body: String?): Boolean =
        runCatching { repository.updateNote(id, title, body) }.isSuccess

    suspend fun toggleFavorite(id: String): Boolean =
        runCatching { repository.toggleFavorite(id) }.isSuccess

    suspend fun deleteNote(id: String): Boolean =
        runCatching { repository.deleteNote(id) }.isSuccess
}

class NotesViewModelFactory(
    private val context: android.content.Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            val repository = ServiceLocator.provideNotesRepository(context)
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
