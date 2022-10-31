package com.example.notesapproom.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "notes_table")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "note_title")val title: String,
    @ColumnInfo(name = "note_content")val content: String,
    @ColumnInfo(name = "note_color") var color: String,
    @ColumnInfo(name = "created_date") var dateCreated: String,
    @ColumnInfo(name = "created_time") var timeCreated: String,
    @ColumnInfo(name = "modified_date") var dateModified: String,
    @ColumnInfo(name = "modified_time") var timeModified: String
)
