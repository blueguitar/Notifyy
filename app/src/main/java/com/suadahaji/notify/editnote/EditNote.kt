package com.suadahaji.notify.editnote

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.suadahaji.notify.R
import com.suadahaji.notify.databinding.FragmentEditNoteBinding



class EditNote: Fragment(),ActionMode.Callback {
    private lateinit var input: InputMethodManager
    private lateinit var noteTitle: EditText
    private lateinit var noteContent: EditText
    private var mActionMode: ActionMode? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding:FragmentEditNoteBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_note, container, false)
        setHasOptionsMenu(true)
        input =
            activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        noteTitle = binding.noteTitle
        noteContent = binding.noteContent
        if (noteTitle.requestFocus()) {
            displayContextMenu()
        }

        hasFocus(noteTitle)
        hasFocus(noteContent)
        return binding.root
    }

    private fun hasFocus(editText: EditText) {
        editText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) displayContextMenu()
        }
    }

    private fun displayContextMenu() {
        input.showSoftInput(noteTitle, InputMethodManager.SHOW_IMPLICIT)
        if (mActionMode == null) {
            mActionMode =
                (activity as AppCompatActivity).startSupportActionMode(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.show_note_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.save -> {
//                input.hideSoftInputFromWindow(activity!!.currentFocus?.windowToken, 0)
//                noteTitle.clearFocus()
//                noteContent.clearFocus()
//            }
//        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        val menuInflater = activity?.menuInflater
        menuInflater?.inflate(R.menu.edit_note_menu, menu)
        mActionMode = mode
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        mActionMode = null
    }


    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.save -> {
                input.hideSoftInputFromWindow(activity!!.currentFocus?.windowToken, 0)
                noteTitle.clearFocus()
                noteContent.clearFocus()
                mActionMode?.finish()
            }
        }
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, item: Menu?): Boolean {
        return false
    }
}