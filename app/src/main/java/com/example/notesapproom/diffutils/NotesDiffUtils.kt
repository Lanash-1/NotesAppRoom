package com.example.notesapproom.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.example.notesapproom.data.Note

class NotesDiffUtils(
    private val oldNotesList: List<Note>,
    private val newNotesList: List<Note>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldNotesList.size
    }

    override fun getNewListSize(): Int {
        return newNotesList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldNotesList[oldItemPosition] == newNotesList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when{
            oldNotesList[oldItemPosition].id != newNotesList[newItemPosition].id -> {
                false
            }
            oldNotesList[oldItemPosition].title != newNotesList[newItemPosition].title -> {
                false
            }
            oldNotesList[oldItemPosition].content != newNotesList[newItemPosition].content -> {
                false
            }
            oldNotesList[oldItemPosition].color != newNotesList[newItemPosition].color -> {
                false
            }
            else -> true
        }
    }
}