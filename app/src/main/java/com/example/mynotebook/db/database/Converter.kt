package com.example.mynotebook.db.database

import androidx.room.TypeConverter
import com.example.mynotebook.db.model.Color

class Converter {

    @TypeConverter
    fun fromColor(color: Color): String {
        return color.name
    }

    @TypeConverter
    fun toColor(color: String): Color {
        return Color.valueOf(color)
    }


}