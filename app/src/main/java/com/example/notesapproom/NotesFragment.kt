package com.example.notesapproom

// imports
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapproom.adapter.NotesListAdapter
import com.example.notesapproom.entity.Note
import com.example.notesapproom.interfaces.OnItemClickListener
import com.example.notesapproom.interfaces.OnNoteOptionsClickListener
import com.example.notesapproom.viewModel.ColorViewModel
import com.example.notesapproom.viewModel.DbViewModel
import com.example.notesapproom.viewModel.FavoriteNoteViewModel
import com.example.notesapproom.viewModel.NotesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesFragment : Fragment() {

    private var adapter = NotesListAdapter()
    private val notesViewModel: NotesViewModel by activityViewModels()
    private val dbViewModel: DbViewModel by activityViewModels()
    private val favoriteNoteViewModel: FavoriteNoteViewModel by activityViewModels()
    private val colorViewModel: ColorViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    private fun setActionBarProperties(color: String){
        (activity as AppCompatActivity).supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(Color.parseColor(color)))
            title = "Notes App"
            setDisplayHomeAsUpEnabled(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        when (requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                setActionBarProperties("#000000")
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                setActionBarProperties("#ffffff")
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                setActionBarProperties("#ffffff")
            }
        }



//        (activity as AppCompatActivity).supportActionBar?.apply {
//            setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffff")))
//            title = "Notes App"
//            setDisplayHomeAsUpEnabled(false)
//        }

        return inflater.inflate(R.layout.fragment_notes, container, false)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.note_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.favorites -> {
                parentFragmentManager.commit {
                    replace(R.id.notesFragment, FavoriteNoteFragment())
                    addToBackStack(null)
                    setTransition(TRANSIT_FRAGMENT_OPEN)
                }
            }
//            R.id.sort -> {
//                openSortOptions()
//            }
        }
        return super.onOptionsItemSelected(item)
    }

//    private fun openSortOptions() {
//        val dialog = BottomSheetDialog(requireContext())
//        val view = layoutInflater.inflate(R.layout.sort_note_bottom_sheet, null)
//        dialog.setCancelable(true)
//        dialog.setContentView(view)
//        dialog.show()
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteNoteViewModel.notePosition = -1

        val recyclerView = view.findViewById<RecyclerView>(R.id.notes_recyclerView)

        val fab = view.findViewById<FloatingActionButton>(R.id.create_note_fab)

        fab.setOnClickListener {
            notesViewModel.note = Note(0, "", "", colorViewModel.colors.random())
            notesViewModel.notePosition = -1
            parentFragmentManager.commit {
                replace(R.id.notesFragment, NewNoteFragment())
                addToBackStack(null)
                setTransition(TRANSIT_FRAGMENT_OPEN)
            }
        }

        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                notesViewModel.note = notesViewModel.dbNotesList.value!![position]
                notesViewModel.notePosition = position
                parentFragmentManager.commit {
                    replace(R.id.notesFragment, NewNoteFragment())
                    addToBackStack(null)
                    setTransition(TRANSIT_FRAGMENT_OPEN)
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

            override fun addOrRemoveFavorite(position: Int, isFavorite: Boolean) {
                val note = notesViewModel.dbNotesList.value!![position]
                if(isFavorite){
                    GlobalScope.launch {
                        dbViewModel.removeNoteFromFavoritesList(note.id!!)
                    }
                }else {
                        dbViewModel.addNoteToFavorite(note)
                }
            }

            override fun isFavorite(position: Int, onClicked: (Boolean) -> Unit) {
                val noteToBeChecked = notesViewModel.dbNotesList.value!![position]
                isFavoriteNote(noteToBeChecked, onClicked)
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
                list=dbViewModel.getNotesList()
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

    private fun isFavoriteNote(noteToBeChecked: Note, onClicked: (Boolean) -> Unit) {
        GlobalScope.launch {
            onClicked(dbViewModel.isFavoriteNoteAvailable(noteToBeChecked.id!!))

//            var favoriteResult: Boolean
//            val job = launch {
//                onClicked(dbViewModel.isFavoriteNoteAvailable(noteToBeChecked.id!!))
//            }
//            job.join()
//            withContext(Dispatchers.Main){
//                favoriteNoteViewModel.isFavorite = isFavorite
//            }
        }
    }

    private suspend fun deleteNoteFromDb(noteToBeDeleted: Note) {
        dbViewModel.removeNoteFromFavoritesList(noteToBeDeleted.id!!)
        dbViewModel.deleteNoteFromDB(noteToBeDeleted)
        var list: MutableList<Note>
        GlobalScope.launch {
            list = dbViewModel.getNotesList()
            withContext(Dispatchers.Main){
                notesViewModel.dbNotesList.value = list
            }
        }
    }

}