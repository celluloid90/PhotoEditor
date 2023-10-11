package com.example.photo_editor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.photo_editor.databinding.ActivityHomeBinding
import com.example.photo_editor.editor.activity.PickerActivity
import com.example.photo_editor.editor.activity.PlayerActivity
import com.example.photo_editor.editor.activity.playerview.MainActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editor.setOnClickListener {
           startActivity(Intent(this, PickerActivity::class.java))
        }
        binding.collage.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))

        }
    }
}