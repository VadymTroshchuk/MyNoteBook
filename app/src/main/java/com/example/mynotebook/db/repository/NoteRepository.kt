package com.example.mynotebook.db.repository

import androidx.lifecycle.LiveData
import com.example.mynotebook.db.model.NoteData

interface  NoteRepository {


    val getAllData: LiveData<List<NoteData>>

    suspend fun insertData(noteData: NoteData)

    suspend fun updateData(noteData: NoteData)

    suspend fun deleteData(noteData: NoteData)

    suspend fun deleteAllData()

    fun searchOnDatabase(noteTitle: String): LiveData<List<NoteData>>
}