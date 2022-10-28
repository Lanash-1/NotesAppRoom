package com.example.notesapproom.dao

import androidx.room.*
import com.example.notesapproom.entity.FavoriteNote

@Dao
interface FavoriteNoteDao {

    @Query("SELECT * FROM favorite_notes_table")
    fun getFavoriteNotes(): MutableList<FavoriteNote>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavoriteNote(favoriteNote: FavoriteNote)

    @Delete
    suspend fun removeFromFavorites(favoriteNote: FavoriteNote)

    @Query("DELETE FROM favorite_notes_table WHERE note_id LIKE :id")
    fun removeNoteFromFavorite(id: Int)

    @Query("SELECT * FROM favorite_notes_table WHERE note_id LIKE :id")
    fun isNoteAvailable(id: Int): MutableList<FavoriteNote>

}
