package com.example.notesapproom

// imports
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.example.notesapproom.interfaces.OnNoteOptionsClickListener
import com.example.notesapproom.viewModel.NotesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesFragment : Fragment() {

    private var adapter = NotesListAdapter()
    private val notesViewModel: NotesViewModel by activityViewModels()

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

        val recyclerView = view.findViewById<RecyclerView>(R.id.notes_recyclerView)

        val fab = view.findViewById<FloatingActionButton>(R.id.create_note_fab)

        fab.setOnClickListener {
            notesViewModel.note = Note(0, "", "", "#EEEEEE")
            notesViewModel.notePosition = -1
            parentFragmentManager.commit {
                replace(R.id.notesFragment, NewNoteFragment())
                addToBackStack(null)
            }
        }

        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                notesViewModel.note = notesViewModel.dbNotesList.value!![position]
                notesViewModel.notePosition = position
                parentFragmentManager.commit {
                    replace(R.id.notesFragment, NewNoteFragment())
                    addToBackStack(null)
                }
            }
        })

        adapter.setOnNoteOptionsClickListener(object: OnNoteOptionsClickListener{
            override fun deleteNote(position: Int) {
                val noteToBeDeleted = notesViewModel.dbNotesList.value!![position]
                GlobalScope.launch {
                    deleteNoteFromDb(noteToBeDeleted)
                }
            }

            override fun addToFavorite(position: Int) {
                Log.d(null, "add to favorie")
            }

            override fun shareNote(position: Int) {
                Log.d(null, "share note")
            }
        })

        recyclerView.adapter = adapter

        if(activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.layoutManager = GridLayoutManager(context, 2)
        }else{
            recyclerView.layoutManager = GridLayoutManager(context, 4)
        }


        GlobalScope.launch {
            var list: MutableList<Note>
            val job= launch {
                list=getNotesList()
                withContext(Dispatchers.Main){
                    notesViewModel.dbNotesList.value=list
                }
            }
            job.join()
            withContext(Dispatchers.Main){
                notesViewModel.dbNotesList.value?.let { adapter.setNotesList(it) }
            }
        }

        notesViewModel.dbNotesList.observe(viewLifecycleOwner, Observer{
            adapter.setNotesList(it)
        })
    }

    private suspend fun deleteNoteFromDb(noteToBeDeleted: Note) {
        appDb.noteDao().delete(noteToBeDeleted)
        var list: MutableList<Note>
        GlobalScope.launch {
            list = getNotesList()
            withContext(Dispatchers.Main){
                notesViewModel.dbNotesList.value = list
            }
        }
    }


    private fun getNotesList(): MutableList<Note> {
        return appDb.noteDao().getAll()
    }



}