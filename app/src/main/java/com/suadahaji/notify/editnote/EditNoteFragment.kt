package com.suadahaji.notify.editnote

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.suadahaji.notify.R
import com.suadahaji.notify.database.Note
import com.suadahaji.notify.database.NotesDatabase
import com.suadahaji.notify.databinding.FragmentEditNoteBinding

class EditNoteFragment : Fragment() {
    private lateinit var noteTitle: EditText
    private lateinit var noteContent: EditText
    private lateinit var noteTag: String
    private var isNewNote: Boolean = true
    private var noteId: Long = 0L
    private lateinit var editNoteViewModel: EditNoteViewModel
    private var note: Note? = null
    private var saveButton: MenuItem? = null
    private var deleteButton: MenuItem? = null
    private var shareButton: MenuItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentEditNoteBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_note, container, false)
        setHasOptionsMenu(true)
        noteTitle = binding.noteTitle
        noteContent = binding.noteContent

        val application = requireNotNull(this.activity).application
        val dataSource = NotesDatabase.getInstance(application).noteDao
        val viewModelFactory = EditViewModelFactory(dataSource, application)
        editNoteViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(EditNoteViewModel::class.java)

        val arguments = EditNoteFragmentArgs.fromBundle(arguments!!)
        noteTag = arguments.noteTag
        if (noteTag.equals(resources.getString(R.string.update_note_tag))) {
            isNewNote = false
            noteId = arguments.noteId
        } else {
            displayContextMenu(true)
            noteTitle.requestFocus()
            hasFocus(noteTitle)
            hasFocus(noteContent)
        }

        binding.editNoteViewModel = editNoteViewModel
        binding.lifecycleOwner = this

        editNoteViewModel.note.observe(viewLifecycleOwner, Observer {
            it?.let {
                note = it
            }
        })

        editNoteViewModel.navigateToNotesLists.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(R.id.action_editNote_to_notesList)
                editNoteViewModel.doneNavigating()
            }
        })
        return binding.root
    }

    private fun hasFocus(editText: EditText) {
        editText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus)
                showSaveMenu(true)
            displayContextMenu(true)
        }
    }

    private fun displayContextMenu(showKeyboard: Boolean) {
        val input: InputMethodManager =
            activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (showKeyboard) {
            input.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        } else {
            input.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.show_note_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                editNoteViewModel.onDeleteNote()
            }
            R.id.save -> {
                saveNote(noteTitle.text.toString(), noteContent.text.toString())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun saveNote(note_title: String, note_content: String) {

        if (!note_title.isBlank() || !note_content.isBlank()) {
            if (note != null) {
                editNoteViewModel.onUpdateNewNote(note_title, note_content)
            } else {
                editNoteViewModel.onInsertNote(note_title, note_content)
            }
            noteTitle.clearFocus()
            noteContent.clearFocus()
            displayContextMenu(false)
            showSaveMenu(false)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        saveButton = menu.findItem(R.id.save)
        deleteButton = menu.findItem(R.id.delete)
        shareButton = menu.findItem(R.id.share)
        showSaveMenu(isNewNote)
    }

    fun showSaveMenu(showSaveItem: Boolean) {
        if (showSaveItem) {
            deleteButton?.isVisible = false
            shareButton?.isVisible = false
            saveButton?.isVisible = true
        } else {
            saveButton?.isVisible = false
            deleteButton?.isVisible = true
            shareButton?.isVisible = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveNote(noteTitle.text.toString(), noteContent.text.toString())
    }
}