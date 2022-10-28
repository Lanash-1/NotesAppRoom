package com.example.notesapproom.interfaces

interface OnNoteOptionsClickListener {

    fun deleteNote(position : Int)
    fun addOrRemoveFavorite(position: Int, isFavorite: Boolean)
    fun isFavorite(position: Int, onClicked: (Boolean) -> Unit)

}