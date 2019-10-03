package com.suadahaji.notify.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {
    @Insert
    fun insert(note: Note)

    @Update
    fun update(note: Note)

    @Query("SELECT * FROM notes WHERE noteId= :key")
    fun get(key: Long): Note?

    @Query("SELECT * FROM notes WHERE noteId= :key")
    fun getNote(key: Long): LiveData<Note>

    @Query("SELECT * FROM notes ORDER BY created_on DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes ORDER BY noteId DESC LIMIT 1")
    fun getNote(): Note?

    @Query("DELETE FROM notes")
    fun deleteAllNotes()

    @Delete
    fun deleteNoteById(note: Note)
}