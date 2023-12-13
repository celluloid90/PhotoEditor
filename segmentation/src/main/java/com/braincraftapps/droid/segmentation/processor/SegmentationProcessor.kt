/*
 * Copyright 2022 - 2023 Brain Craft Ltd - All Rights Reserved
 *
 * This file is a part of BgRemover.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * @file: SegmentationProcessor.kt
 * @author: shakib@braincraftapps.com
 * @modified: Nov 08, 2023, 04:33 PM
 */

package com.braincraftapps.droid.segmentation.processor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import com.braincraftapps.droid.segmentation.data.SegmentationOutput

abstract class SegmentationProcessor(val applicationContext: Context) {

    fun process(input: Bitmap): SegmentationOutput? {
        return onProcess(input)
    }

    protected fun Bitmap.applyMask(mask: Bitmap): Bitmap {
        val canvas = Canvas(this)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            isFilterBitmap = true
            xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
        }
        canvas.drawBitmap(mask, 0F, 0F, paint)
        return this
    }

    protected abstract fun onProcess(input: Bitmap): SegmentationOutput?
}
