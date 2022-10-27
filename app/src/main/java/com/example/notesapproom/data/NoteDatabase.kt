package com.example.notesapproom.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notesapproom.dao.FavoriteNoteDao
import com.example.notesapproom.dao.NoteDao
import com.example.notesapproom.entity.FavoriteNote
import com.example.notesapproom.entity.Note

@Database(entities = [Note::class, FavoriteNote::class], version = 1, exportSchema = false)
abstract class NoteDatabase: RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun favoriteNoteDao(): FavoriteNoteDao

    companion object{
        @Volatile
        private var INSTANCE : NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            // synchronized - can be accessed from only one thread
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}