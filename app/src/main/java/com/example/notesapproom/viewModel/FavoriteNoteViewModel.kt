package com.example.notesapproom.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notesapproom.entity.FavoriteNote
import com.example.notesapproom.entity.Note

class FavoriteNoteViewModel : ViewModel() {

    var notePosition: Int = -1


    var note: Note = Note(0, "", "", "#DDDDDD")


    var noteId: Int = 0
    var noteTitle: String = ""
    var noteContent: String = ""
    var noteColor: String = ""


    var dbFavoriteNoteList = mutableListOf<FavoriteNote>()

    var favoriteNotes = MutableLiveData<MutableList<Note>>()

}