package com.suadahaji.notify.searchnote

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.suadahaji.notify.R
import com.suadahaji.notify.database.NotesDatabase
import com.suadahaji.notify.databinding.FragmentSearchNoteBinding
import com.suadahaji.notify.listnotes.NoteClickListener
import com.suadahaji.notify.listnotes.NotesListAdapter
import com.suadahaji.notify.listnotes.NotesListViewModel
import com.suadahaji.notify.listnotes.NotesListViewModelFactory

class SearchFragment : Fragment() {
    private lateinit var notesListViewModel: NotesListViewModel
    private lateinit var adapter: NotesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSearchNoteBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_note, container, false
        )

        val application = requireNotNull(this.activity).application
        val dataSource = NotesDatabase.getInstance(application).noteDao
        val viewModelFactory = NotesListViewModelFactory(dataSource, application)
        notesListViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            NotesListViewModel::class.java
        )

        binding.notesListViewModel = notesListViewModel

        binding.setLifecycleOwner(this)

        val manager = StaggeredGridLayoutManager(2, 1)

        binding.noteList.layoutManager = manager

        adapter = NotesListAdapter(NoteClickListener { noteId ->
            notesListViewModel.onNoteClicked(noteId)
        })

        binding.noteList.adapter = adapter

        setHasOptionsMenu(true)



        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)

        val searchView = menu.findItem(R.id.search).actionView as SearchView
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        searchView.setOnSearchClickListener {
            loadQuery("null")
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    if (query.isNotEmpty()) loadQuery("%$query%")
                    if (query.isEmpty()) loadQuery("null")
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    if (newText.length > 1) loadQuery("%$newText%")
                    if (newText.isEmpty()) loadQuery("null")
                }
                return false
            }
        })
        searchView.setOnCloseListener {
            loadQuery("%")
            false
        }

//        return super.onCreateOptionsMenu(menu, inflater)
//        var menuItem = menu.findItem(R.id.search)
//        menuItem.expandActionView()
//        return super.onCreateOptionsMenu(menu, inflater)
    }

    private fun loadQuery(s: String) {
        notesListViewModel.searchAllNotes(s)
        notesListViewModel.searchNotes.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
//                this.findNavController().navigate(NotesListFragmentDirections.actionNotesListToSearchFragment())

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val input: InputMethodManager =
            activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        input.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}