/*
 * Copyright 2022 - 2023 Brain Craft Ltd - All Rights Reserved
 *
 * This file is a part of BgRemover.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * @file: DownloaderTask.kt
 * @author: shakib@braincraftapps.com
 * @modified: May 29, 2023, 05:26 PM
 */

package com.example.segmentation.ai.downloader.core

import android.content.ContentResolver
import android.net.Uri
import com.example.segmentation.ai.downloader.data.Progress
import com.example.segmentation.ai.downloader.data.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.OutputStream
import java.util.concurrent.TimeUnit

class DownloaderTask(private val config: Config<*>) {
    private val coroutine = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var job: Job? = null
    private var isRunning: Boolean = false
    private var lastProgressUpdateTime: Long = 0
    private var publishedProgressBytes: Long = 0

    fun start() {
        if (isRunning) {
            return
        }
        job?.cancel()
        job = coroutine.launch {
            isRunning = true
            var success = false
            try {
                val client: OkHttpClient =
                    OkHttpClient.Builder().readTimeout(config.timeout, TimeUnit.MILLISECONDS)
                        .writeTimeout(config.timeout, TimeUnit.MILLISECONDS)
                        .connectTimeout(config.timeout, TimeUnit.MILLISECONDS).build()
                val request = Request.Builder().url(config.url).build()
                val response = client.newCall(request).execute()
                if (response.code == 200) {
                    val body = response.body
                    if (body != null) {
                        val totalBytes = body.contentLength()
                        withContext(Dispatchers.Main) {
                            publishDownloadStarted(totalBytes)
                        }
                        BufferedInputStream(body.byteStream()).use { inputStream ->
                            var outputStream: OutputStream? = null
                            if (config.output is Uri) {
                                outputStream =
                                    config.contentResolver.openOutputStream(config.output)
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
                                            bufferedOutputStream.write(buffer, 0, bytes)
                                            bytesCopied += bytes
                                            bytes = inputStream.read(buffer)
                                            withContext(Dispatchers.Main) {
                                                publishProgress(
                                                    totalBytes = totalBytes,
                                                    downloadedBytes = bytesCopied
                                                )
                                            }
                                        }
                                        if (publishedProgressBytes != totalBytes) {
                                            withContext(Dispatchers.Main) {
                                                publishProgress(
                                                    totalBytes = totalBytes,
                                                    downloadedBytes = totalBytes,
                                                    force = true
                                                )
                                            }
                                        }
                                        success = true
                                    } catch (exception: Exception) {
                                        withContext(Dispatchers.Main) {
                                            config.onError?.invoke(exception)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (exception: Exception) {
                withContext(Dispatchers.Main) {
                    config.onError?.invoke(exception)
                }
            }
            withContext(Dispatchers.Main) {
                if (success) {
                    publishComplete(Status.SUCCESS)
                } else {
                    publishComplete(Status.FAILED)
                }
            }
        }
    }

    fun cancel() {
        job?.cancel()
        job = null
        if (isRunning) {
            publishComplete(Status.CANCELED)
        }
    }

    private fun publishDownloadStarted(totalBytes: Long) {
        config.onStart?.invoke(totalBytes)
    }

    private fun publishComplete(status: Status) {
        if (isRunning) {
            isRunning = false
            config.onComplete?.invoke(status)
        }
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
        val progressUpdateInterval: Long,
        val followRedirect: Boolean,
        val followSslRedirect: Boolean,
        val onStart: ((Long) -> Unit)?,
        val onProgress: ((Progress) -> Unit)?,
        val onComplete: ((Status) -> Unit)?,
        val onError: ((Exception) -> Unit)?,
        var contentResolver: ContentResolver
    )
}
