package com.bcl.android.collage_editor.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bcl.android.collage_editor.R
import com.bcl.android.collage_editor.utils.Constants.EXPORT_COLLAGE_TEMPLATE
import java.io.File

class AfterCollageActivity : AppCompatActivity() {

    private lateinit var exportedImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.No_Action_Bar_Theme)
        setContentView(R.layout.activity_after_collage)

        exportedImageView = findViewById(R.id.exported_view)

        val bitmapPath = this.cacheDir.absolutePath + File.separator + EXPORT_COLLAGE_TEMPLATE
        exportedImageView.setImageBitmap(BitmapFactory.decodeFile(bitmapPath))
    }
}