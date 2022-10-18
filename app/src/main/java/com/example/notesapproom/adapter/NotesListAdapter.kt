package com.example.notesapproom.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapproom.data.Note
import com.example.notesapproom.databinding.ItemNoteBinding
import com.example.notesapproom.interfaces.OnItemClickListener

class NotesListAdapter : RecyclerView.Adapter<NotesListAdapter.NotesListViewHolder>(),
    OnItemClickListener {
    private lateinit var listener: OnItemClickListener

    private lateinit var notesList: List<Note>

    fun setNotesList(notesList: List<Note>){
        this.notesList = notesList
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    class NotesListViewHolder(val binding: ItemNoteBinding, listener: OnItemClickListener): RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemNoteBinding.inflate(layoutInflater, parent, false)
        return NotesListViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: NotesListViewHolder, position: Int) {
        val note = notesList[position]
        holder.binding.apply {
            titleText.text = note.title
            noteText.text = note.content
            holder.itemView.setBackgroundColor(Color.parseColor(note.color))
        }
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }
}