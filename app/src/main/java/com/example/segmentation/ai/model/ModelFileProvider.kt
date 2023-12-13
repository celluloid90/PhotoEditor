/*
 * Copyright 2022 - 2023 Brain Craft Ltd - All Rights Reserved
 *
 * This file is a part of BgRemover.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * @file: ModelFileProvider.kt
 * @author: shakib@braincraftapps.com
 * @modified: Jun 20, 2023, 03:25 PM
 */

package com.example.segmentation.ai.model

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.os.StatFs
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.photo_editor.BuildConfig
import com.example.segmentation.ai.downloader.AiModelDownloadWorker
import com.example.segmentation.ai.model.data.ModelFile
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class ModelFileProvider(var context: Context) {
    companion object {
        const val UNIQUE_DOWNLOADER_WORK_NAME = "bcl_ai_model_download_worker_unique_name"
        private const val MODEL_FILE_NAME = "model.ptl"
        private const val ASSET_CLASSIFICATION_MODEL_FILE_NAME = "models/ml_classification.ptl"
        private const val ASSET_OBJECT_MODEL_FILE_NAME = "models/modnet_portrait_matting.ptl"
//        private const val ASSET_OBJECT_MODEL_FILE_NAME = "models/ml_object_small.ptl"
        private const val ROOT_DIRECTORY_NAME = "ai_model"
        private const val VERSIONED_DIRECTORY_PREFIX = "version_"
        private const val DIRECTORY_NAME_CLASSIFICATION = "bundled_classification"
        private const val DIRECTORY_NAME_BUNDLED_OBJECT = "bundled_object"
        private const val DIRECTORY_NAME_DOWNLOADED_HUMAN = "downloaded_human"
        private const val DIRECTORY_NAME_DOWNLOADED_FOREGROUND = "downloaded_foreground"
        private const val MINIMUM_AVAILABLE_STORAGE_REQUIRED_IN_MB = 200L
    }

    private var fileProvider: ModelFileProvider? = null

    fun getInstance(context: Context): ModelFileProvider {
        return fileProvider ?: synchronized(ModelFileProvider::class) {
            val manager = ModelFileProvider(context.applicationContext)
            fileProvider = manager
            return@synchronized manager
        }
    }

    private val rootDirectory: File
        get() {
            val fileDir = context.filesDir
            val directory = File(fileDir, ROOT_DIRECTORY_NAME)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            return directory
        }

    private val bundledClassificationDirectory: File
        get() {
            val directory = File(rootDirectory, DIRECTORY_NAME_CLASSIFICATION)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val currentVersionedDirectoryName =
                VERSIONED_DIRECTORY_PREFIX + BuildConfig.MODEL_VERSION_CLASSIFICATION
            val versionedDirectory = File(directory, currentVersionedDirectoryName)
            if (!versionedDirectory.exists()) {
                versionedDirectory.mkdirs()
            }
            directory.listFiles()?.forEach {
                if (!it.name.equals(currentVersionedDirectoryName)) {
                    it.deleteRecursively()
                }
            }
            return versionedDirectory
        }

    private val bundledClassificationModelFile: File
        get() {
            val file = File(bundledClassificationDirectory, MODEL_FILE_NAME)
            if (!file.exists()) {
                context.assets?.open(ASSET_CLASSIFICATION_MODEL_FILE_NAME)?.use { inputStream ->
                    BufferedOutputStream(FileOutputStream(file)).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
            return file
        }

    private val bundledObjectModelDirectory: File
        get() {
            val directory = File(rootDirectory, DIRECTORY_NAME_BUNDLED_OBJECT)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val currentVersionedDirectoryName =
                VERSIONED_DIRECTORY_PREFIX + BuildConfig.MODEL_VERSION_BUNDLED
            val versionedDirectory = File(directory, currentVersionedDirectoryName)
            if (!versionedDirectory.exists()) {
                versionedDirectory.mkdirs()
            }
            directory.listFiles()?.forEach {
                if (!it.name.equals(currentVersionedDirectoryName)) {
                    it.deleteRecursively()
                }
            }
            return versionedDirectory
        }

    private val bundleObjectModelFile: File
        get() {
            val file = File(bundledObjectModelDirectory, MODEL_FILE_NAME)
            if (!file.exists()) {
                context.assets?.open(ASSET_OBJECT_MODEL_FILE_NAME)?.use { inputStream ->
                    BufferedOutputStream(FileOutputStream(file)).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
            return file
        }

    private val downloadedHumanDirectory: File
        get() {
            val directory = File(rootDirectory, DIRECTORY_NAME_DOWNLOADED_HUMAN)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val currentVersionedDirectoryName =
                VERSIONED_DIRECTORY_PREFIX + BuildConfig.MODEL_VERSION_HUMAN_LARGE
            val versionedDirectory = File(directory, currentVersionedDirectoryName)
            if (!versionedDirectory.exists()) {
                versionedDirectory.mkdirs()
            }
            directory.listFiles()?.forEach {
                if (!it.name.equals(currentVersionedDirectoryName)) {
                    it.deleteRecursively()
                }
            }
            return versionedDirectory
        }

    private val downloadedHumanTempCacheFile: File
        get() {
            return File(rootDirectory, "temp_cache_human")
        }

    private val downloadedHumanModelFile: File
        get() {
            val file = File(downloadedHumanDirectory, MODEL_FILE_NAME)
            val cacheFile = downloadedHumanTempCacheFile
            if (cacheFile.exists()) {
                cacheFile.renameTo(file)
            }
            return file
        }

    private val downloadedForegroundDirectory: File
        get() {
            val directory = File(rootDirectory, DIRECTORY_NAME_DOWNLOADED_FOREGROUND)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val currentVersionedDirectoryName =
                VERSIONED_DIRECTORY_PREFIX + BuildConfig.MODEL_VERSION_FOREGROUND_LARGE
            val versionedDirectory = File(directory, currentVersionedDirectoryName)
            if (!versionedDirectory.exists()) {
                versionedDirectory.mkdirs()
            }
            directory.listFiles()?.forEach {
                if (!it.name.equals(currentVersionedDirectoryName)) {
                    it.deleteRecursively()
                }
            }
            return versionedDirectory
        }

    private val downloadedForegroundCacheFile: File
        get() {
            return File(rootDirectory, "temp_cache_foreground")
        }

    private val downloadedForegroundModelFile: File
        get() {
            val file = File(downloadedForegroundDirectory, MODEL_FILE_NAME)
            val cacheFile = downloadedForegroundCacheFile
            if (cacheFile.exists()) {
                cacheFile.renameTo(file)
            }
            return file
        }

    /**
     * Get model file as requested.
     *
     * **Note:** If [ModelFile.Type.DOWNLOADED_HUMAN] or [ModelFile.Type.DOWNLOADED_FOREGROUND]
     * is not available, [ModelFile.Type.BUNDLED_OBJECT] will be returned instead.
     */
    fun getModelFile(requestedType: ModelFile.Type): ModelFile {
//        downloadModelIfNot()
        return when (requestedType) {
            ModelFile.Type.BUNDLED_CLASSIFICATION -> ModelFile(
                bundledClassificationModelFile, ModelFile.Type.BUNDLED_CLASSIFICATION
            )

            ModelFile.Type.BUNDLED_OBJECT -> ModelFile(
                bundleObjectModelFile, ModelFile.Type.BUNDLED_OBJECT
            )

            ModelFile.Type.DOWNLOADED_HUMAN -> if (downloadedHumanModelFile.exists()) {
                ModelFile(
                    downloadedHumanModelFile, ModelFile.Type.DOWNLOADED_HUMAN
                )
            } else ModelFile(bundleObjectModelFile, ModelFile.Type.BUNDLED_OBJECT)

            ModelFile.Type.DOWNLOADED_FOREGROUND -> if (downloadedForegroundModelFile.exists()) {
                ModelFile(
                    downloadedForegroundModelFile, ModelFile.Type.DOWNLOADED_FOREGROUND
                )
            } else ModelFile(bundleObjectModelFile, ModelFile.Type.BUNDLED_OBJECT)
        }
    }

    /**
     * Download [ModelFile.Type.DOWNLOADED_HUMAN] and [ModelFile.Type.DOWNLOADED_FOREGROUND] model if not exists on the disk.
     */
    @SuppressLint("EnqueueWork")
    fun downloadModelIfNot() {/*if (isEnqueuedAny()) {
            return
        }*/
//        val isMinimumRequiredStorageAvailable = getAvailableSpaceInMB() > MINIMUM_AVAILABLE_STORAGE_REQUIRED_IN_MB
        val workRequestList = ArrayList<OneTimeWorkRequest>()
        if (!downloadedHumanModelFile.exists() /*&& isMinimumRequiredStorageAvailable*/) {
//            downloadedHumanTempCacheFile.deleteRecursively()
            workRequestList.add(
                AiModelDownloadWorker.getWorkRequest(
                    BuildConfig.MODEL_DOWNLOAD_URL_HUMAN_LARGE, downloadedHumanTempCacheFile
                )
            )
        }

        /* if (!downloadedForegroundModelFile.exists()) {
             downloadedForegroundCacheFile.deleteRecursively()
             workRequestList.add(
                 AiModelDownloadWorker.getWorkRequest(
                     BuildConfig.MODEL_DOWNLOAD_URL_FOREGROUND_LARGE, downloadedForegroundCacheFile
                 )
             )
         }*/

        if (workRequestList.isEmpty()) {
            return
        }
        val workManager = WorkManager.getInstance(context)
        val workContinuation = workManager.beginUniqueWork(
            UNIQUE_DOWNLOADER_WORK_NAME, ExistingWorkPolicy.KEEP, workRequestList.removeFirst()
        )
        workRequestList.forEach {
            workContinuation.then(it)
        }
        workContinuation.enqueue()
    }

    /*fun isEnqueuedAny(): Boolean {
        return WorkManager.getInstance(context).getWorkInfosByTag(AiModelDownloadWorker.WORKER_TAG)
            .get().firstOrNull { it.state == WorkInfo.State.ENQUEUED } != null
    }*/

    private fun getAvailableSpaceInMB(): Long {
        val sizeKB = 1024L
        val sizeMB = sizeKB * sizeKB
        var availableSpace = -1L
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        availableSpace = stat.availableBlocksLong * stat.blockSizeLong
        return availableSpace / sizeMB
    }
}
