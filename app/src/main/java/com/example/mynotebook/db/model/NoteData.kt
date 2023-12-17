package com.example.mynotebook.db.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "note_table")
@kotlinx.parcelize.Parcelize
data class NoteData (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var color: Color,
    var description: String
): Parcelable