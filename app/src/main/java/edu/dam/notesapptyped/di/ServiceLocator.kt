package edu.dam.notesapptyped.di

import android.content.Context
import androidx.room.Room
import edu.dam.notesapptyped.data.local.NotesDatabase
import edu.dam.notesapptyped.data.local.dao.NotesDao
import edu.dam.notesapptyped.data.repository.NotesRepository
import edu.dam.notesapptyped.data.repository.NotesRepositoryImpl

object ServiceLocator {
    @Volatile
    private var database: NotesDatabase? = null

    @Volatile
    private var notesRepository: NotesRepository? = null

    fun provideNotesDatabase(context: Context): NotesDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                NotesDatabase::class.java,
                "notes.db"
            ).build().also { database = it }
        }
    }

    fun provideNotesDao(context: Context): NotesDao =
        provideNotesDatabase(context).notesDao()

    fun provideNotesRepository(context: Context): NotesRepository {
        return notesRepository ?: synchronized(this) {
            notesRepository ?: NotesRepositoryImpl(provideNotesDao(context)).also {
                notesRepository = it
            }
        }
    }
}
