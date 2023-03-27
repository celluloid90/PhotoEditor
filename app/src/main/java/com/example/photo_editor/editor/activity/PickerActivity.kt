package com.example.photo_editor.editor.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.example.photo_editor.databinding.ActivityPickerBinding
import com.example.photo_editor.editor.activit.EditActivity
import com.example.photo_editor.editor.view.CustomView


class PickerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPickerBinding
    private lateinit var customView: CustomView
    var SELECT_PICTURE = 200
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*binding.changeColor.setOnClickListener {
            binding.customView.swapColor()
            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()
        }*/

        binding.buttonLoadPicture.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, SELECT_PICTURE)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            imageUri = data?.data
            val intent = Intent(this, UpdateActivity::class.java)
            intent.putExtra("imageUri", imageUri.toString())
            startActivity(intent)
        }
    }
}