package com.example.mynotebook.fragments

import android.app.AlertDialog
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
import androidx.navigation.fragment.navArgs
import com.example.mynotebook.R
import com.example.mynotebook.databinding.UpdateFragmentBinding
import com.example.mynotebook.db.model.NoteData
import com.example.mynotebook.db.viewmodel.NoteViewModel
import com.example.mynotebook.db.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UpdateFragment : Fragment() {

    private lateinit var binding: UpdateFragmentBinding

    private val args by navArgs<UpdateFragmentArgs>()

    private val noteViewModel: NoteViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UpdateFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.update_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_save -> {
                        updateCurrentData()
                    }
                    R.id.menu_delete -> {
                        confirmRemoveData()
                    }
                    android.R.id.home -> {
                        findNavController().navigate(R.id.action_updateFragment_to_listFragment)
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        getDataFromListFragmentToUpdateIt()
    }

    private fun getDataFromListFragmentToUpdateIt() {
        try {
            binding.edAddTitle.setText(args.currentItem.title)
            binding.edAddDescription.setText(args.currentItem.description)
            binding.spinnerAddPriorities.setSelection(sharedViewModel.parseColor(args.currentItem.color))
            binding.spinnerAddPriorities.onItemSelectedListener = sharedViewModel.listener
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCurrentData() {
        val title = binding.edAddTitle.text.toString()
        val description = binding.edAddDescription.text.toString()
        val color = binding.spinnerAddPriorities.selectedItem.toString()

        val validation = sharedViewModel.verifyDataFromUser(title, description)
        if (validation) {
            val newItem = NoteData(
                args.currentItem.id,
                title,
                sharedViewModel.parseToColor(color),
                description
            )

            noteViewModel.updateData(newItem)
            Toast.makeText(requireContext(), "'${newItem.title}' ${getString(R.string.updated_successfully)}", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(), getString(R.string.please_fill_out_all_fields), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun confirmRemoveData() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("${getString(R.string.remove)} '${args.currentItem.title}'!")
        builder.setMessage("${getString(R.string.are_you_sure_to_remove)} '${args.currentItem.title}'?")
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            noteViewModel.deleteData(args.currentItem)
            Toast.makeText(requireContext(), "'${args.currentItem.title}' ${getString(R.string.removed)}", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }

        builder.setNegativeButton(getString(R.string.no)) { _, _ -> }
        builder.show()
    }
}