/*
 * Copyright 2022 - 2023 Brain Craft Ltd - All Rights Reserved
 *
 * This file is a part of BgRemover.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * @file: DownloaderSync.kt
 * @author: shakib@braincraftapps.com
 * @modified: May 29, 2023, 05:26 PM
 */

package com.example.segmentation.ai.downloader.core

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import androidx.core.os.CancellationSignal
import androidx.work.Data
import androidx.work.ListenableWorker
import com.example.segmentation.ai.downloader.data.Progress
import com.example.segmentation.ai.downloader.data.Status
import com.example.segmentation.ai.extension.OutOfMemoryException
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.OutputStream
import java.util.concurrent.TimeUnit

class DownloaderSync(val config: Config<*>) {
    private var lastProgressUpdateTime: Long = 0
    private var publishedProgressBytes: Long = 0

    @SuppressLint("RestrictedApi")
    fun start(contentResolver: ContentResolver): ListenableWorker.Result {
        var success = false
        var memoryException = ""
        try {
            val client: OkHttpClient =
                OkHttpClient.Builder().readTimeout(config.timeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(config.timeout, TimeUnit.MILLISECONDS)
                    .connectTimeout(config.timeout, TimeUnit.MILLISECONDS)
                    .followRedirects(config.followRedirect)
                    .followSslRedirects(config.followSslRedirect).build()
            val request = Request.Builder().url(config.url).build()
            val response = client.newCall(request).execute()
            if (response.code == 200) {
                val body = response.body
                if (body != null) {
                    val totalBytes = body.contentLength()
                    config.onStart?.invoke(totalBytes)
                    BufferedInputStream(body.byteStream()).use { inputStream ->
                        var outputStream: OutputStream? = null
                        if (config.output is Uri) {
                            outputStream = contentResolver.openOutputStream(config.output)
                        } else if (config.output is File) {
                            outputStream = config.output.outputStream()
                        }
                        outputStream?.use { stream ->
                            BufferedOutputStream(stream).use { bufferedOutputStream ->
                                try {
                                    var bytesCopied: Long = 0
                                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                                    var bytes = inputStream.read(buffer)
                                    while (bytes >= 0) {
                                        if (config.cancellationSignal?.isCanceled == true) {
                                            return ListenableWorker.Result.failure(
                                                Data(
                                                    mutableMapOf(
                                                        Pair("Error", "")
                                                    )
                                                )
                                            )
                                        }
                                        bufferedOutputStream.write(buffer, 0, bytes)
                                        bytesCopied += bytes
                                        bytes = inputStream.read(buffer)
                                        publishProgress(
                                            totalBytes = totalBytes, downloadedBytes = bytesCopied
                                        )
                                    }
                                    if (publishedProgressBytes != totalBytes) {
                                        publishProgress(
                                            totalBytes = totalBytes,
                                            downloadedBytes = totalBytes,
                                            force = true
                                        )
                                    }
                                    success = true
                                } catch (exception: Exception) {
                                    config.onError?.invoke(exception)
                                }
                            }
                        }
                    }
                }
            }
        } catch (exception: Exception) {
            config.onError?.invoke(exception)
            if (exception is OutOfMemoryException) {
                memoryException = "out_of_memory"
            }
        }
        return if (success) {
            config.onComplete?.invoke(Status.SUCCESS)
            ListenableWorker.Result.success()
        } else {
            config.onComplete?.invoke(Status.FAILED)
            ListenableWorker.Result.failure(Data(mutableMapOf(Pair("Error", memoryException))))
        }
//        return Pair(success, memoryException)
    }

    private fun publishProgress(totalBytes: Long, downloadedBytes: Long, force: Boolean = false) {
        if (force) {
            sendProgress(totalBytes, downloadedBytes)
            lastProgressUpdateTime = System.currentTimeMillis()
        } else {
            val time = System.currentTimeMillis()
            if ((time - lastProgressUpdateTime) >= config.progressUpdateInterval) {
                sendProgress(totalBytes, downloadedBytes)
                lastProgressUpdateTime = time
            }
        }
    }

    private fun sendProgress(totalBytes: Long, downloadedBytes: Long) {
        config.onProgress?.invoke(
            Progress(
                totalBytes = totalBytes, downloadedBytes = downloadedBytes
            )
        )
        publishedProgressBytes = downloadedBytes
    }

    data class Config<T>(
        val url: String,
        val output: T,
        val timeout: Long,
        val followRedirect: Boolean,
        val followSslRedirect: Boolean,
        val progressUpdateInterval: Long,
        val cancellationSignal: CancellationSignal?,
        val onStart: ((Long) -> Unit)?,
        val onProgress: ((Progress) -> Unit)?,
        val onComplete: ((Status) -> Unit)?,
        val onError: ((Exception) -> Unit)?,
        val contentResolver: ContentResolver
    )
}
