package com.suadahaji.notify.listnotes

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.suadahaji.notify.database.NoteDao

class NotesListViewModelFactory(private val dataSource: NoteDao,
                                private val application: Application): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NotesListViewModel::class.java)) {
            return NotesListViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}