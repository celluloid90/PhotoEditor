package com.example.photo_editor.editor.activit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photo_editor.R
import com.example.photo_editor.databinding.ActivityEditBinding
import com.example.photo_editor.editor.adapter.RatioAdapter
import com.example.photo_editor.editor.model.RatioModel
import com.example.photo_editor.editor.utils.RoateImage
import com.hoko.blur.HokoBlur
import java.util.*


class EditActivity : AppCompatActivity(), RatioAdapter.OnItemClickListener {

    private var myUri: Uri? = null
    private var imageView: ImageView? = null
    lateinit var itemName: ArrayList<String>
    lateinit var gifItemDescription: ArrayList<String>
    private var bitmap: Bitmap? = null
    private var ratio: Float? = null
    private var mBitmap:Bitmap? = null
    private var myBitmap: Bitmap?= null

    private lateinit var binding: ActivityEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        myUri = Uri.parse(extras!!.getString("imageUri"))

       // bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, myUri)
        bitmap = RoateImage.getRotatedBitmap(this,myUri)
       // bitmap = RoateImage.getResizedBitmap(bitmap,1080,1080)



        // binding.customView.setPicture(bitmap)

        /*  val drawable: Drawable = BitmapDrawable(resources, bitmap)
          binding.hoverView.background = drawable*/

        binding.hoverView.setPicture(bitmap)

        val outBitmap: Bitmap = HokoBlur.with(this)
            .radius(60) //blur radius，max=25，default=5
            .sampleFactor(3.0f) //scale factor，if factor=2，the width and height of a bitmap will be scale to 1/2 sizes，default=5
            .forceCopy(false) //If scale factor=1.0f，the origin bitmap will be modified. You could set forceCopy=true to avoid it. default=false
            .needUpscale(true) //After blurring，the bitmap will be upscaled to origin sizes，default=true
            .processor() //build a blur processor
            .blur(bitmap);

        binding.hoverView.setBackgroundPicture(outBitmap)


        recyclerviewInitiate()


    }

    override fun onItemClick(position: Int) {
        if (position == 0) {
            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()
            val hHeight = binding.customLayoutView.height
            val hWeight = binding.customLayoutView.width
            val params: ViewGroup.LayoutParams = binding.hoverView.layoutParams
            params.width = hWeight
            params.height = hHeight
            binding.hoverView.layoutParams = params
        }
        if (position == 1) {

            setLayoutHeightWidth(1f, 1f)

        }
        if (position == 2) {
            // setLayoutParam("H,4:5", .8f)
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

    private fun setLayoutHeightWidth(fl: Float, fl1: Float) {
        var getRatio = fl / fl1.toFloat()
        Log.d("TAG", "ratio: " + getRatio)
        val pHeight = binding.customLayoutView.height;
        val pWidth = binding.customLayoutView.width;
        var hWidth = binding.hoverView.width
        var hHeight = binding.hoverView.height
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
        val params: ViewGroup.LayoutParams = binding.hoverView.layoutParams
        params.width = hWidth
        params.height = hHeight
        binding.hoverView.layoutParams = params

    }


    /*  private fun setLayoutParam(s: String, float: Float) {
          val pWidth = binding.customLayoutView.width
          val pHeight = binding.customLayoutView.height
          var hWidth = binding.hoverView.width
          var hHeight = binding.hoverView.height
          if (float > 1) {
              hWidth = pWidth;
              hHeight = (hWidth / float).toInt()
          }
          if (float < 1) {
              hHeight = pHeight
              hWidth = (pHeight * float).toInt()
          }
          if (float == 1f) {
              hHeight = pHeight
              hWidth = pHeight
          }
          val params: ViewGroup.LayoutParams = binding.hoverView.layoutParams
          params.width = hWidth
          params.height = hHeight
          binding.hoverView.layoutParams = params

      }*/

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
}