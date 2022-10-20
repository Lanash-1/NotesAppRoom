package com.example.notesapproom

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapproom.adapter.ColorAdapter
import com.example.notesapproom.interfaces.OnItemClickListener
import com.example.notesapproom.data.Note
import com.example.notesapproom.data.NoteDatabase
import com.example.notesapproom.viewModel.ColorViewModel
import com.example.notesapproom.viewModel.NotesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NewNoteFragment : Fragment() {

    private val notesViewModel: NotesViewModel by activityViewModels()
    private val colorViewModel: ColorViewModel by activityViewModels()

    private lateinit var noteText: EditText
    private lateinit var titleText: EditText

    private lateinit var appDb: NoteDatabase

    private var colorAdapter = ColorAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDb = NoteDatabase.getDatabase(requireContext())

        (activity as AppCompatActivity).supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(Color.parseColor(notesViewModel.note.color)))
            setDisplayHomeAsUpEnabled(true)
        }

        titleText = view.findViewById(R.id.titleText)
        noteText = view.findViewById(R.id.noteText)

        println("${notesViewModel.note}")
        titleText.setText(notesViewModel.note.title)
        noteText.setText(notesViewModel.note.content)
        setNoteColor(notesViewModel.note.color)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.create_note_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                insertOrUpdateDb()
            }
            R.id.done -> {
                insertOrUpdateDb()
            }
            R.id.delete -> {
                if(notesViewModel.notePosition != -1){
                    GlobalScope.launch {
                        deleteNoteFromDB(notesViewModel.dbNotesList.value!![notesViewModel.notePosition])
                    }
                }
                moveToNotesFragment()
            }
            R.id.colorPicker -> {
                openColorPicker()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertOrUpdateDb() {
        if(notesViewModel.notePosition == -1){
            notesViewModel.noteTitle = titleText.text.toString()
            notesViewModel.noteContent = noteText.text.toString()
            if(notesViewModel.noteTitle.isNotEmpty() || notesViewModel.noteContent.isNotEmpty()){
                insertNote()
            }
        }else{
            notesViewModel.noteTitle = titleText.text.toString()
            notesViewModel.noteContent = noteText.text.toString()
            notesViewModel.noteColor = notesViewModel.noteColor
            notesViewModel.noteId = notesViewModel.dbNotesList.value!![notesViewModel.notePosition].id!!
            updateNoteInDB()
        }
        moveToNotesFragment()
    }

    private fun openColorPicker() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.color_picker_bottom_sheet_dialog, null)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()

        val colorRecyclerView = view.findViewById<RecyclerView>(R.id.colorRV)
        colorAdapter.setColorList(colorViewModel.colors)
        colorRecyclerView.adapter = colorAdapter
        colorRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        colorAdapter.setOnItemClickListener(object: OnItemClickListener {
            override fun onItemClick(position: Int) {
                setNoteColor(colorViewModel.colors[position])
            }
        })

        val selectBtn = view.findViewById<TextView>(R.id.selectColor)
        selectBtn.setOnClickListener {
            dialog.dismiss()
        }
    }


    private fun setNoteColor(color: String) {
        notesViewModel.noteColor = color
        notesViewModel.note.color = color
        val layout = requireView().findViewById<ConstraintLayout>(R.id.noteLayout)
        layout.setBackgroundColor(Color.parseColor(color))
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(color)))
    }

    private suspend fun deleteNoteFromDB(note: Note) {
        appDb.noteDao().delete(note)
    }

    private fun updateNoteInDB() {
        GlobalScope.launch {
            appDb.noteDao().update(notesViewModel.noteId, notesViewModel.noteTitle, notesViewModel.noteContent, notesViewModel.noteColor)
        }
    }

    private fun insertNote() {
        GlobalScope.launch {
            appDb.noteDao().insert(Note(null, notesViewModel.noteTitle, notesViewModel.noteContent, notesViewModel.noteColor))
        }
    }

    private fun moveToNotesFragment(){
        parentFragmentManager.commit {
            replace(R.id.notesFragment, NotesFragment())
            parentFragmentManager.popBackStack()
        }
    }


}