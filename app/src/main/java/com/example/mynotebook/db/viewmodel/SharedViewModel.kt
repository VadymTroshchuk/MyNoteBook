package com.example.mynotebook.db.viewmodel

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mynotebook.R
import com.example.mynotebook.constants.Tag
import com.example.mynotebook.db.model.Color
import com.example.mynotebook.db.model.NoteData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(private val context: Application) : ViewModel() {

    private var _emptyDatabase: MutableLiveData<Boolean> = MutableLiveData(false)
    val emptyDatabase: LiveData<Boolean> get() = _emptyDatabase

    fun checkIfDatabaseIsEmpty(noteDataList: List<NoteData>) {
        _emptyDatabase.value = noteDataList.isEmpty()
    }

    val listener: AdapterView.OnItemSelectedListener = object :
        AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when (position) {
                0 -> {
                    try {
                        (parent?.getChildAt(0) as TextView).setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.red
                            )
                        )
                    } catch (e: Exception) {
                        Log.d(Tag.TAG, e.message.toString())
                    }
                }

                1 -> {
                    try {
                        (parent?.getChildAt(0) as TextView).setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.yellow
                            )
                        )
                    } catch (e: Exception) {
                        Log.d(Tag.TAG, e.message.toString())
                    }
                }

                2 -> {
                    try {
                        (parent?.getChildAt(0) as TextView).setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.green
                            )
                        )
                    } catch (e: Exception) {
                        Log.d(Tag.TAG, e.message.toString())
                    }
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    fun verifyDataFromUser(title: String, description: String): Boolean {
        return !(title.isEmpty() || description.isEmpty())
    }

    fun parseToColor(color: String): Color {
        return when (color) {
            "Red Colour" -> {
                Color.RED
            }

            "Yellow Colour" -> {
                Color.YELLOW
            }

            "Green Colour" -> {
                Color.GREEN
            }

            else -> {
                Color.RED
            }
        }
    }

    fun parseColor(color: Color): Int {
        return when (color) {
            Color.RED -> 0
            Color.YELLOW -> 1
            Color.GREEN -> 2
        }
    }
}