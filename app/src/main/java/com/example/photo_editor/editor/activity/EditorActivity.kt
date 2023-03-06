package com.example.photo_editor.editor.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.photo_editor.databinding.ActivityEditorBinding
import com.example.photo_editor.editor.view.CustomView

class EditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditorBinding
    private lateinit var customView: CustomView
    var SELECT_PICTURE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*binding.changeColor.setOnClickListener {
            binding.customView.swapColor()
            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()
        }*/

        binding.BSelectImage.setOnClickListener {
           imageChooser();
        }

    }

    private fun imageChooser() {

    }
}