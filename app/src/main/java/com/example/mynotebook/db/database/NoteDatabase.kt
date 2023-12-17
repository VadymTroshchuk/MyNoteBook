package com.example.mynotebook.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mynotebook.db.model.NoteData


@Database(entities = [NoteData::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class NoteDatabase: RoomDatabase(){

    abstract fun noteModuleDao(): NoteDao

}