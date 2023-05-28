package com.bcl.android.collage_editor.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bcl.android.collage_editor.R
import com.bcl.android.collage_editor.customview.CollageTemplateView
import com.bcl.android.collage_editor.utils.Constants.EXPORT_COLLAGE_TEMPLATE
import com.bcl.android.collage_editor.utils.Utils
import com.braincraft.droid.filepicker.utils.Constant
import com.braincraftapps.mediaFetcher.model.MediaFile
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class EditHome : AppCompatActivity(), View.OnClickListener {
    lateinit var close: ImageView
    lateinit var exportBtn: ImageView
    lateinit var ratio: ImageView
    lateinit var adjust: ImageView
    lateinit var background: ImageView
    lateinit var gallery: ImageView
    lateinit var adjustDone: ImageView
    lateinit var adjustContainer: ConstraintLayout
    lateinit var ratioContainer: ConstraintLayout
    lateinit var ratioDone: ImageView
    var isAdjustViewOpen: Boolean = false
    var isRatioViewOpen: Boolean = false
    lateinit var zoomBar: SeekBar
    lateinit var edgeGapBar: SeekBar
    lateinit var edgeSmoothBar: SeekBar
    var zoomScaleValue: Float = 1f
    var edgeGapValue: Float = 0.01f
    private lateinit var collageTemplateView: CollageTemplateView
    private lateinit var mediaFiles: ArrayList<MediaFile>
    private var uriLists: ArrayList<Uri> = ArrayList()
    private lateinit var progressBar: ProgressBar
    private lateinit var ratioRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.No_Action_Bar_Theme)
        setContentView(R.layout.activity_edit_home)

        mediaFiles =
            intent.getParcelableArrayListExtra<MediaFile>(Constant.MEDIA_FILES) as ArrayList<MediaFile>

        for (mediaFile in mediaFiles) {
            uriLists.add(mediaFile.uri)
        }


        initAllViews()
        initAllListeners()
        collageTemplateView.setData(uriLists)
    }

    private fun initAllListeners() {
        close.setOnClickListener(this)
        ratio.setOnClickListener(this)
        adjust.setOnClickListener(this)
        background.setOnClickListener(this)
        gallery.setOnClickListener(this)
        adjustContainer.setOnClickListener(this)
        ratioContainer.setOnClickListener(this)
        adjustDone.setOnClickListener(this)
        exportBtn.setOnClickListener(this)
        ratioDone.setOnClickListener(this)

        zoomBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateScaleValue(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        edgeGapBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateEdgeGap(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        edgeSmoothBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateEdgeSmoothness(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun updateEdgeSmoothness(progress: Int) {
        var value: Float = (progress * 10).toFloat()
        collageTemplateView.updateEdgeSmooth(value)
    }

    private fun updateEdgeGap(progress: Int) {
        edgeGapValue = progress / 1000f
        collageTemplateView.updateFrameGap(edgeGapValue)
    }

    private fun updateScaleValue(progress: Int) {
        zoomScaleValue = (100f - progress) / 100f
        collageTemplateView.updateZoomScale(zoomScaleValue)
    }

    private fun initAllViews() {
        close = findViewById(R.id.collage_close)
        ratio = findViewById(R.id.ratio)
        adjust = findViewById(R.id.adjust)
        background = findViewById(R.id.background)
        gallery = findViewById(R.id.gallery)
        adjustContainer = findViewById(R.id.adjust_container)
        adjustDone = findViewById(R.id.adjust_done)
        ratioDone = findViewById(R.id.ratio_done)
        zoomBar = findViewById(R.id.zoom_seekbar)
        edgeGapBar = findViewById(R.id.edge_seekbar)
        edgeSmoothBar = findViewById(R.id.corner_seekbar)
        collageTemplateView = findViewById(R.id.img_background)
        exportBtn = findViewById(R.id.btn_next)
        progressBar = findViewById(R.id.progress_bar)
        ratioRecyclerView = findViewById(R.id.ratio_recycler_view)
        ratioContainer = findViewById(R.id.ratio_container)
    }

    override fun onClick(v: View?) {
        when (v) {
            close -> {
                finish()
            }

            ratio -> {
                openRatioView()
            }

            adjust -> {
                openAdjustView()
            }

            background -> {
                Toast.makeText(this, "Not Implemented!", Toast.LENGTH_SHORT).show()
            }

            gallery -> {
                Toast.makeText(this, "Not Implemented!", Toast.LENGTH_SHORT).show()
            }

            adjustContainer -> {
//                Empty click handled.
            }

            adjustDone -> {
                isAdjustViewOpen = false
                Utils.viewSlideDown(adjustContainer)
            }

            ratioDone -> {
                isRatioViewOpen = false
                Utils.viewSlideDown(ratioContainer)
            }

            exportBtn -> {
                if (isAdjustViewOpen) {
                    isAdjustViewOpen = false
                    Utils.viewSlideDown(adjustContainer)
                }
                progressBar.visibility = View.VISIBLE
                createOutputImage()
            }
        }
    }

    private fun createOutputImage() {
        var outStream: FileOutputStream? = null
        try {
            val bitmap = viewToBitmap(collageTemplateView)
            outStream = FileOutputStream(File(cacheDir, EXPORT_COLLAGE_TEMPLATE))
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 75, outStream)
            outStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        startActivity(Intent(this, AfterCollageActivity::class.java))
        progressBar.visibility = View.INVISIBLE
    }

    private fun viewToBitmap(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun openAdjustView() {
        if (!isAdjustViewOpen) {
            isAdjustViewOpen = true
            Utils.viewSlideUp(adjustContainer)
        } else {
            isAdjustViewOpen = false
            Utils.viewSlideDown(adjustContainer)
        }
    }

    private fun openRatioView() {
        if (!isRatioViewOpen) {
            isRatioViewOpen = true
            Utils.viewSlideUp(ratioContainer)
        } else {
            isRatioViewOpen = false
            Utils.viewSlideDown(ratioContainer)
        }
    }
}