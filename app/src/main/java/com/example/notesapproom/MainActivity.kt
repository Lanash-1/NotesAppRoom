package com.example.notesapproom

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.commit {
            replace(R.id.notesFragment, NotesFragment())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffff")))
            title = "Notes App"
        }

    }

}