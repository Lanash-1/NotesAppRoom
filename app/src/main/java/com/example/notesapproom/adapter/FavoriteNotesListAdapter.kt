package com.example.notesapproom.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapproom.R
import com.example.notesapproom.databinding.ItemNoteBinding
import com.example.notesapproom.diffutils.NotesDiffUtils
import com.example.notesapproom.entity.Note
import com.example.notesapproom.interfaces.OnFavoriteNoteClickListener
import com.example.notesapproom.interfaces.OnItemClickListener

class FavoriteNotesListAdapter: RecyclerView.Adapter<FavoriteNotesListAdapter.FavoriteNotesListViewHolder>() {

    private lateinit var itemClickListener: OnItemClickListener

    private lateinit var favoriteNoteOptionsClickListener: OnFavoriteNoteClickListener

    private var notesList = listOf<Note>()

    fun setNotesList(newNotesList: List<Note>){
        val notesDiffUtil = NotesDiffUtils(notesList, newNotesList)
        val diffResult = DiffUtil.calculateDiff(notesDiffUtil)
        notesList = newNotesList
        diffResult.dispatchUpdatesTo(this)
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener){
        this.itemClickListener = itemClickListener
    }

    fun setOnFavoriteNoteOptionsClickListener(favoriteNoteClickListener: OnFavoriteNoteClickListener){
        this.favoriteNoteOptionsClickListener = favoriteNoteClickListener
    }


    inner class FavoriteNotesListViewHolder(val binding: ItemNoteBinding, listener: OnItemClickListener): RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }

            binding.moreBtn.setOnClickListener {
                val popupMenu = PopupMenu(binding.moreBtn.context, it)
                popupMenu.inflate(R.menu.favorite_note_menu)

                popupMenu.setOnMenuItemClickListener(object: PopupMenu.OnMenuItemClickListener{
                    override fun onMenuItemClick(item: MenuItem): Boolean {
                        when(item.itemId){
                            R.id.removeFromFavorite -> {
                                favoriteNoteOptionsClickListener.removeFromFavorite(absoluteAdapterPosition)
                                return true
                            }
                        }
                        return false
                    }
                })
                popupMenu.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteNotesListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemNoteBinding.inflate(layoutInflater, parent, false)
        return FavoriteNotesListViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: FavoriteNotesListViewHolder, position: Int) {
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
}