package com.example.mynotebook.fragments.list

import android.app.ActionBar
import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
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
import com.example.mynotebook.db.model.NoteData
import com.example.mynotebook.db.viewmodel.NoteViewModel
import com.example.mynotebook.db.viewmodel.SharedViewModel
import com.example.mynotebook.fragments.list.adapter.NoteRecycleViewAdapter
import com.example.mynotebook.utils.hideKeyboard
import com.example.mynotebook.utils.observeOnce
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import javax.inject.Inject


@AndroidEntryPoint
class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var binding: ListFragmentBinding

    private val recyclerViewAdapter: NoteRecycleViewAdapter by lazy { NoteRecycleViewAdapter() }

    private val noteViewModel: NoteViewModel by viewModels()
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




        noteViewModel.userSortingTypeLiveData.observe(viewLifecycleOwner) {
            when (it) {
                SortingData.LATEST -> {
                    getDataByLatest()
                }
            }
        }

        hideKeyboard(requireActivity())

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

                noteViewModel.queryToSearchOnDatabaseLiveData.observe(viewLifecycleOwner) {
                    if (!it.isNullOrEmpty()) {
                        try {
                            searchView?.setQuery(it, false)
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        noteViewModel.userSortingTypeLiveData.observe(viewLifecycleOwner) { sort ->
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
                    R.id.menu_delete_all -> {
                        confirmDeleteAllData()
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
            noteViewModel.setQueryForSearchOnDataBase(query)
        } else {
            noteViewModel.setQueryForSearchOnDataBase(null)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = recyclerViewAdapter
        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        //        binding.recyclerView.itemAnimator = SlideInUpAnimator().apply {
//            addDuration = 200
//        }
        swipeToDelete(binding.recyclerView)



    }

    private fun getDataByLatest() {
        noteViewModel.getAllData.observe(viewLifecycleOwner) {
            sharedViewModel.checkIfDatabaseIsEmpty(it)
            recyclerViewAdapter.setToDoList(it)
        }
    }



    private fun confirmDeleteAllData() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.remove_everything))
        builder.setMessage(getString(R.string.are_you_sure_to_remove_everything))
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            noteViewModel.deleteAllData()
            Toast.makeText(
                requireContext(),
                getString(R.string.everything_removed_successfully),
                Toast.LENGTH_SHORT
            )
                .show()
        }
        builder.setNegativeButton(getString(R.string.no)) { _, _ -> }
        builder.show()
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
            noteViewModel.setQueryForSearchOnDataBase(query)
            searchFromDatabase(query)
        } else {
            searchQuery = null
            noteViewModel.setQueryForSearchOnDataBase(null)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (!newText.isNullOrEmpty()) {
            searchQuery = newText
            noteViewModel.setQueryForSearchOnDataBase(newText)
//            searchFromDatabase(newText)
        } else {
            searchQuery = null
            noteViewModel.setQueryForSearchOnDataBase(null)
        }
        return true
    }

    private fun searchFromDatabase(query: String) {
        if (query.isNotEmpty()) {
            val searchQuery = "%$query%"

            noteViewModel.searchOnDatabase(searchQuery).observeOnce(viewLifecycleOwner) { list ->
                list?.let {
                    recyclerViewAdapter.setToDoList(it)
                }
            }
        }
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemToDelete = recyclerViewAdapter.noteDataList[viewHolder.adapterPosition]
                noteViewModel.deleteData(itemToDelete)
                recyclerViewAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                recyclerViewAdapter.notifyItemRangeRemoved(
                    0,
                    recyclerViewAdapter.noteDataList.size - 1
                )
                noteViewModel.setUserSortingType(userSortingFromSharedPreferences)
                restoreDeletedData(viewHolder.itemView, itemToDelete)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(view: View, deletedItem: NoteData) {
        val snackBar = Snackbar.make(
            view,
            "'${deletedItem.title}' ${getString(R.string.deleted)}",
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction(getString(R.string.undo)) {
            noteViewModel.insertData(deletedItem)
            noteViewModel.setUserSortingType(userSortingFromSharedPreferences)
        }
        snackBar.show()
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

            noteViewModel.setUserSortingType(SortingData.LATEST)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

}
