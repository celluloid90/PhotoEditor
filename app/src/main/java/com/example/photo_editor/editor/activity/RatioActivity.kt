package com.example.photo_editor.editor.activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photo_editor.R
import com.example.photo_editor.databinding.ActivityRatioBinding
import com.example.photo_editor.editor.adapter.ColorAdapter
import com.example.photo_editor.editor.adapter.RatioAdapter
import com.example.photo_editor.editor.enums.BorderType
import com.example.photo_editor.editor.enums.FLIPTYPE
import com.example.photo_editor.editor.model.ColorModel
import com.example.photo_editor.editor.model.DataModel
import com.example.photo_editor.editor.model.RatioModel
import com.example.photo_editor.editor.enums.BackgroundType
import com.example.photo_editor.editor.utils.CheckButtonType
import com.example.photo_editor.editor.utils.RoateImage
import java.util.*
import kotlin.collections.ArrayList

class RatioActivity : AppCompatActivity(), RatioAdapter.OnItemClickListener, View.OnClickListener,
    ColorAdapter.OnColorItemClickListener {
    var count = 1;
    private var myUri: Uri? = null
    private lateinit var binding: ActivityRatioBinding
    private var bitmap: Bitmap? = null
    private val IMAGE_URI: String = "imageUri"
    private var dataModel: DataModel? = null
    var SELECT_PICTURE = 200
    private var imageUri: Uri? = null
    lateinit var itemName: ArrayList<String>
    lateinit var itemColor: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerviewInitiate()
        colorRecyclerviewInit()
        val extras = intent.extras
        myUri = Uri.parse(extras!!.getString(IMAGE_URI))
        binding.ratioView.setImageUri(myUri);
        bitmap = RoateImage.getRotatedBitmap(this, myUri)
        initClickListener()

        binding.ratioView.checkClickedButtonType(CheckButtonType.CENTER)
        binding.ratioView.setBackBround(BackgroundType.BLUR)
        binding.ratioView.setBorder(BorderType.NONE);

    }

    private fun initClickListener() {

        binding.left.setOnClickListener(this)
        binding.right.setOnClickListener(this)
        binding.center.setOnClickListener(this)
        binding.white.setOnClickListener(this)
        binding.black.setOnClickListener(this)
        binding.blur.setOnClickListener(this)
        binding.gallery.setOnClickListener(this)
        binding.gradient.setOnClickListener(this)
        binding.color.setOnClickListener(this)
        binding.crop.setOnClickListener(this)
        binding.ratio.setOnClickListener(this)
        binding.border.setOnClickListener(this)
        binding.rotate.setOnClickListener(this)
        binding.verticalFlip.setOnClickListener(this)
        binding.horizontalFlip.setOnClickListener(this)

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

    private fun colorRecyclerviewInit() {
        itemColor = ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.color_name)))
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.colorRecyclerView.layoutManager = layoutManager

        val colorModelList = ArrayList<ColorModel>()
        for (i in 0 until itemColor.size) {
            colorModelList.add(
                ColorModel(
                    itemColor[i]
                )
            )
        }
        val adapter = this.let { ColorAdapter(it, colorModelList, this) }
        binding.colorRecyclerView.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        if (position == 0) {
            binding.ratioView.setOriginalRatio();
            setLayoutHeightWidth(bitmap!!.width.toFloat(), bitmap!!.height.toFloat())
            binding.ratioView.checkClickedButtonType(CheckButtonType.CENTER)
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
        if (v == binding.left) {
            binding.ratioView.checkClickedButtonType(CheckButtonType.LEFT)
        } else if (v == binding.center) {
            binding.ratioView.checkClickedButtonType(CheckButtonType.CENTER)
        } else if (v == binding.right) {
            binding.ratioView.checkClickedButtonType(CheckButtonType.RIGHT)
        } else if (v == binding.white) {
            binding.ratioView.setBackBround(BackgroundType.WHITE)
        } else if (v == binding.black) {
            binding.ratioView.setBackBround(BackgroundType.BLACK)
        } else if (v == binding.blur) {
            binding.ratioView.setBackBround(BackgroundType.BLUR)
        } else if (v == binding.gallery) {
            binding.ratioView.setBackBround(BackgroundType.PHOTO)
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, SELECT_PICTURE)
        } else if (v == binding.gradient) {
            binding.ratioView.setBackBround(BackgroundType.GRADIENT)

        } else if (v == binding.color) {
            binding.colorView.visibility = View.VISIBLE
            binding.backgroundView.visibility = View.INVISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
        } else if (v == binding.crop) {

        } else if (v == binding.ratio) {

        } else if (v == binding.border) {

            if (count == 0) {
                binding.ratioView.setBorder(BorderType.NONE);
                count = 1;
            } else if (count == 1) {
                binding.ratioView.setBorder(BorderType.WHITE);
                count = 2;
            } else if (count == 2) {
                binding.ratioView.setBorder(BorderType.BLACK);
                count = 3;
            } else if (count == 3) {
                binding.ratioView.setBorder(BorderType.COLOR);
                count = 0
            }
        } else if (v == binding.rotate) {
            binding.ratioView.setRotation()
        } else if (v == binding.verticalFlip) {
            binding.ratioView.setFlipType(FLIPTYPE.VERTICAL_FLIP)

        } else if (v == binding.horizontalFlip) {
            binding.ratioView.setFlipType(FLIPTYPE.HORIZONTAL_FLIP)

        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            imageUri = data?.data
            binding.ratioView.setgellaryUri(imageUri)
        }
    }

    override fun onColorItemClick(position: Int) {

        if (position == 0) {
            binding.ratioView.setBackBround(BackgroundType.DROPPER)
        } else {
            binding.ratioView.setBackBround(BackgroundType.COLOR)
            binding.ratioView.setColor(itemColor[position].toString())
        }
    }
}