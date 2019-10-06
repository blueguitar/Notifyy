package com.suadahaji.notify.listnotes

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
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

        val manager = StaggeredGridLayoutManager(2, 1)

        binding.noteList.layoutManager = manager

        val adapter = NotesListAdapter(NoteClickListener { noteId ->
            notesListViewModel.onNoteClicked(noteId)
        })
        
        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                binding.noteList.scrollToPosition(0)
            }
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                binding.noteList.scrollToPosition(0)
            }
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                binding.noteList.scrollToPosition(0)
            }
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.noteList.scrollToPosition(0)
            }
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                binding.noteList.scrollToPosition(0)
            }
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                binding.noteList.scrollToPosition(0)
            }
        })

        binding.noteList.adapter = adapter

        notesListViewModel.navigatetoUpdateNote.observe(this, Observer {  note ->
            note?.let {
                this.findNavController().navigate(NotesListFragmentDirections.actionNotesListToEditNote(note, getString(R.string.update_note_tag)))
                notesListViewModel.onNoteUpdateNavigated()
            }
        })

        notesListViewModel.notes.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                binding.included.isVisible = it.size <= 0
            }
        })

        val sharedPref = activity!!.getSharedPreferences(getString(R.string.search_query), 0)
        val editor = sharedPref.edit()
        editor.putString(getString(R.string.search_query), "")
        editor.apply()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.search_icon -> {
                this.findNavController().navigate(NotesListFragmentDirections.actionNotesListToSearchFragment())
            }
        }
        return super.onOptionsItemSelected(item)
    }
}