/*
 * Copyright 2022 - 2023 Brain Craft Ltd - All Rights Reserved
 *
 * This file is a part of BgRemover.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * @file: AiEraser.kt
 * @author: shakib@braincraftapps.com
 * @modified: Jun 20, 2023, 02:27 PM
 */

package com.example.segmentation.ai.eraser

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Log
import android.util.Size
import androidx.annotation.ColorInt
import com.bcl.nativemagicbrush.NativeLib
import com.example.segmentation.ai.model.data.ModelFile
import org.pytorch.Device
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import kotlin.math.roundToInt

class AiEraser(
    val modelFile: ModelFile, val deleteModelFileOnError: Boolean = true
) {

    private val module: Module? by lazy {
        var module: Module? = null
        try {
            val length = modelFile.file.length()
            Log.d("dfjasdgseg", "${length}")
            module = LiteModuleLoader.load(modelFile.file.absolutePath, null, Device.CPU)
        } catch (exception: Exception) {
            exception.printStackTrace()/*logError(
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
     * Erase the [input bitmap][inputBitmap] using an AI model.
     *
     * Calling this method when [isReleased] is `true` will throw an [IllegalStateException].
     *
     * @see release
     * @see isReleased
     */
    fun eraseNow(inputBitmap: Bitmap): Bitmap? {
        if (isReleased) {/* logErrorAndThrow(
                 throwable = IllegalStateException("Can't erase with ai model. AiEraser is released!"),
                 tag = LogTags.AI_IMAGE_ERASER
             )*/
        }
        val module = module ?: return null
        val optimizedBitmap = inputBitmap.copy(
            Bitmap.Config.ARGB_8888, true
        ) // Convert to a mutable bitmap and copy to preserve the source bitmap
        val mask = getErasedBitmapByU2NETSegmentation(module, optimizedBitmap)
        var bitmap = getMaskAppliedBitmap(optimizedBitmap, mask)
        bitmap = getNoisedRemovedBitmap(bitmap)
        repeat(3) {
            bitmap = getMaskAppliedBitmap(bitmap, mask)
        }
        return bitmap
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

    private fun getErasedBitmapByU2NETSegmentation(module: Module, bitmap: Bitmap): Bitmap {
        val scaledBitmap = Bitmap.createScaledBitmap(
            bitmap, segmentedImageSize.width, segmentedImageSize.height, true
        )
//        for small model

        /*val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            scaledBitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )*/

//        for modnet model
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            scaledBitmap, floatArrayOf(0.5f, 0.5f, 0.5f), floatArrayOf(0.5f, 0.5f, 0.5f)
        )

        scaledBitmap.recycle()
        val value: IValue = module.forward(IValue.from(inputTensor))
        val outputTensor: Tensor =
            if (value.isTuple) value.toTuple()[1].toTensor() else value.toTensor()
        val outputMask = floatArrayToBitmap(outputTensor.dataAsFloatArray)
        val outputMaskScaled =
            Bitmap.createScaledBitmap(outputMask, bitmap.width, bitmap.height, true)
        outputMask.recycle()
        NativeLib.makeSmoothShiftedMask(outputMaskScaled)
        return outputMaskScaled
    }

    private fun floatArrayToBitmap(floatArray: FloatArray): Bitmap {
        // Create empty bitmap in ARGB format
        val bitmap: Bitmap = Bitmap.createBitmap(
            segmentedImageSize.width, segmentedImageSize.height, Bitmap.Config.ARGB_8888
        )
        val pixels = IntArray(segmentedImageSize.width * segmentedImageSize.height)
        val maxValue = floatArray.maxOrNull()
        val minValue = floatArray.minOrNull()
        val deltaValue = maxValue?.minus(minValue!!)
        val conversion = { v: Float -> ((v - minValue!!) / deltaValue!! * 255.0f).roundToInt() }
        repeat(segmentedImageSize.width * segmentedImageSize.height) { index ->
            val alpha = conversion(floatArray[index])
            pixels[index] = Color.argb(alpha, alpha, alpha, alpha)
        }
        bitmap.setPixels(pixels)
        return bitmap
    }

    private fun Bitmap.setPixels(@ColorInt pixels: IntArray) {
        setPixels(pixels, 0, width, 0, 0, width, height)
    }

    private fun getMaskAppliedBitmap(bitmap: Bitmap, mask: Bitmap): Bitmap {
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            isFilterBitmap = true
            xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
        }
        canvas.drawBitmap(mask, 0F, 0F, paint)
        return bitmap
    }

    private fun getNoisedRemovedBitmap(bitmap: Bitmap): Bitmap {
        NativeLib.removeNoise(bitmap, 25)
        return bitmap
    }
}
