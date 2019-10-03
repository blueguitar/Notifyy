package com.suadahaji.notify.listnotes

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.suadahaji.notify.R
import com.suadahaji.notify.databinding.FragmentNotesListBinding

class NotesList: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentNotesListBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_notes_list, container, false)
        setHasOptionsMenu(true)
        binding.addFab.setOnClickListener {
            findNavController().navigate(R.id.action_notesList_to_editNote)
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}