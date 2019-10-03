package com.suadahaji.notify.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

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
}