/*
 * Copyright 2022 - 2023 Brain Craft Ltd - All Rights Reserved
 *
 * This file is a part of BgRemover.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * @file: AiEraserManager.kt
 * @author: shakib@braincraftapps.com
 * @modified: Jun 20, 2023, 04:35 PM
 */

package com.example.segmentation.ai

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.segmentation.ai.eraser.AiEraser
import com.example.segmentation.ai.eraser.data.Output
import com.example.segmentation.ai.model.ModelFileProvider
import com.example.segmentation.ai.model.data.ModelFile

class AiEraserManager private constructor(var context: Context) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var aiEraserManager: AiEraserManager? = null

        fun getInstance(context: Context): AiEraserManager {
            return aiEraserManager ?: synchronized(AiEraserManager::class) {
                val manager = AiEraserManager(context.applicationContext)
                aiEraserManager = manager
                return@synchronized manager
            }
        }
    }

    val isDownloaderRunningLiveData: LiveData<Boolean> = MutableLiveData()

    /*init {
        ContextCompat.getMainExecutor(context).execute {
            WorkManager.getInstance(context)
                .getWorkInfosForUniqueWorkLiveData(ModelFileProvider.UNIQUE_DOWNLOADER_WORK_NAME)
                .observeForever { list ->
                    (isDownloaderRunningLiveData as MutableLiveData<Boolean>).value =
                        list.firstOrNull { it.state == WorkInfo.State.RUNNING } != null
                }
        }
    }*/

    private val modelFileProvider: ModelFileProvider by lazy { ModelFileProvider(context) }

    fun eraseWithAi(inputBitmap: Bitmap): Output? {/*val aiClassifier =
            AiClassifier(modelFile = modelFileProvider.getModelFile(ModelFile.Type.BUNDLED_CLASSIFICATION))
        val classificationOutput = aiClassifier.classify(inputBitmap)
        aiClassifier.release()
        val modelType = when (classificationOutput) {
            ClassificationOutput.HUMAN -> ModelFile.Type.DOWNLOADED_HUMAN
//            ClassificationOutput.FOREGROUND -> ModelFile.Type.DOWNLOADED_FOREGROUND
            ClassificationOutput.FOREGROUND -> ModelFile.Type.DOWNLOADED_HUMAN
        }*/

        /*try {
            return erase(inputBitmap, ModelFile.Type.DOWNLOADED_HUMAN)
        } catch (e: Exception) {
        }*/

        try {
            return erase(inputBitmap, ModelFile.Type.BUNDLED_OBJECT)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null

        /*var modelFile = modelFileProvider.getModelFile(ModelFile.Type.DOWNLOADED_HUMAN)
        var aiEraser = AiEraser(modelFile = modelFile)
        var outputBitmap: Bitmap
        try {
            outputBitmap = aiEraser.eraseNow(inputBitmap) ?: run {
                aiEraser.release()
                return null
            }
        } catch (e: Exception) {
            modelFile = modelFileProvider.getModelFile(ModelFile.Type.BUNDLED_OBJECT)
            aiEraser = AiEraser(modelFile = modelFile)
            outputBitmap = aiEraser.eraseNow(inputBitmap) ?: kotlin.run {
                aiEraser.release()
                return null
            }
        }

        aiEraser.release()
        return Output(outputBitmap, modelFile.type)*/
    }

    fun downloadModelIfNot() {
        modelFileProvider.downloadModelIfNot()
    }

    private fun erase(inputBitmap: Bitmap, modelType: ModelFile.Type): Output? {
        val modelFile = modelFileProvider.getModelFile(modelType)
        val aiEraser = AiEraser(modelFile = modelFile)
        val outputBitmap = aiEraser.eraseNow(inputBitmap) ?: run {
            aiEraser.release()
            return null
        }
        aiEraser.release()
        return Output(outputBitmap, modelFile.type)
    }
}
