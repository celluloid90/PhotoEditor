/*
 * Copyright 2022 - 2023 Brain Craft Ltd - All Rights Reserved
 *
 * This file is a part of BgRemover.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * @file: NativeLib.kt
 * @author: shakib@braincraftapps.com
 * @modified: May 31, 2023, 02:38 PM
 */

package com.bcl.nativemagicbrush

import android.graphics.Bitmap

object NativeLib {

    init {
        // Used to load the 'nativemagicbrush' library on application startup.
        System.loadLibrary("nativemagicbrush")
    }

    /**
     * A native method that is implemented by the 'nativemagicbrush' native library,
     * which is packaged with this application.
     */
    external fun eraseMagicallyWithDynamicThreshold(bitmap: Bitmap, x: Int, y: Int, radius: Int, magicThreshold: Int)
    external fun eraseMagicallyWithDynamicThresholdModified(bitmap: Bitmap, x: Int, y: Int, color: Int, radius: Int, magicThreshold: Int)
    external fun eraseMagically(bitmap: Bitmap, x: Int, y: Int, magicThreshold: Int)

    external fun removeNoise(bitmap: Bitmap, alphaThreshold: Int)

    external fun makeOpaque(mask: Bitmap, color: Int)

    external fun makeSmoothShiftedMask(bitmap: Bitmap)
}
