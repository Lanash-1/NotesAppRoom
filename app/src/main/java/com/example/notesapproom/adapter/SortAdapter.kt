package com.example.notesapproom.adapter

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapproom.databinding.ItemSortBinding
import com.example.notesapproom.enums.SortOptions
import com.example.notesapproom.interfaces.OnItemClickListener

class SortAdapter: RecyclerView.Adapter<SortAdapter.SortViewHolder>() {

    private var currPos = 0

    fun setSortPosition(currPos: Int){
        this.currPos = currPos
    }

    private lateinit var listener: OnItemClickListener

    inner class SortViewHolder(val binding: ItemSortBinding, listener: OnItemClickListener): RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SortViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSortBinding.inflate(layoutInflater, parent, false)
        return SortViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: SortViewHolder, position: Int) {
        holder.binding.apply {
            when(SortOptions.values()[position]){
                SortOptions.TITLE_ASCENDING -> {
                    sortTitle.text = "Title"
                    sortType.text = "Ascending"
                }
                SortOptions.TITLE_DESCENDING -> {
                    sortTitle.text = "Title"
                    sortType.text = "Descending"
                }
                SortOptions.DATE_CREATED_NEWEST_FIRST -> {
                    sortTitle.text = "Date Created"
                    sortType.text = "Newest First"
                }
                SortOptions.DATE_CREATED_OLDEST_FIRST -> {
                    sortTitle.text = "Date Created"
                    sortType.text = "Oldest First"
                }
                SortOptions.DATE_MODIFIED_NEWEST_FIRST -> {
                    sortTitle.text = "Date Modified"
                    sortType.text = "Newest First"
                }
                SortOptions.DATE_MODIFIED_OLDEST_FIRST -> {
                    sortTitle.text = "Date Modified"
                    sortType.text = "Oldest First"
                }
            }
//            if(sortList[position]){
//                println("RESULT: ${sortList[position]} - position ${position}")
//                check.visibility = View.VISIBLE
//            }

            if(currPos == position){
                check.visibility = View.VISIBLE
            }

        }

    }

    override fun getItemCount(): Int {
        return 6
    }
}