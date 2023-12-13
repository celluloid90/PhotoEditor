/*
 * Copyright 2022 - 2023 Brain Craft Ltd - All Rights Reserved
 *
 * This file is a part of BgRemover.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * @file: Progress.kt
 * @author: shakib@braincraftapps.com
 * @modified: May 30, 2023, 01:02 PM
 */

package com.example.segmentation.ai.downloader.data

data class Progress(
    val totalBytes: Long,
    val downloadedBytes: Long
)
