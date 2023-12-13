/*
 * Copyright 2022 - 2023 Brain Craft Ltd - All Rights Reserved
 *
 * This file is a part of BgRemover.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * @file: SegmentationOutput.kt
 * @author: shakib@braincraftapps.com
 * @modified: Nov 08, 2023, 04:33 PM
 */

package com.braincraftapps.droid.segmentation.data

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class SegmentationOutput(
    val type: Type,
    val original: Bitmap,
    val segmented: Bitmap
) : Parcelable {

    @Keep
    enum class Type {
        BUNDLED_OBJECT,
        REMOTE
    }
}
