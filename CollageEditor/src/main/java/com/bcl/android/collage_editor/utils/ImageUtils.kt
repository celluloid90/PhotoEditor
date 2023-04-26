package com.bcl.android.collage_editor.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
        val ei: ExifInterface =
            if (Build.VERSION.SDK_INT > 23) ExifInterface(input!!) else ExifInterface(selectedImage.path!!)
        return when (ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )) {
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

    @Throws(IOException::class)
    fun getResizedBitmap(
        context: Context,
        selectedImage: Uri?,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap? {
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var imageStream = context.contentResolver.openInputStream(selectedImage!!)
        BitmapFactory.decodeStream(imageStream, null, options)
        imageStream!!.close()

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        imageStream = context.contentResolver.openInputStream(selectedImage)
        var img = BitmapFactory.decodeStream(imageStream, null, options)
        img = rotateImageIfRequired(context, img!!, selectedImage)
        return img
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = Math.min(heightRatio, widthRatio)

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).
            val totalPixels = (width * height).toFloat()

            // Anything more than 2x the requested pixels we'll sample down further
            val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
        }
        return inSampleSize
    }

}