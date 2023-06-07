package com.bcl.android.collage_editor.activity

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bcl.android.collage_editor.R
import com.bcl.android.collage_editor.customview.CenteredBitmapImageView
import com.bcl.android.collage_editor.utils.ImageUtils
import com.braincraft.droid.filepicker.utils.Constant
import com.braincraftapps.mediaFetcher.model.MediaFile


class EditCustomHome : AppCompatActivity(), View.OnClickListener {
    lateinit var close: ImageView
    lateinit var reset: ImageView
    private lateinit var customView: CenteredBitmapImageView
    private lateinit var mediaFiles: ArrayList<MediaFile>
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.No_Action_Bar_Theme)
        setContentView(R.layout.activity_edit_custom_home)

        mediaFiles =
            intent.getParcelableArrayListExtra<MediaFile>(Constant.MEDIA_FILES) as ArrayList<MediaFile>

        uri = mediaFiles[0].uri

        initAllViews()
        initAllListeners()
        customView.setBitmap((ImageUtils.getResizedBitmap(this, uri, 720, 720)!!))
    }

    private fun initAllListeners() {
        close.setOnClickListener(this)
        reset.setOnClickListener(this)
    }

    private fun initAllViews() {
        close = findViewById(R.id.collage_close)
        customView = findViewById(R.id.img_background)
        reset = findViewById(R.id.btn_reset)
    }

    override fun onClick(v: View?) {
        when (v) {
            close -> {
                onBackPressed()
            }

            reset -> {
//                var bitmap = customView.getModifiedBitmap()
                customView.reset()
            }
        }
    }
}