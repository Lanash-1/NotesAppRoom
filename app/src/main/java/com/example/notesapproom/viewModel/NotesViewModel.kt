package com.example.notesapproom.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notesapproom.entity.Note

class NotesViewModel: ViewModel() {

    var note: Note = Note(0, "", "", "#DDDDDD", "", "", "", "")

    var dbNotesList = MutableLiveData<MutableList<Note>>()

    var noteId: Int = 0
    var noteTitle: String = ""
    var noteContent: String = ""
    var noteColor: String = ""
    var createdDate: String = ""
    var createdTime: String = ""
    var modifiedDate: String = ""
    var modifiedTime: String = ""

    var notePosition = -1
}