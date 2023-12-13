/*
 * Copyright 2022 - 2023 Brain Craft Ltd - All Rights Reserved
 *
 * This file is a part of BgRemover.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * @file: SegmentationManager.kt
 * @author: shakib@braincraftapps.com
 * @modified: Nov 08, 2023, 04:33 PM
 */

package com.braincraftapps.droid.segmentation

import android.content.Context
import android.graphics.Bitmap
import com.braincraftapps.droid.segmentation.data.SegmentationOutput
import com.braincraftapps.droid.segmentation.processor.local.BundledObjectProcessor

class SegmentationManager(val applicationContext: Context) {
    companion object {
        @Volatile
        private var instance: SegmentationManager? = null

        fun getInstance(context: Context): SegmentationManager {
            return instance ?: synchronized(SegmentationManager::class) {
                val manager = SegmentationManager(context.applicationContext)
                instance = manager
                return@synchronized manager
            }
        }
    }

    fun processWithAi(input: Bitmap): SegmentationOutput? {
        try {
            val localProcessor = BundledObjectProcessor(applicationContext)
            return localProcessor.process(input)
        } catch (exception: Exception) {
            exception.printStackTrace()/*logWarning(
                throwable = exception,
                comment = "Something went wrong while processing image with bundled object processor!",
                tag = LogTags.AI_IMAGE_ERASER
            )*/
        }
        return null
    }
}
