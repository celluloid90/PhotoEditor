package com.example.segmentation.segmentation

import android.graphics.Bitmap
import kotlin.math.min

/**
 * @author: Raihan Uddin Piash (raihan.uddin@braincraftapps.com)
 * @project: StickerMaker
 * @date: On 10/12/23 at 11:57 AM
 */
fun scaleIfNeeded(bitmap: Bitmap, maxImageSize: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val min = min(width, height)
    if (min > maxImageSize) {
        val widthScaleFactor = maxImageSize / width.toFloat()
        val heightScaleFactor = maxImageSize / height.toFloat()
        val scaleFactor = if (widthScaleFactor > heightScaleFactor) {
            widthScaleFactor
        } else heightScaleFactor
        val newWidth = (width * scaleFactor).toInt()
        val newHeight = (height * scaleFactor).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    return bitmap
}