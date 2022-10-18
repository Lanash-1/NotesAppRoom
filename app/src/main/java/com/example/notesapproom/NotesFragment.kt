package com.example.notesapproom

// imports
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapproom.adapter.NotesListAdapter
import com.example.notesapproom.interfaces.OnItemClickListener
import com.example.notesapproom.data.Note
import com.example.notesapproom.data.NoteDatabase
import com.example.notesapproom.viewModel.NotesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesFragment : Fragment() {

    private var adapter = NotesListAdapter()
    private val viewModel: NotesViewModel by activityViewModels()

    private lateinit var appDb: NoteDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDb = NoteDatabase.getDatabase(requireContext())

        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffff")))
        }


        val fab = view.findViewById<FloatingActionButton>(R.id.create_note_fab)

        fab.setOnClickListener {
            viewModel.note = Note(0, "", "", "#DDDDDD")
            viewModel.notePosition = -1
            parentFragmentManager.commit {
                replace(R.id.notesFragment, NewNoteFragment())
                addToBackStack(null)
            }
        }

        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                println("setting: ${viewModel.dbNotesList.value!![position]}")
                viewModel.note = viewModel.dbNotesList.value!![position]
                viewModel.notePosition = position
                parentFragmentManager.commit {
                    replace(R.id.notesFragment, NewNoteFragment())
                    addToBackStack(null)
                }
            }
        })

        val recyclerView = view.findViewById<RecyclerView>(R.id.notes_recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        GlobalScope.launch {
            var list: MutableList<Note>
            val job= launch {
                list=getNotesList()
                withContext(Dispatchers.Main){
                    viewModel.dbNotesList.value=list
                }
            }
            job.join()
            println("inside global launch - launch - ${viewModel.dbNotesList.value}")
            withContext(Dispatchers.Main){
                viewModel.dbNotesList.value?.let { adapter.setNotesList(it) }
                recyclerView.adapter = adapter
            }
        }

        viewModel.dbNotesList.observe(viewLifecycleOwner, Observer{
            println("Coming to live observer")
            adapter.setNotesList(it)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        })
    }

    private fun getNotesList():MutableList<Note> {

        val newList = appDb.noteDao().getAll()
        return newList

    }

}