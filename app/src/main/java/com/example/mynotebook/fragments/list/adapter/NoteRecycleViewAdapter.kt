
package com.example.mynotebook.fragments.list.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotebook.R
import com.example.mynotebook.databinding.ItemOnRecyclerViewBinding
import com.example.mynotebook.db.model.Color
import com.example.mynotebook.db.model.NoteData
import com.example.mynotebook.fragments.list.ListFragmentDirections

class NoteRecycleViewAdapter : RecyclerView.Adapter<NoteRecycleViewAdapter.NoteViewHolder>() {

    var noteDataList = emptyList<NoteData>()

    private var noteDiffUtil: NoteDiffUtil? = null

    fun setToDoList(noteList: List<NoteData>) {
        noteDiffUtil = NoteDiffUtil(noteDataList, noteList)
        val toDoDiffResult = DiffUtil.calculateDiff(noteDiffUtil!!)
        this.noteDataList = noteList
        toDoDiffResult.dispatchUpdatesTo(this)
    }

    class NoteViewHolder(private val binding: ItemOnRecyclerViewBinding  ) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceType")
        fun bind(noteData: NoteData) {
            binding.tvItemTitle.text = noteData.title
            binding.tvItemDescription.text = noteData.description

            // to go to update fragment
            binding.constraintLayoutItemContainer.setOnClickListener {
                val action = ListFragmentDirections.actionListFragmentToUpdateFragment(noteData)
                itemView.findNavController().navigate(action)
            }

            when (noteData.color) {
                Color.RED -> {
                    binding.tvViewPriorityIndicator.backgroundTintList =
                        ContextCompat.getColorStateList(itemView.context, R.color.red)
                }

                Color.YELLOW -> {
                    binding.tvViewPriorityIndicator.backgroundTintList =
                        ContextCompat.getColorStateList(itemView.context, R.color.yellow)
                }

                Color.GREEN -> {
                    binding.tvViewPriorityIndicator.backgroundTintList =
                        ContextCompat.getColorStateList(itemView.context, R.color.green)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding =
            ItemOnRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val toDoData: NoteData = noteDataList[position]
        holder.bind(toDoData)
    }

    override fun getItemCount(): Int {
        return noteDataList.size
    }
}


