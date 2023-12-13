/*
 * Copyright 2022 - 2023 Brain Craft Ltd - All Rights Reserved
 *
 * This file is a part of BgRemover.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * @file: ModelFile.kt
 * @author: shakib@braincraftapps.com
 * @modified: Jun 20, 2023, 01:39 PM
 */

package com.example.segmentation.ai.model.data

import android.util.Size
import java.io.File

data class ModelFile(
    val file: File, val type: Type
) {
    enum class Type {
        BUNDLED_CLASSIFICATION, BUNDLED_OBJECT, DOWNLOADED_HUMAN, DOWNLOADED_FOREGROUND;

        fun toSize(): Size = when (this) {
            BUNDLED_CLASSIFICATION -> Size(224, 224)
            BUNDLED_OBJECT -> Size(512, 512)
            DOWNLOADED_HUMAN -> Size(512, 512)
            DOWNLOADED_FOREGROUND -> Size(320, 320)
        }
    }
}
