/*
 * Copyright 2022 - 2023 Brain Craft Ltd - All Rights Reserved
 *
 * This file is a part of BgRemover.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * @file: Output.kt
 * @author: shakib@braincraftapps.com
 * @modified: Jun 20, 2023, 01:32 PM
 */

package com.example.segmentation.ai.eraser.data

import android.graphics.Bitmap
import com.example.segmentation.ai.model.data.ModelFile

data class Output(
    val bitmap: Bitmap, val modelType: ModelFile.Type
)
