package com.example.mynotebook.di

import android.content.Context
import androidx.room.Room
import com.example.mynotebook.db.database.NoteDao
import com.example.mynotebook.db.database.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    @Named("nameOfDatabase")
    fun provideDataBaseName(): String = "note_database"

    @Singleton
    @Provides
    fun provideNoteDataBase(@ApplicationContext context: Context, @Named("nameOfDatabase") name: String): NoteDatabase =
        Room.databaseBuilder(context, NoteDatabase::class.java, name).build()

    @Singleton
    @Provides
    fun provideNoteDao(db: NoteDatabase): NoteDao = db.noteModuleDao()

}