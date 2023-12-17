package com.example.mynotebook.db.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotebook.constants.SortingData
import com.example.mynotebook.db.model.NoteData
import com.example.mynotebook.db.repository.NoteRepositorylmp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor (private val repository: NoteRepositorylmp) : ViewModel() {




    val getAllData: LiveData<List<NoteData>> = repository.getAllData


    private var _queryToSearchOnDatabaseMutableLiveData: MutableLiveData<String?> = MutableLiveData(null)
    val queryToSearchOnDatabaseLiveData: LiveData<String?> get() = _queryToSearchOnDatabaseMutableLiveData

    private var _userSortingTypeMutableLiveData: MutableLiveData<String> = MutableLiveData(
        SortingData.LATEST)
    val userSortingTypeLiveData: LiveData<String> get() = _userSortingTypeMutableLiveData

    fun insertData(noteData: NoteData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(noteData)
        }
    }

    fun updateData(noteData: NoteData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateData(noteData)
        }
    }

    fun deleteData(noteData: NoteData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteData(noteData)
        }
    }

    fun deleteAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllData()
        }
    }

    fun searchOnDatabase(toDoTitle: String): LiveData<List<NoteData>> {
        return repository.searchOnDatabase(toDoTitle)
    }

    fun setQueryForSearchOnDataBase(query: String?) {
        _queryToSearchOnDatabaseMutableLiveData.value = query
    }

    fun setUserSortingType(sortingType: String) {
        _userSortingTypeMutableLiveData.value = sortingType
    }

}
