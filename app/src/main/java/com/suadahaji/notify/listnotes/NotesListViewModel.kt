package com.suadahaji.notify.listnotes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suadahaji.notify.database.Note
import com.suadahaji.notify.database.NoteDao
import kotlinx.coroutines.*

class NotesListViewModel(
    val dataSource: NoteDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var note = MutableLiveData<Note?>()

    val notes = dataSource.getAllNotes()

    private var _searchNotes = MutableLiveData<List<Note>>()
    val searchNotes: LiveData<List<Note>>
    get() = _searchNotes


    private val _navigateToEditNote = MutableLiveData<Note>()
    val navigateToEditNote: LiveData<Note>
    get() = _navigateToEditNote

    fun doneNavigating() {
        _navigateToEditNote.value = null
    }

    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        uiScope.launch {
            note.value = getNoteFromDatabase()
        }
    }

    private suspend fun searchNote(searchWord: String): List<Note> {
        return withContext(Dispatchers.IO) {
            dataSource.searchNotes(searchWord)
        }
    }

    fun searchAllNotes(searchWord: String) {
        uiScope.launch {
            searchNote(searchWord)
            _searchNotes.value = searchNote(searchWord)
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

    private val _navigatetoUpdateNote = MutableLiveData<Long>()
    val navigatetoUpdateNote
    get() = _navigatetoUpdateNote

    fun onNoteClicked(id: Long) {
        _navigatetoUpdateNote.value = id
    }

    fun onNoteUpdateNavigated() {
        _navigatetoUpdateNote.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}