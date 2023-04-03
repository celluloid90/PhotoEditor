package com.example.photo_editor.editor.utils

import android.content.Context
import android.graphics.Bitmap
import com.hoko.blur.HokoBlur

class BlurBitmap {
    companion object{
        fun blurBitmap(bitmap: Bitmap,context: Context):Bitmap{
           val blurBitmap = HokoBlur.with(context)
                .radius(60) //blur radius，max=25，default=5
                .sampleFactor(3.0f) //scale factor，if factor=2，the width and height of a bitmap will be scale to 1/2 sizes，default=5
                .forceCopy(false) //If scale factor=1.0f，the origin bitmap will be modified. You could set forceCopy=true to avoid it. default=false
                .needUpscale(true) //After blurring，the bitmap will be upscaled to origin sizes，default=true
                .processor() //build a blur processor
                .blur(bitmap);
            return blurBitmap;
        }
    }
}