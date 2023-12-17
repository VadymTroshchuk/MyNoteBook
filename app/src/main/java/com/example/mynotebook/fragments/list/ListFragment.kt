package com.example.mynotebook.fragments.list

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mynotebook.R
import com.example.mynotebook.constants.SortingData
import com.example.mynotebook.databinding.ListFragmentBinding
import com.example.mynotebook.db.viewmodel.NoteViewModel
import com.example.mynotebook.db.viewmodel.SharedViewModel
import com.example.mynotebook.fragments.list.adapter.NoteRecycleViewAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import javax.inject.Inject


@AndroidEntryPoint
class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var binding: ListFragmentBinding

    private val recyclerViewAdapter: NoteRecycleViewAdapter by lazy { NoteRecycleViewAdapter() }

    private val toDoViewModel: NoteViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var searchQuery: String? = null

    private val sortingDataForUser = "sortingBy"
    private lateinit var userSortingFromSharedPreferences: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = ListFragmentBinding.inflate(inflater, container, false)

        setupRecyclerView()
        readUserSortingFromSharedPreferencesFile()

        toDoViewModel.userSortingTypeLiveData.observe(viewLifecycleOwner) {
            when (it) {
                SortingData.LATEST -> {
                    getDataByLatest()
                }
            }
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list_fragment_menu, menu)

                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.queryHint = getString(R.string.search_hint)
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@ListFragment)

                toDoViewModel.queryToSearchOnDatabaseLiveData.observe(viewLifecycleOwner) {
                    if (!it.isNullOrEmpty()) {
                        try {
                            searchView?.setQuery(it, false)
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        toDoViewModel.userSortingTypeLiveData.observe(viewLifecycleOwner) { sort ->
                            when (sort) {
                                SortingData.LATEST -> {
                                    getDataByLatest()
                                }
//
                            }
                        }
                    }
                }

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_priority_latest -> {
                        saveUserSortingOnSharedPreferencesFile(SortingData.LATEST)
                    }

                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        sharedViewModel.emptyDatabase.observe(viewLifecycleOwner) {
            showEmptyDatabaseViews(it)
        }

        binding.floatingActionBtn.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment2)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (!searchQuery.isNullOrEmpty()) {
            outState.putString("query", searchQuery)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val query = savedInstanceState?.getString("query")

        if (!query.isNullOrEmpty()) {
            toDoViewModel.setQueryForSearchOnDataBase(query)
        } else {
            toDoViewModel.setQueryForSearchOnDataBase(null)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = recyclerViewAdapter
        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)


    }

    private fun getDataByLatest() {
        toDoViewModel.getAllData.observe(viewLifecycleOwner) {
            sharedViewModel.checkIfDatabaseIsEmpty(it)
            recyclerViewAdapter.setToDoList(it)
        }
    }





    private fun showEmptyDatabaseViews(emptyDatabase: Boolean) {
        if (emptyDatabase) {
            binding.noDataImage.visibility = View.VISIBLE
            binding.tvNoData.visibility = View.VISIBLE
        } else {
            binding.noDataImage.visibility = View.INVISIBLE
            binding.tvNoData.visibility = View.INVISIBLE
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()) {
            searchQuery = query
            toDoViewModel.setQueryForSearchOnDataBase(query)
//            searchFromDatabase(query)
        } else {
            searchQuery = null
            toDoViewModel.setQueryForSearchOnDataBase(null)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (!newText.isNullOrEmpty()) {
            searchQuery = newText
            toDoViewModel.setQueryForSearchOnDataBase(newText)

        } else {
            searchQuery = null
            toDoViewModel.setQueryForSearchOnDataBase(null)
        }
        return true
    }



    private fun saveUserSortingOnSharedPreferencesFile(sortingData: String) {
        try {
            val editor = sharedPreferences.edit()

            when (sortingData) {
                SortingData.LATEST -> {
                    editor.putString(sortingDataForUser, SortingData.LATEST)
                    editor.apply()
                    readUserSortingFromSharedPreferencesFile()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun readUserSortingFromSharedPreferencesFile() {
        try {
            userSortingFromSharedPreferences =
                sharedPreferences.getString(sortingDataForUser, SortingData.LATEST).toString()

            toDoViewModel.setUserSortingType(SortingData.LATEST)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

}