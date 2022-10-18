package com.example.notesapproom.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "note_title")val title: String,
    @ColumnInfo(name = "note_content")val content: String,
    @ColumnInfo(name = "note_color") var color: String
)
