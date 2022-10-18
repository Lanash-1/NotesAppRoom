package com.example.notesapproom.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapproom.databinding.ItemColorBinding
import com.example.notesapproom.interfaces.OnItemClickListener

class ColorAdapter: RecyclerView.Adapter<ColorAdapter.ColorViewHolder>(), OnItemClickListener {

    private lateinit var listener: OnItemClickListener

    private lateinit var colorList: List<String>

    fun setColorList(colorList: List<String>){
        this.colorList = colorList
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }


    class ColorViewHolder(val binding: ItemColorBinding, listener: OnItemClickListener): RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemColorBinding.inflate(layoutInflater, parent, false)
        return ColorViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = colorList[position]
        holder.binding.apply {
            colorItem.setBackgroundColor(Color.parseColor(color))
        }
    }

    override fun getItemCount(): Int {
        return colorList.size
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }
}