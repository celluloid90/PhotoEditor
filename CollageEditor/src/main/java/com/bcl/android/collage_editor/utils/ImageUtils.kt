package com.bcl.android.collage_editor.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Region
import android.net.Uri
import android.os.Build
import androidx.exifinterface.media.ExifInterface
import java.io.IOException
import kotlin.math.max

/**
 * Created by Raihan Uddin Piash on ২৭/৩/২৩

 * Copyright (c) 2023 Brain Craft LTD.
 **/
object ImageUtils {
    @Throws(IOException::class)
    fun rotateImageIfRequired(context: Context, img: Bitmap, selectedImage: Uri): Bitmap? {
        val input = context.contentResolver.openInputStream(selectedImage)
        val ei: ExifInterface = if (Build.VERSION.SDK_INT > 23) ExifInterface(input!!) else ExifInterface(selectedImage.path!!)
        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
            else -> img
        }
    }

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

    fun createMatrixToDrawImageInCenterView(
        region: Region, imageWidth: Float, imageHeight: Float
    ): Matrix {

        val ratioWidth = region.bounds.width() / imageWidth
        val ratioHeight = region.bounds.height() / imageHeight
        val ratio = max(ratioWidth, ratioHeight)
        val dx = (region.bounds.width() - (imageWidth * ratio)) / 2.0f
        val dy = (region.bounds.height() - (imageHeight * ratio)) / 2.0f
        val result = Matrix()
        result.postScale(ratio, ratio)
        result.postTranslate(region.bounds.left.toFloat() + dx, region.bounds.top.toFloat() + dy)
        return result
    }
}