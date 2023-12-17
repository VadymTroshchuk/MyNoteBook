package com.example.mynotebook.db.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mynotebook.db.model.NoteData

@Dao
interface NoteDao {

    @Query("select * from note_table order by id asc")
    fun getAllData(): LiveData<List<NoteData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(noteData: NoteData)

    @Update
    suspend fun updateData(noteData: NoteData)

    @Delete
    suspend fun deleteData(noteData: NoteData)

    @Query("delete from note_table")
    suspend fun deleteAllData()

    @Query("select * from note_table where title like :noteTitle")
    fun searchOnDatabase(noteTitle: String): LiveData<List<NoteData>>


}