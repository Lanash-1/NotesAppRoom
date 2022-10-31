package com.example.notesapproom.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapproom.R
import com.example.notesapproom.entity.Note
import com.example.notesapproom.databinding.ItemNoteBinding
import com.example.notesapproom.diffutils.NotesDiffUtils
import com.example.notesapproom.interfaces.OnItemClickListener
import com.example.notesapproom.interfaces.OnNoteOptionsClickListener
import java.text.SimpleDateFormat
import java.util.*

class NotesListAdapter : RecyclerView.Adapter<NotesListAdapter.NotesListViewHolder>()
{
    private lateinit var itemClickListener: OnItemClickListener

    private lateinit var noteOptionsClickListener: OnNoteOptionsClickListener

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

    fun setOnNoteOptionsClickListener(noteOptionsClickListener: OnNoteOptionsClickListener){
        this.noteOptionsClickListener = noteOptionsClickListener
    }

    inner class NotesListViewHolder(val binding: ItemNoteBinding, listener: OnItemClickListener): RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
            itemView.setOnLongClickListener{
                Toast.makeText(itemView.context, "long press", Toast.LENGTH_SHORT).show()
                true
            }

            binding.moreBtn.setOnClickListener{
                val popupMenu = PopupMenu(binding.moreBtn.context, it)

                noteOptionsClickListener.isFavorite(absoluteAdapterPosition){ isFavorite ->
                    if(isFavorite)
                        popupMenu.inflate(R.menu.isfavorite_note_menu)
                    else
                        popupMenu.inflate(R.menu.note_menu)
                }

                popupMenu.setOnMenuItemClickListener(object: PopupMenu.OnMenuItemClickListener{
                    override fun onMenuItemClick(item: MenuItem): Boolean {
                        when(item.itemId){
                            R.id.delete -> {
                                noteOptionsClickListener.deleteNote(absoluteAdapterPosition)
                                return true
                            }
                            R.id.addFavorite -> {
                                noteOptionsClickListener.addOrRemoveFavorite(absoluteAdapterPosition, false)
                                return true
                            }
                            R.id.removeFavorite -> {
                                noteOptionsClickListener.addOrRemoveFavorite(absoluteAdapterPosition, true)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemNoteBinding.inflate(layoutInflater, parent, false)
        return NotesListViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: NotesListViewHolder, position: Int) {
        val note = notesList[position]
        holder.binding.apply {
            titleText.text = note.title
            noteText.text = note.content

            val dateFormatter = SimpleDateFormat("yyyy/MM/dd")
            val calendar = Calendar.getInstance().time
            val currentDate = dateFormatter.format(calendar)
            val curr = dateFormatter.parse(currentDate)
            val modifiedDate = dateFormatter.parse(note.dateModified)

            val diff = kotlin.math.abs(curr.time - modifiedDate.time)
            val differenceDates = diff / (24 * 60 * 60 * 1000)
            val days = differenceDates.toString()

            time.apply {
                text = if(days.toInt() == 0){
                    "${note.timeModified}"
                }else if(days.toInt() > 1){
                    "$days days ago"
                }else{
                    "$days day ago"
                }
            }

            holder.itemView.setBackgroundColor(Color.parseColor(note.color))
        }
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

}