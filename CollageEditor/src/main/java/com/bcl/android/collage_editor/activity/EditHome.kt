package com.bcl.android.collage_editor.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bcl.android.collage_editor.R
import com.bcl.android.collage_editor.customview.CustomView
import com.bcl.android.collage_editor.utils.Utils
import com.braincraft.droid.filepicker.utils.Constant
import com.braincraftapps.mediaFetcher.model.MediaFile

class EditHome : AppCompatActivity(), View.OnClickListener {
    lateinit var close: ImageView
    lateinit var ratio: ImageView
    lateinit var adjust: ImageView
    lateinit var background: ImageView
    lateinit var gallery: ImageView
    lateinit var adjustDone: ImageView
    lateinit var adjustContainer: ConstraintLayout
    var isAdjustViewOpen: Boolean = false
    lateinit var zoomBar: SeekBar
    lateinit var edgeGapBar: SeekBar
    lateinit var edgeSmoothBar: SeekBar
    var zoomScaleValue: Float = 1f
    var edgeGapValue: Float = 0.01f
    lateinit var customView: CustomView
    private lateinit var mediaFiles: ArrayList<MediaFile>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.No_Action_Bar_Theme)
        setContentView(R.layout.activity_edit_home)

        mediaFiles =
            intent.getParcelableArrayListExtra<MediaFile>(Constant.MEDIA_FILES) as ArrayList<MediaFile>

        initAllViews()
        initAllListeners()
    }

    private fun initAllListeners() {
        close.setOnClickListener(this)
        ratio.setOnClickListener(this)
        adjust.setOnClickListener(this)
        background.setOnClickListener(this)
        gallery.setOnClickListener(this)
        adjustContainer.setOnClickListener(this)
        adjustDone.setOnClickListener(this)

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
        customView.updateEdgeSmooth(value)
    }

    private fun updateEdgeGap(progress: Int) {
        edgeGapValue = progress / 1000f
        customView.updateFrameGap(edgeGapValue)
    }

    private fun updateScaleValue(progress: Int) {
        zoomScaleValue = (100f - progress) / 100f
        customView.updateZoomScale(zoomScaleValue)
    }

    private fun initAllViews() {
        close = findViewById(R.id.collage_close)
        ratio = findViewById(R.id.ratio)
        adjust = findViewById(R.id.adjust)
        background = findViewById(R.id.background)
        gallery = findViewById(R.id.gallery)
        adjustContainer = findViewById(R.id.adjust_container)
        adjustDone = findViewById(R.id.adjust_done)
        zoomBar = findViewById(R.id.zoom_seekbar)
        edgeGapBar = findViewById(R.id.edge_seekbar)
        edgeSmoothBar = findViewById(R.id.corner_seekbar)
        customView = findViewById(R.id.img_background)
    }

    override fun onClick(v: View?) {
        when (v) {
            close -> {
                finish()
            }
            ratio -> {
                Toast.makeText(this, "Not Implemented!", Toast.LENGTH_SHORT).show()
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
        }
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
}