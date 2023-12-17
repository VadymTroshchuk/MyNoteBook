package com.example.mynotebook.fragments.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.mynotebook.R
import com.example.mynotebook.databinding.AddFragmentBinding
import com.example.mynotebook.db.model.NoteData
import com.example.mynotebook.db.viewmodel.NoteViewModel
import com.example.mynotebook.db.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

class AddFragment : Fragment() {

    private lateinit var binding: AddFragmentBinding

    private val noteViewModel: NoteViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_add -> {
                        insertDataToDatabase()
                    }
                    android.R.id.home -> {
                        findNavController().navigate(R.id.action_addFragment2_to_listFragment)
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.spinnerAddPriorities.onItemSelectedListener = sharedViewModel.listener
    }

    private fun insertDataToDatabase() {
        val title = binding.edAddTitle.text.toString()
        val color = binding.spinnerAddPriorities.selectedItem.toString()
        val description = binding.edAddDescription.text.toString()

        val validation = sharedViewModel.verifyDataFromUser(title, description)
        if (validation) {
            val newToDoData = NoteData(
                0,
                title,
                sharedViewModel.parseToColor(color),
                description
            )

            noteViewModel.insertData(newToDoData)
            Toast.makeText(requireContext(), getString(R.string.added_successfully), Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addFragment2_to_listFragment)
        } else {
            Toast.makeText(requireContext(), getString(R.string.please_fill_out_all_fields), Toast.LENGTH_SHORT)
                .show()
        }
    }

}