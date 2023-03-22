package com.example.photo_editor.editor.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photo_editor.R
import com.example.photo_editor.databinding.ActivityEditBinding
import com.example.photo_editor.databinding.ActivityUpdateBinding
import com.example.photo_editor.editor.adapter.RatioAdapter
import com.example.photo_editor.editor.model.RatioModel
import java.util.*

class UpdateActivity : AppCompatActivity(), RatioAdapter.OnItemClickListener {

    private lateinit var binding: ActivityUpdateBinding
    lateinit var itemName: ArrayList<String>
    lateinit var gifItemDescription: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerviewInitiate()
    }

    private fun recyclerviewInitiate() {
        itemName = ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.rationame)))

        val ratioImage = resources?.obtainTypedArray(R.array.ratio_icons)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerView.layoutManager = layoutManager

        val ratioModelList = ArrayList<RatioModel>()
        for (i in 0 until itemName.size) {
            ratioModelList.add(
                RatioModel(
                    itemName[i],
                    ratioImage?.getResourceId(i, 0)!!
                )
            )
        }
        val adapter = this?.let { RatioAdapter(it, ratioModelList, this) }
        binding.recyclerView.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        if (position == 0) {
            binding.editorView.setRatio(0f, 0f)
        } else if (position == 1){
            binding.editorView.setRatio(1f, 1f)
        }

        if (position == 2) {
            // setLayoutParam("H,4:5", .8f)
            binding.editorView.setRatio(4f, 5f)
        }
        if (position == 3) {
            binding.editorView.setRatio(9f, 16f)
        }
        if (position == 4) {
            binding.editorView.setRatio(3f, 4f)
        }
        if (position == 5) {
            binding.editorView.setRatio(4f, 3f)
        }
        if (position == 6) {
            binding.editorView.setRatio(2f, 3f)
        }
        if (position == 7) {
            binding.editorView.setRatio(3f, 2f)

        }
        if (position == 8) {
            binding.editorView.setRatio(5f, 4f)
        }
        if (position == 9) {
            binding.editorView.setRatio(16f, 9f)
        }
    }
}