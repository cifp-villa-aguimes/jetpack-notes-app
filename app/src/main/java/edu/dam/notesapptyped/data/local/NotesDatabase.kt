package edu.dam.notesapptyped.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.dam.notesapptyped.data.local.dao.NotesDao
import edu.dam.notesapptyped.data.local.entity.NoteEntity

@Database(
    entities = [NoteEntity::class],
    version = 1,
    exportSchema = true
)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
}
