package com.example.notesapproom.interfaces

interface OnNoteOptionsClickListener {
    fun deleteNote(position : Int)
    fun addToFavorite(position: Int)
    fun shareNote(position: Int)
}