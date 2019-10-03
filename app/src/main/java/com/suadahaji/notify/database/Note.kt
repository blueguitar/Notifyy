package com.suadahaji.notify.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var noteId:Long = 0L,
    @ColumnInfo(name = "note_title")
    var noteTitle: String,
    @ColumnInfo(name = "note_content")
    var noteContent: String,
    @ColumnInfo(name = "created_on")
    var createdDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_on")
    var updatedDate: Long = createdDate
)