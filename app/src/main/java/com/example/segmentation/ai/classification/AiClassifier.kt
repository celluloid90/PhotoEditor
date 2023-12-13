/*
 * Copyright 2022 - 2023 Brain Craft Ltd - All Rights Reserved
 *
 * This file is a part of BgRemover.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * @file: AiClassifier.kt
 * @author: shakib@braincraftapps.com
 * @modified: Jun 20, 2023, 02:48 PM
 */

package com.example.segmentation.ai.classification

import android.graphics.Bitmap
import android.util.Size
import com.example.segmentation.ai.classification.data.ClassificationOutput
import com.example.segmentation.ai.model.data.ModelFile
import org.pytorch.Device
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils

class AiClassifier(
    val modelFile: ModelFile,
    val deleteModelFileOnError: Boolean = true
) {

    companion object {
        private const val MINIMUM_HUMAN_THRESHOLD = 0.8F
    }

    private val module: Module? by lazy {
        if (modelFile.type != ModelFile.Type.BUNDLED_CLASSIFICATION) {
            /*logErrorAndThrow(
                throwable = IllegalStateException("Invalid model file type: ${modelFile.type}"),
                tag = LogTags.AI_IMAGE_ERASER
            )*/
        }
        var module: Module? = null
        try {
            module = LiteModuleLoader.load(modelFile.file.absolutePath, null, Device.CPU)
        } catch (exception: Exception) {
            /*logError(
                throwable = exception,
                comment = "Can not load module from: ${modelFile.file.absolutePath}",
                tag = LogTags.AI_IMAGE_ERASER
            )*/
            if (deleteModelFileOnError) {
                modelFile.file.deleteRecursively()
            }
        }
        return@lazy module
    }

    private val segmentedImageSize: Size by lazy { modelFile.type.toSize() }

    /**
     * `true` when the model is released it's resources, `false` otherwise.
     */
    var isReleased: Boolean = false
        private set

    /**
     * Classify the [input bitmap][inputBitmap] using an AI model.
     *
     * Calling this method when [isReleased] is `true` will throw an [IllegalStateException].
     *
     * @see release
     * @see isReleased
     *
     * @return [ClassificationOutput.HUMAN] or [ClassificationOutput.FOREGROUND]
     */
    fun classify(inputBitmap: Bitmap): ClassificationOutput {
        if (isReleased) {
            /*logErrorAndThrow(
                throwable = IllegalStateException("Can't classify with ai model. AiClassifier is released!"),
                tag = LogTags.AI_IMAGE_ERASER
            )*/
        }
        val module = module /*?: logErrorAndThrow(
            throwable = IllegalStateException("Can't classify with ai model. No AI module found!"),
            tag = LogTags.AI_IMAGE_ERASER
        )*/
        val scaledBitmap = Bitmap.createScaledBitmap(inputBitmap, segmentedImageSize.width, segmentedImageSize.height, true)
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            scaledBitmap, floatArrayOf(0.5F, 0.5F, 0.5F), floatArrayOf(0.5F, 0.5F, 0.5F)
        )
        scaledBitmap.recycle()
        val value: IValue = module!!.forward(IValue.from(inputTensor))
        val outputTensor: Tensor = if (value.isTuple) value.toTuple()[1].toTensor() else value.toTensor()
        val result = outputTensor.dataAsFloatArray[0]
        return if (result >= MINIMUM_HUMAN_THRESHOLD) ClassificationOutput.HUMAN else ClassificationOutput.FOREGROUND
    }

    /**
     * Explicitly destroys the native torch::jit::Module. Calling this method is not required, as the
     * native object will be destroyed when this object is garbage-collected. However, the timing of
     * garbage collection is not guaranteed, so proactively calling [release] can free memory
     * more quickly.
     */
    fun release() {
        module?.destroy()
        isReleased = true
    }
}
