package com.suadahaji.notify.listnotes

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.suadahaji.notify.R
import com.suadahaji.notify.database.NotesDatabase
import com.suadahaji.notify.databinding.FragmentNotesListBinding

class NotesListFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentNotesListBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_notes_list, container, false)
        setHasOptionsMenu(true)
        binding.addFab.setOnClickListener {
            this.findNavController().navigate(
                NotesListFragmentDirections.actionNotesListToEditNote(0L, getString(R.string.new_note_tag) ))
        }

        val application = requireNotNull(this.activity).application
        val dataSource = NotesDatabase.getInstance(application).noteDao
        val viewModelFactory = NotesListViewModelFactory(dataSource, application)
        val notesListViewModel = ViewModelProviders.of(this, viewModelFactory).get(NotesListViewModel::class.java)

        binding.notesListViewModel = notesListViewModel

        binding.setLifecycleOwner(this)

        val manager = GridLayoutManager(activity, 2)

        binding.noteList.layoutManager = manager

        notesListViewModel.notes.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(activity, it.size.toString(), Toast.LENGTH_SHORT).show()
            }
        })

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