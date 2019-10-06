package com.suadahaji.notify.searchnote

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.suadahaji.notify.R
import com.suadahaji.notify.database.NotesDatabase
import com.suadahaji.notify.databinding.FragmentSearchNoteBinding
import com.suadahaji.notify.listnotes.NoteClickListener
import com.suadahaji.notify.listnotes.NotesListAdapter
import com.suadahaji.notify.listnotes.NotesListViewModel
import com.suadahaji.notify.listnotes.NotesListViewModelFactory

class SearchFragment : Fragment() {
    private lateinit var  sharedPref: SharedPreferences
    private lateinit var queryText: String

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

        sharedPref = activity!!.getSharedPreferences(getString(R.string.search_query), 0)

        binding.notesListViewModel = notesListViewModel

        binding.setLifecycleOwner(this)

        val manager = StaggeredGridLayoutManager(2, 1)

        binding.noteList.layoutManager = manager

        adapter = NotesListAdapter(NoteClickListener { noteId ->
            notesListViewModel.onNoteClicked(noteId)
        })

        binding.noteList.adapter = adapter

        setHasOptionsMenu(true)

        notesListViewModel.navigatetoUpdateNote.observe(this, Observer { note ->
            note?.let {
                this.findNavController().navigate(
                    SearchFragmentDirections.actionSearchFragmentToEditNote(
                        note,
                        getString(R.string.update_note_tag)
                    )
                )
                val editor = sharedPref.edit()
                editor.putString(getString(R.string.search_query), queryText)
                editor.apply()
                notesListViewModel.onNoteUpdateNavigated()
            }
        })

        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)

        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setIconifiedByDefault(true)
        searchView.isFocusable = true
        searchView.isIconified = false
        searchView.clearFocus()
        searchView.requestFocusFromTouch()
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        queryText = sharedPref.getString(getString(R.string.search_query), null)!!
        if (queryText.isNotEmpty()) {
            searchView.setQuery(queryText, false)
            loadQuery(queryText)
        }
        searchView.setOnSearchClickListener {
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    if (query.isNotEmpty()) loadQuery(query)
                    if (query.isEmpty()) loadQuery("null")
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    if (newText.length > 1) loadQuery(newText)
                    if (newText.isEmpty()) loadQuery("null")
                }
                return false
            }
        })
        searchView.setOnCloseListener {
            this.findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToNotesList())
            false
        }

    }

    private fun loadQuery(s: String) {
        queryText = s
        notesListViewModel.searchAllNotes("%$s%")
        notesListViewModel.searchNotes.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        val input: InputMethodManager =
            activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        input.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}