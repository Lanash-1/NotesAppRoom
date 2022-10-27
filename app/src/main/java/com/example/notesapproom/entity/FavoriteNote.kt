package com.example.notesapproom.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_notes_table",
    foreignKeys = [ForeignKey(
        entity = Note::class,
        childColumns = ["note_id"],
        parentColumns = ["id"]
    )]
)
data class FavoriteNote(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "note_id") val noteId: Int?
)
