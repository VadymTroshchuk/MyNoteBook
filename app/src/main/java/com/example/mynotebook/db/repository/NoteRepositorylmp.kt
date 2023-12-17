package com.example.mynotebook.db.repository

import androidx.lifecycle.LiveData
import com.example.mynotebook.db.database.NoteDao
import com.example.mynotebook.db.model.NoteData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteRepositorylmp @Inject constructor (private val noteDao: NoteDao) : NoteRepository {


    override val getAllData: LiveData<List<NoteData>>
        get() = noteDao.getAllData()


    override suspend fun insertData(noteData: NoteData) {
        withContext(Dispatchers.IO) {
            noteDao.insertData(noteData)
        }
    }

    override suspend fun updateData(noteData: NoteData) {
        withContext(Dispatchers.IO) {
            noteDao.updateData(noteData)
        }
    }

    override suspend fun deleteData(noteData: NoteData) {
        withContext(Dispatchers.IO) {
            noteDao.deleteData(noteData)
        }
    }

    override suspend fun deleteAllData() {
        withContext(Dispatchers.IO) {
            noteDao.deleteAllData()
        }
    }

    override fun searchOnDatabase(noteTitle: String): LiveData<List<NoteData>> {
        return noteDao.searchOnDatabase(noteTitle)
    }


}