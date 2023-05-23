package com.example.photo_editor.editor.activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photo_editor.R
import com.example.photo_editor.databinding.ActivityUpdateBinding
import com.example.photo_editor.editor.adapter.RatioAdapter
import com.example.photo_editor.editor.enums.BorderType
import com.example.photo_editor.editor.enums.CanvasBackgroundType
import com.example.photo_editor.editor.model.RatioModel
import com.example.photo_editor.editor.utils.CanvasRatioValue
import com.example.photo_editor.editor.utils.CheckButtonType
import com.example.photo_editor.editor.utils.RoateImage
import java.util.Arrays

class UpdateActivity : AppCompatActivity(), OnClickListener, RatioAdapter.OnItemClickListener,
    OnSeekBarChangeListener {

    private lateinit var binding: ActivityUpdateBinding
    lateinit var itemName: ArrayList<String>
    lateinit var itemColor: ArrayList<String>
    private var bitmap: Bitmap? = null
    private var myUri: Uri? = null
    private var imageUri: Uri? = null
    private val IMAGE_URI: String = "imageUri"
    var SELECT_PICTURE = 200
    var selected = false;
    var count = 1;

    //  lateinit var canvasBackgroundType: CanvasBackgroundType
    val canvasBackgroundType = CanvasBackgroundType.BLUR

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initClickListener()
        recyclerviewInitiate()

        val extras = intent.extras
        myUri = Uri.parse(extras!!.getString(IMAGE_URI))
        binding.editView.setImageUri(myUri)
        bitmap = RoateImage.getRotatedBitmap(this, myUri)

        binding.editView.checkClickedButtonType(CheckButtonType.CENTER)
        binding.editView.setBackgroundType(CanvasBackgroundType.BLUR)
        binding.editView.setBorder(BorderType.NONE);

    }

    private fun initClickListener() {
        binding.crop.setOnClickListener(this)
        binding.ratio.setOnClickListener(this)
        binding.border.setOnClickListener(this)
        binding.rotate.setOnClickListener(this)
        binding.verticalFlip.setOnClickListener(this)
        binding.horizontalFlip.setOnClickListener(this)
        binding.white.setOnClickListener(this)
        binding.black.setOnClickListener(this)
        binding.blur.setOnClickListener(this)
        binding.gallery.setOnClickListener(this)
        binding.gradient.setOnClickListener(this)
        binding.color.setOnClickListener(this)
        binding.bottomView.rightSideImageView.setOnClickListener(this)
        binding.left.setOnClickListener(this)
        binding.center.setOnClickListener(this)
        binding.right.setOnClickListener(this)
        binding.seekbar.setOnSeekBarChangeListener(this)

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
        val adapter = this.let { RatioAdapter(it, ratioModelList, this) }
        binding.recyclerView.adapter = adapter
    }


    override fun onClick(v: View?) {


        if (v == binding.left) {
            binding.editView.checkClickedButtonType(CheckButtonType.LEFT)
        } else if (v == binding.center) {
            binding.editView.checkClickedButtonType(CheckButtonType.CENTER)
        } else if (v == binding.right) {
            binding.editView.checkClickedButtonType(CheckButtonType.RIGHT)
        } else if (v == binding.crop) {
            Toast.makeText(this, "Crop", Toast.LENGTH_SHORT).show()
        } else if (v == binding.ratio) {
            binding.ratioLayout.visibility = View.VISIBLE
            binding.linearLayout.visibility = View.INVISIBLE
            binding.recyclerView.visibility = View.VISIBLE
            binding.seekbarLayout.visibility = View.INVISIBLE
            binding.alignLayout.visibility = View.VISIBLE
            binding.bottomView.centerText.text = "Ratio"
        } else if (v == binding.border) {
            if (count == 0) {
                binding.editView.setBorder(BorderType.NONE);
                count = 1;
            } else if (count == 1) {
                binding.editView.setBorder(BorderType.WHITE);
                count = 2;
            } else if (count == 2) {
                binding.editView.setBorder(BorderType.BLACK);
                count = 3;
            } else if (count == 3) {
                binding.editView.setBorder(BorderType.COLOR);
                count = 0
            }
        } else if (v == binding.rotate) {
            Toast.makeText(this, "rotate", Toast.LENGTH_SHORT).show()
        } else if (v == binding.verticalFlip) {
            Toast.makeText(this, "verticalFlip", Toast.LENGTH_SHORT).show()
        } else if (v == binding.horizontalFlip) {
            Toast.makeText(this, "horizontalFlip", Toast.LENGTH_SHORT).show()
        } else if (v == binding.white) {
            binding.editView.setBackgroundType(CanvasBackgroundType.WHITE)
        } else if (v == binding.black) {
            binding.editView.setBackgroundType(CanvasBackgroundType.BLACK)
        } else if (v == binding.blur) {

            if (!selected) {
                binding.editView.setBackgroundType(CanvasBackgroundType.BLUR)
                selected = true;
            } else if (selected) {

                binding.ratioLayout.visibility = View.VISIBLE
                binding.linearLayout.visibility = View.INVISIBLE
                binding.seekbarLayout.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.INVISIBLE
                binding.bottomView.centerText.text = "Blur"
            }

        } else if (v == binding.gradient) {
            binding.editView.setBackgroundType(CanvasBackgroundType.GRADIENT)
        } else if (v == binding.gallery) {
            binding.editView.setBackgroundType(CanvasBackgroundType.PHOTO)
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, SELECT_PICTURE)
        } else if (v == binding.color) {
            binding.editView.setBackgroundType(CanvasBackgroundType.COLOR)
        } else if (v == binding.bottomView.rightSideImageView) {
            binding.ratioLayout.visibility = View.INVISIBLE
            binding.linearLayout.visibility = View.VISIBLE
            binding.alignLayout.visibility = View.GONE
            selected = false;
        }

    }

    override fun onItemClick(position: Int) {
        if (position == 0) {
            CanvasRatioValue.setWidthHeight(
                binding.editView, binding.parentView,
                bitmap!!.width.toFloat(), bitmap!!.height.toFloat()
            )
            binding.editView.checkClickedButtonType(CheckButtonType.CENTER)
        }
        CanvasRatioValue.setLayoutHeightWidth(
            binding.editView,
            position,
            binding.parentView
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            imageUri = data?.data
            binding.editView.setgellaryUri(imageUri)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        Log.d("TAG", "onProgressChanged: " + progress)
        binding.editView.setBlurProgressValue(progress)
        binding.seekBarvalue.text = (progress).toString();

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }
}