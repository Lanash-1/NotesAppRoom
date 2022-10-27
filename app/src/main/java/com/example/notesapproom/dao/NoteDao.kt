package com.example.notesapproom.dao

import androidx.room.*
import com.example.notesapproom.entity.Note

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Query("UPDATE notes_table SET note_title=:title, note_content=:content, note_color=:color WHERE id LIKE :id")
    suspend fun update(id: Int,title: String, content: String, color: String)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notes_table")
    fun getAll(): MutableList<Note>

    @Query("SELECT * FROM notes_table WHERE id LIKE :id")
    fun getFavoriteNote(id: Int): Note

}