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
                viewModel.note = viewModel.dbNotesList.value!![position]
                viewModel.notePosition = position
                parentFragmentManager.commit {
                    replace(R.id.notesFragment, NewNoteFragment())
                    addToBackStack(null)
                }
            }
        })

        val recyclerView = view.findViewById<RecyclerView>(R.id.notes_recyclerView)
        GlobalScope.launch {
            val job=launch {
                getNotesList()
            }
            job.join()
            withContext(Dispatchers.Main){
                viewModel.dbNotesList.value?.let { adapter.setNotesList(it) }

                recyclerView.adapter = adapter
                recyclerView.layoutManager = GridLayoutManager(context, 2)
            }
        }

        viewModel.dbNotesList.observe(viewLifecycleOwner, Observer{
            adapter.setNotesList(it)
            adapter.notifyDataSetChanged()
        })

    }

    private fun getNotesList() {
//        val contentResolver = (activity as AppCompatActivity).contentResolver!!
//        val result = contentResolver.query(NotesProvider.CONTENT_URI, arrayOf(NotesProvider.COLUMN_ID, NotesProvider.COLUMN_TITLE, NotesProvider.COLUMN_NOTE, NotesProvider.COLUMN_COLOR), null, null, NotesProvider.COLUMN_ID)
//        if(result!!.moveToNext()){
//            viewModel.dbNotesList = mutableListOf()
//            do{
//                val id = result.getInt(0)
//                val title = result.getString(1)
//                val content = result.getString(2)
//                val color = result.getString(3)
//                viewModel.dbNotesList.add(Note(id, title, content, color))
//            }while(result.moveToNext())
//        }else{
//            viewModel.dbNotesList = mutableListOf()
//        }
//        result.close()

//            viewModel.dbNotesList = appDb.noteDao().getAll() as MutableList<Note>


        viewModel.dbNotesList.postValue(appDb.noteDao().getAll() as MutableList<Note>)




    }

}