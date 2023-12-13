package com.example.segmentation.segmentation

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File

/**
 * @author: Raihan Uddin Piash (raihan.uddin@braincraftapps.com)
 * @project: StickerMaker
 * @date: On 10/12/23 at 11:57 AM
 */

fun saveToGallery(
    context: Context, bitmap: Bitmap, albumName: String, imageName: String
) {
    val filename = "${imageName}_demo.png"

    val externalImageContentUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    val contentValues = ContentValues()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentValues.put(
            MediaStore.Images.Media.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES + File.separator + albumName
        )
    } else {
        val directory =
            File(Environment.getExternalStorageDirectory().absolutePath + File.separator + Environment.DIRECTORY_PICTURES/* + File.separator + albumName*/)

        if (!directory.exists()) {
            directory.mkdirs()
        }
        val outputFile = File(directory, filename)
        if (outputFile.exists()) {
            outputFile.deleteRecursively()
        }

        outputFile.createNewFile()
        contentValues.put(MediaStore.Images.Media.DATA, outputFile.absolutePath)
    }

    val uri = context.contentResolver.insert(externalImageContentUri, contentValues) ?: return

    context.contentResolver.openOutputStream(uri).use { stream ->
        runCatching {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream!!)
        }
        stream?.flush()
    }
}