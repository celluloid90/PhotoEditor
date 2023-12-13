package com.example.segmentation.ai.extension

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import androidx.core.content.getSystemService
import java.io.File

/**
 * @author: Raihan Uddin Piash (raihan.uddin@braincraftapps.com)
 * @project: StickerMaker
 * @date: On 7/6/23 at 12:48 PM
 */
fun Context.allocatePossible(file: File, totalBytes: Long): Boolean {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val storageManager: StorageManager = getSystemService() ?: return false
            val uuid = storageManager.getUuidForPath(file)
            val allocatableBytes = storageManager.getAllocatableBytes(uuid)
            if (totalBytes > allocatableBytes) {
                return false
            }
            storageManager.allocateBytes(uuid, totalBytes)
        } else {
            var availableSpace = -1L
            val stat = StatFs(Environment.getExternalStorageDirectory().path)
            availableSpace = stat.availableBlocksLong * stat.blockSizeLong
            return availableSpace > totalBytes
        }
        return true
    } catch (e: Exception) {

    }

    return false
}