/*
 * Copyright 2022 - 2023 Brain Craft Ltd - All Rights Reserved
 *
 * This file is a part of BgRemover.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * @file: DownloaderBuilder.kt
 * @author: shakib@braincraftapps.com
 * @modified: May 29, 2023, 05:26 PM
 */

package com.example.segmentation.ai.downloader.builder

import android.content.ContentResolver
import android.net.Uri
import androidx.core.os.CancellationSignal
import androidx.work.ListenableWorker
import com.example.segmentation.ai.downloader.core.DownloaderSync
import com.example.segmentation.ai.downloader.core.DownloaderTask
import com.example.segmentation.ai.downloader.data.Progress
import com.example.segmentation.ai.downloader.data.Status
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

class DownloaderBuilder(private val url: String) {
    private var output: Any? = null
    private var timeout: Long = TimeUnit.MINUTES.toMillis(3)
    private var progressUpdateInterval: Long = 1.seconds.inWholeMilliseconds
    private var followRedirect: Boolean = true
    private var followSslRedirect: Boolean = true
    private var onStart: ((Long) -> Unit)? = null
    private var onProgress: ((Progress) -> Unit)? = null
    private var onComplete: ((Status) -> Unit)? = null
    private var onError: ((Exception) -> Unit)? = null
    private lateinit var contentResolver: ContentResolver

    constructor(uri: Uri) : this(uri.toString())

    constructor(builder: DownloaderBuilder) : this(builder.url) {
        this.output = builder.output
        this.timeout = builder.timeout
        this.progressUpdateInterval = builder.progressUpdateInterval
        this.followRedirect = builder.followRedirect
        this.followSslRedirect = builder.followSslRedirect
        this.onStart = builder.onStart
        this.onProgress = builder.onProgress
        this.onComplete = builder.onComplete
        this.onError = builder.onError
    }

    fun setContentResolver(resolver: ContentResolver): DownloaderBuilder {
        contentResolver = resolver
        return this
    }

    /**
     * Set output file to save downloaded data.
     *
     */
    fun setOutputFile(file: File): DownloaderBuilder {
        output = file
        return this
    }

    /**
     * Set output uri to save downloaded data
     *
     */
    fun setOutputUri(uri: Uri): DownloaderBuilder {
        output = uri
        return this
    }

    /**
     * Sets default timeout for new connection.
     *
     */
    fun setTimeout(msec: Long): DownloaderBuilder {
        this.timeout = msec
        return this
    }

    /**
     * Set progress update interval
     *
     */
    fun setProgressUpdateInterval(msec: Long): DownloaderBuilder {
        progressUpdateInterval = msec
        return this
    }

    /**
     * Configure this downloader to follow redirects. If unset, redirects will be followed.
     */
    fun setFollowRedirect(follow: Boolean): DownloaderBuilder {
        followRedirect = follow
        return this
    }

    /**
     * Configure this downloader to allow protocol redirects from HTTPS to HTTP and from HTTP to HTTPS.
     * Redirects are still first restricted by [follow].
     *
     * Defaults to `true`.
     */
    fun setFollowSslRedirect(follow: Boolean): DownloaderBuilder {
        followSslRedirect = follow
        return this
    }

    /**
     * Notifies when a download is started.
     *
     */
    fun doOnStart(action: (Long) -> Unit): DownloaderBuilder {
        onStart = action
        return this
    }

    /**
     * Notifies on each download [Progress].
     *
     */
    fun doOnProgress(action: (Progress) -> Unit): DownloaderBuilder {
        onProgress = action
        return this
    }

    /**
     * Notifies when download is completed with a download [Status].
     *
     */
    fun doOnComplete(action: (Status) -> Unit): DownloaderBuilder {
        onComplete = action
        return this
    }

    /**
     * Notifies when downloader throws any [Exception].
     */
    fun doOnError(action: (Exception) -> Unit): DownloaderBuilder {
        onError = action
        return this
    }

    fun copy(action: DownloaderBuilder.() -> Unit = {}): DownloaderBuilder {
        val builder = DownloaderBuilder(this)
        action(builder)
        return builder
    }

    /**
     * Build a [DownloaderTask].
     *
     * Call [DownloaderTask.start] to start download and [DownloaderTask.cancel] to cancel.
     *
     * @return a [DownloaderTask] to start or cancel download.
     */
    fun buildTask(): DownloaderTask {
        if (output == null) {/*logErrorAndThrow(
                throwable = IllegalArgumentException("Output uri or output file is not set."),
                tag = LogTags.IO
            )*/
        }
        val config = DownloaderTask.Config(
            url = url,
            output = output,
            timeout = timeout,
            progressUpdateInterval = progressUpdateInterval,
            followRedirect = followRedirect,
            followSslRedirect = followSslRedirect,
            onStart = onStart,
            onProgress = onProgress,
            onComplete = onComplete,
            onError = onError,
            contentResolver = contentResolver
        )
        return DownloaderTask(config)
    }

    /**
     * Start download synchronously.
     *
     * @param cancellationSignal Provides the ability to cancel an operation in progress
     * @return `true` if downloaded successfully. `false` otherwise.
     */
    fun startDownload(cancellationSignal: CancellationSignal? = null): ListenableWorker.Result {
        if (output == null) {/*logErrorAndThrow(
                throwable = IllegalArgumentException("Output uri or output file is not set."),
                tag = LogTags.IO
            )*/
        }
        val config = DownloaderSync.Config(
            url = url,
            output = output,
            timeout = timeout,
            followRedirect = followRedirect,
            followSslRedirect = followSslRedirect,
            progressUpdateInterval = progressUpdateInterval,
            cancellationSignal = cancellationSignal,
            onStart = onStart,
            onProgress = onProgress,
            onComplete = onComplete,
            onError = onError,
            contentResolver = contentResolver
        )
        val result = DownloaderSync(config).start(contentResolver)

        if (result is ListenableWorker.Result.Success) {
            onComplete?.invoke(Status.SUCCESS)
        } else {
            onComplete?.invoke(Status.FAILED)
        }

        /* if (result) {
             onComplete?.invoke(Status.SUCCESS)
         } else {
             onComplete?.invoke(Status.FAILED)
         }*/
        return result
    }
}
