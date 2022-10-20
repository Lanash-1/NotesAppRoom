package com.example.notesapproom.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notesapproom.data.Note

class NotesViewModel: ViewModel() {

    var note: Note = Note(0, "", "", "#DDDDDD")


    var dbNotesList = MutableLiveData<MutableList<Note>>()


    var noteId: Int = 0
    var noteTitle: String = ""
    var noteContent: String = ""
    var noteColor: String = ""


    var notePosition = -1
}