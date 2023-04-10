package com.example.photo_editor.editor.activity

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photo_editor.R
import com.example.photo_editor.databinding.ActivityRatioBinding
import com.example.photo_editor.databinding.ActivityUpdateBinding
import com.example.photo_editor.editor.adapter.RatioAdapter
import com.example.photo_editor.editor.model.RatioModel
import com.example.photo_editor.editor.utils.RoateImage
import java.util.*

class RatioActivity : AppCompatActivity(), RatioAdapter.OnItemClickListener, View.OnClickListener {
    private var myUri: Uri? = null
    private lateinit var binding: ActivityRatioBinding
    private var bitmap: Bitmap? = null
    private val IMAGE_URI:String = "imageUri"
    lateinit var itemName: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerviewInitiate()
        val extras = intent.extras
        myUri = Uri.parse(extras!!.getString(IMAGE_URI))
        binding.ratioView.setImageUri(myUri);
        bitmap = RoateImage.getRotatedBitmap(this, myUri)

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
                    itemName[i], ratioImage?.getResourceId(i, 0)!!
                )
            )
        }
        val adapter = this?.let { RatioAdapter(it, ratioModelList, this) }
        binding.recyclerView.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        if (position == 0) {
            setLayoutHeightWidth(bitmap!!.width.toFloat(), bitmap!!.height.toFloat())
        }
        if (position == 1) {
            setLayoutHeightWidth(1f, 1f)
        }
        if (position == 2) {
            setLayoutHeightWidth(4f, 5f)
        }
        if (position == 3) {
            setLayoutHeightWidth(9f, 16f)
        }
        if (position == 4) {
            setLayoutHeightWidth(3f, 4f)
        }
        if (position == 5) {
            setLayoutHeightWidth(4f, 3f)
        }
        if (position == 6) {
            setLayoutHeightWidth(2f, 3f)
        }
        if (position == 7) {
            setLayoutHeightWidth(3f, 2f)

        }
        if (position == 8) {
            setLayoutHeightWidth(5f, 4f)
        }
        if (position == 9) {
            setLayoutHeightWidth(16f, 9f)
        }
    }

    override fun onClick(v: View?) {

    }

    private fun setLayoutHeightWidth(fl: Float, fl1: Float) {

        var getRatio = fl / fl1
        val pHeight = binding.rootView.height;
        val pWidth = binding.rootView.width;
        var hWidth = binding.ratioView.width
        var hHeight = binding.ratioView.height
        if (getRatio > 1) {
            hWidth = pWidth;
            hHeight = (hWidth / getRatio).toInt()
        }
        if (getRatio < 1) {
            hHeight = pHeight
            hWidth = (pHeight * getRatio).toInt()

        }
        if (getRatio == 1f) {
            hHeight = pWidth
            hWidth = pWidth
        }
        val params: ViewGroup.LayoutParams = binding.ratioView.layoutParams
        params.width = hWidth
        params.height = hHeight
        binding.ratioView.layoutParams = params


    }
}