package com.example.notesapproom

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapproom.adapter.FavoriteNotesListAdapter
import com.example.notesapproom.entity.FavoriteNote
import com.example.notesapproom.entity.Note
import com.example.notesapproom.interfaces.OnFavoriteNoteClickListener
import com.example.notesapproom.interfaces.OnItemClickListener
import com.example.notesapproom.viewModel.DbViewModel
import com.example.notesapproom.viewModel.FavoriteNoteViewModel
import com.example.notesapproom.viewModel.NotesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteNoteFragment : Fragment() {

    private var adapter = FavoriteNotesListAdapter()
    private val dbViewModel: DbViewModel by activityViewModels()
    private val notesViewModel: NotesViewModel by activityViewModels()
    private val favoriteNoteViewModel: FavoriteNoteViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.apply {
            title = "Favorites"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite_note, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                parentFragmentManager.commit {
                    replace(R.id.favorite_note_fragment, NotesFragment())
                    addToBackStack(null)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffff")))
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.favoriteNotes_recyclerView)


        // note item click
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
//                favoriteNoteViewModel.note = favoriteNoteViewModel.dbNotesList.value!![position]
//                favoriteNoteViewModel.note = dbViewModel.getFavoriteNote(favoriteNoteViewModel.dbFavoriteNoteList[position].noteId!!)
                favoriteNoteViewModel.note = favoriteNoteViewModel.favoriteNotes.value!![position]
                favoriteNoteViewModel.notePosition = position
                parentFragmentManager.commit {
                    replace(R.id.favorite_note_fragment, NewNoteFragment())
                    addToBackStack(null)
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                }
            }
        })

        adapter.setOnFavoriteNoteOptionsClickListener(object : OnFavoriteNoteClickListener{
            override fun removeFromFavorite(position: Int) {
//                val noteToBeRemoved = favoriteNoteViewModel.dbFavoriteNoteList[position]
                val noteToBeRemoved = favoriteNoteViewModel.favoriteNotes.value!![position]
                val n = favoriteNoteViewModel.dbFavoriteNoteList[position]

                GlobalScope.launch {
//                    dbViewModel.removeNoteFromFavorite(dbViewModel.getFavoriteNote(noteToBeRemoved.noteId!!))
                    removeNoteFromFavorites(n)
                }
            }
        })

        recyclerView.adapter = adapter

        if(activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.layoutManager = GridLayoutManager(context, 2)
        }else{
            recyclerView.layoutManager = GridLayoutManager(context, 4)
        }

//        GlobalScope.launch{
//            favoriteNoteViewModel.dbFavoriteNoteList = dbViewModel.getfavoriteNotesIdList()
//        }

        GlobalScope.launch {
            var fList: MutableList<FavoriteNote>
            val firstJob = launch {
                fList = dbViewModel.getfavoriteNotesIdList()
                withContext(Dispatchers.Main){
                    favoriteNoteViewModel.dbFavoriteNoteList = fList
                }
            }
            firstJob.join()
            var list: MutableList<Note>
            val job= launch {
                list=dbViewModel.getFavoriteNotes(favoriteNoteViewModel.dbFavoriteNoteList)
                withContext(Dispatchers.Main){
                    favoriteNoteViewModel.favoriteNotes.value= list
                }
            }
            job.join()
            withContext(Dispatchers.Main){
                favoriteNoteViewModel.favoriteNotes.value!!.let { adapter.setNotesList(it) }
            }
        }

        favoriteNoteViewModel.favoriteNotes.observe(viewLifecycleOwner, Observer{
            adapter.setNotesList(it)
        })
    }

    private suspend fun removeNoteFromFavorites(n: FavoriteNote) {
        dbViewModel.removeNoteFromFavorite(n)
        GlobalScope.launch {
            var fList: MutableList<FavoriteNote>
            val firstJob = launch {
                fList = dbViewModel.getfavoriteNotesIdList()
                withContext(Dispatchers.Main){
                    favoriteNoteViewModel.dbFavoriteNoteList = fList
                }
            }
            firstJob.join()
            var list: MutableList<Note>
            val job = launch {
                list=dbViewModel.getFavoriteNotes(favoriteNoteViewModel.dbFavoriteNoteList)
                withContext(Dispatchers.Main){
                    favoriteNoteViewModel.favoriteNotes.value= list
                }
            }
            job.join()
            withContext(Dispatchers.Main){
                favoriteNoteViewModel.favoriteNotes.value!!.let { adapter.setNotesList(it) }
            }
        }
    }

}