package com.suadahaji.notify.editnote

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suadahaji.notify.database.Note
import com.suadahaji.notify.database.NoteDao
import kotlinx.coroutines.*

class EditNoteViewModel(
    val dataSource: NoteDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _note = MutableLiveData<Note>()
    val note: LiveData<Note>
        get() = _note

    private val _navigateToNotesLists = MutableLiveData<Boolean?>()
    val navigateToNotesLists: LiveData<Boolean?>
        get() = _navigateToNotesLists

    fun doneNavigating() {
        _navigateToNotesLists.value = null
    }

    private suspend fun deleteAllNotes() {
        withContext(Dispatchers.IO) {
            dataSource.deleteAllNotes()
        }
    }

    private suspend fun deleteNote(note: Note) {
        withContext(Dispatchers.IO) {
            dataSource.deleteNoteById(note)
        }
    }

    private suspend fun update(note: Note) {
        withContext(Dispatchers.IO) {
            dataSource.update(note)
        }
    }

    private suspend fun insert(note: Note) {
        withContext(Dispatchers.IO) {
            dataSource.insert(note)
        }
    }

    private suspend fun getNoteFromDatabase(): Note? {
        return withContext(Dispatchers.IO) {
            var note = dataSource.getNote()
            if (note?.updatedDate != note?.createdDate) {
                note = null
            }
            note
        }
    }

    private suspend fun getNote(key: Long): Note? {
        return withContext(Dispatchers.IO) {
             dataSource.getNoteById(key)
        }
    }

    fun getNoteById(key: Long) {
        uiScope.launch {
            getNote(key)
            _note.value = getNote(key)
        }
    }

    fun onInsertNote(noteTitle: String, noteContent: String) {
        uiScope.launch {
            val newNote = Note(noteTitle = noteTitle, noteContent = noteContent)
            insert(newNote)
            _note.value = getNoteFromDatabase()
        }
    }

    fun onUpdateNewNote(noteTitle: String, noteContent: String) {
        uiScope.launch {
            val oldNote = _note.value ?: return@launch
            oldNote.noteTitle = noteTitle
            oldNote.noteContent = noteContent
            oldNote.updatedDate = System.currentTimeMillis()
            update(oldNote)
        }
    }

    fun onDeleteAllNotes() {
        uiScope.launch {
            deleteAllNotes()
            _note.value = null
        }
    }

    fun onDeleteNote() {
        uiScope.launch {

            val currentNote = _note.value ?: return@launch

            deleteNote(currentNote)
            _navigateToNotesLists.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}