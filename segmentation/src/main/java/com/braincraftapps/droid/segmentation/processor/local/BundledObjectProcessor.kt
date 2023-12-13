/*
 * Copyright 2022 - 2023 Brain Craft Ltd - All Rights Reserved
 *
 * This file is a part of BgRemover.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * @file: BundledObjectProcessor.kt
 * @author: shakib@braincraftapps.com
 * @modified: Nov 13, 2023, 12:11 PM
 */

package com.braincraftapps.droid.segmentation.processor.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Size
import androidx.annotation.ColorInt
import androidx.core.graphics.scale
import com.bcl.nativemagicbrush.NativeLib
import com.braincraftapps.droid.segmentation.data.SegmentationOutput
import com.braincraftapps.droid.segmentation.processor.SegmentationProcessor
import org.pytorch.Device
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import kotlin.math.roundToInt

class BundledObjectProcessor(applicationContext: Context) :
    SegmentationProcessor(applicationContext) {
    companion object {
        private const val MODEL_FILE_NAME = "model.ptl"
        private const val ASSET_OBJECT_MODEL_FILE_NAME = "segmentation/ml_object_small.ptl"
        private const val ROOT_DIRECTORY_NAME = "ai_model"
        private const val OBJECT_MODEL_DIRECTORY_NAME = "object"
        private const val VERSIONED_DIRECTORY_PREFIX = "v"
        private const val MODEL_VERSION_BUNDLED = 1

        private val BUNDLE_OBJECT_INPUT_SIZE: Size by lazy { Size(512, 512) }
    }

    override fun onProcess(input: Bitmap): SegmentationOutput? {
        val module = getPyTorchModule() ?: return null
        val original = input.copy(input.config, true)
        val scaledInputBitmap =
            input.scale(BUNDLE_OBJECT_INPUT_SIZE.width, BUNDLE_OBJECT_INPUT_SIZE.height)
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            scaledInputBitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )
        val value: IValue = module.forward(IValue.from(inputTensor))
        val outputTensor: Tensor =
            if (value.isTuple) value.toTuple()[1].toTensor() else value.toTensor()
        val mask = floatArrayToBitmap(
            outputTensor.dataAsFloatArray, scaledInputBitmap.width, scaledInputBitmap.height
        )
        module.destroy()
        val scaledMask = Bitmap.createScaledBitmap(mask, input.width, input.height, true)
        NativeLib.makeSmoothShiftedMask(scaledMask)
        input.applyMask(scaledMask)
        NativeLib.removeNoise(input, 25)
        repeat(3) {
            input.applyMask(scaledMask)
        }
        return SegmentationOutput(
            type = SegmentationOutput.Type.BUNDLED_OBJECT, original = original, segmented = input
        )
    }

    private fun getPyTorchModule(): Module? {
        val fileDir = applicationContext.filesDir
        val rootDirectory = File(fileDir, ROOT_DIRECTORY_NAME)
        if (!rootDirectory.exists()) {
            rootDirectory.mkdirs()
        }
        val directory = File(rootDirectory, OBJECT_MODEL_DIRECTORY_NAME)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val currentVersionedDirectoryName = VERSIONED_DIRECTORY_PREFIX + MODEL_VERSION_BUNDLED
        val versionedDirectory = File(directory, currentVersionedDirectoryName)
        if (!versionedDirectory.exists()) {
            versionedDirectory.mkdirs()
        }
        directory.listFiles()?.forEach {
            if (!it.name.equals(currentVersionedDirectoryName)) {
                it.deleteRecursively()
            }
        }
        val modelFile = File(versionedDirectory, MODEL_FILE_NAME)
        if (!modelFile.exists()) {
            applicationContext.assets?.open(ASSET_OBJECT_MODEL_FILE_NAME)?.buffered()
                ?.use { inputStream ->
                    modelFile.outputStream().buffered().use { outputStream ->
                        inputStream.copyTo(outputStream)
                        outputStream.flush()
                    }
                }
        }
        var module: Module? = null
        try {
            module = LiteModuleLoader.load(modelFile.absolutePath, null, Device.CPU)
        } catch (exception: Exception) {
            exception.printStackTrace()
            /*logError(
                throwable = exception,
                comment = "Can not load module from: ${modelFile.absolutePath}",
                tag = LogTags.AI_IMAGE_ERASER
            )*/
            modelFile.deleteRecursively()
        }
        return module
    }

    private fun floatArrayToBitmap(floatArray: FloatArray, width: Int, height: Int): Bitmap {
        // Create empty bitmap in ARGB format
        val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        val maxValue = floatArray.max()
        val minValue = floatArray.min()
        val deltaValue = maxValue - minValue
        val conversion = { v: Float -> ((v - minValue) / deltaValue * 255.0f).roundToInt() }
        repeat(width * height) { index ->
            val alpha = conversion(floatArray[index])
            pixels[index] = Color.argb(alpha, alpha, alpha, alpha)
        }
        bitmap.setPixels(pixels)
        return bitmap
    }

    fun Bitmap.setPixels(@ColorInt pixels: IntArray) {
        setPixels(pixels, 0, width, 0, 0, width, height)
    }
}
