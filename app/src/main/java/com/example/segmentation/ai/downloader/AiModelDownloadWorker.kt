/*
 * Copyright 2022 - 2023 Brain Craft Ltd - All Rights Reserved
 *
 * This file is a part of BgRemover.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * @file: AiModelDownloadWorker.kt
 * @author: shakib@braincraftapps.com
 * @modified: Jun 20, 2023, 04:35 PM
 */

package com.example.segmentation.ai.downloader

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import com.example.segmentation.ai.AiEraserManager.Companion.getInstance
import com.example.segmentation.ai.downloader.builder.DownloaderBuilder
import com.example.segmentation.ai.extension.OutOfMemoryException
import com.example.segmentation.ai.extension.allocatePossible
import java.io.File
import java.util.concurrent.TimeUnit

class AiModelDownloadWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private lateinit var contentResolver: ContentResolver

    companion object {
        const val WORKER_TAG = "ai_model_download_worker"
        const val LOG_TAG = "ai_model_download"
        const val EXTRA_URL = "extra_url"
        const val EXTRA_PATH = "extra_path"

        fun getWorkRequest(url: String, outputFile: File): OneTimeWorkRequest {
            val data = Data.Builder().putString(EXTRA_URL, url)
                .putString(EXTRA_PATH, outputFile.absolutePath).build()
            return OneTimeWorkRequest.Builder(AiModelDownloadWorker::class.java)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 5, TimeUnit.MINUTES)
//                .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofSeconds(10)).addTag(WORKER_TAG)
                .setInputData(data).setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                ).build()
        }
    }

    init {
        setContentResolver(context.contentResolver, applicationContext)
    }

    private fun setContentResolver(resolver: ContentResolver, context: Context) {
        contentResolver = resolver
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        val downloadUrl = inputData.getString(EXTRA_URL) ?: return Result.failure()
        val outputFile =
            inputData.getString(EXTRA_PATH)?.let { File(it) } ?: return Result.failure()
        if (outputFile.exists()) {
            return Result.success()
        }
        val tempFile = File(outputFile.parentFile, outputFile.name + "downloading")
        try {
            val downloader =
                DownloaderBuilder(downloadUrl).setOutputFile(tempFile).doOnStart { totalBytes ->
                    if (!applicationContext.allocatePossible(tempFile, totalBytes)) {
                        throw OutOfMemoryException()
                    }
//                    Log.d("modelDownload", "doOnStart ")
                    Handler(Looper.getMainLooper()).post {
                        (getInstance(applicationContext).isDownloaderRunningLiveData as MutableLiveData<Boolean>).value =
                            true
                    }
                }.doOnProgress {
//                    Log.d("modelDownload", "onProgress " + it)
                }.doOnComplete {
//                    Log.d("modelDownload", "onComplete " + it)
                }.doOnError {
//                    Log.d("modelDownload", "onError " + it)
                }.setContentResolver(contentResolver)

            val result = downloader.startDownload()

            if (result is ListenableWorker.Result.Success) {
                return try {
                    tempFile.renameTo(outputFile)
                    Result.success()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Result.retry()
                }
            } else if (result is ListenableWorker.Result.Failure) {
                result.outputData.getString("Error")
                tempFile.deleteRecursively()
                return result
            }
        } catch (exception: Exception) {
            tempFile.deleteRecursively()
            return Result.retry()
        }
        tempFile.deleteRecursively()
        return Result.retry()
    }
}
