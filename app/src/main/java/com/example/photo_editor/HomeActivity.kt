package com.example.photo_editor

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bcl.android.collage_editor.activity.CollagePicker
import com.example.photo_editor.databinding.ActivityHomeBinding
import com.example.photo_editor.editor.activity.EditorActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editor.setOnClickListener {
            startActivity(Intent(this, EditorActivity::class.java))
        }
        binding.collage.setOnClickListener {
            startActivity(Intent(this, CollagePicker::class.java))
        }
    }
}