package com.example.notesapproom.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.notesapproom.dao.FavoriteNoteDao
import com.example.notesapproom.data.NoteDatabase
import com.example.notesapproom.entity.FavoriteNote
import com.example.notesapproom.entity.Note
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DbViewModel(
    application: Application
): AndroidViewModel(application) {

    private val appDb = NoteDatabase.getDatabase(application.applicationContext)

    suspend fun deleteNoteFromDB(note: Note) {
        appDb.noteDao().delete(note)
    }

    fun updateNoteInDB(id: Int, title: String, content: String, color: String) {
        GlobalScope.launch {
            appDb.noteDao().update(id, title, content, color)
        }
    }

    fun insertNote(title: String, content: String, color: String) {
        GlobalScope.launch {
            appDb.noteDao().insert(Note(null, title, content, color))
        }
    }

    fun getNotesList(): MutableList<Note> {
        return appDb.noteDao().getAll()
    }

    fun getFavoriteNote(id: Int): Note{
        return appDb.noteDao().getFavoriteNote(id)
    }

    fun getFavoriteNotes(favorites: List<FavoriteNote>): MutableList<Note>{
        var list = mutableListOf<Note>()
        for(element in favorites){
            list.add(getFavoriteNote(element.noteId!!))
            println("NOTE ID: ${element.noteId}")
        }
        return list
    }

    fun addNoteToFavorite(noteToBeAddedToFavorite: Note) {
        GlobalScope.launch {
            appDb.favoriteNoteDao().insertFavoriteNote(FavoriteNote(null, noteToBeAddedToFavorite.id))
        }
    }

    fun removeNoteFromFavorite(noteToBeRemoved: FavoriteNote){
        GlobalScope.launch {
            appDb.favoriteNoteDao().removeFromFavorites(noteToBeRemoved)
        }
    }

    fun getfavoriteNotesIdList(): MutableList<FavoriteNote> {
        return appDb.favoriteNoteDao().getFavoriteNotes()
    }


}