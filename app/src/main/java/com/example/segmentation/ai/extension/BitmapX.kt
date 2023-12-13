package com.example.segmentation.ai.extension

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.util.Size
import androidx.annotation.ColorInt
import androidx.core.graphics.scale
import kotlin.math.max
import kotlin.math.min

/**
 * @author: Raihan Uddin Piash (raihan.uddin@braincraftapps.com)
 * @project: StickerMaker
 * @date: On 6/25/23 at 5:08 PM
 */

/**
 * Smartly cast [Number] object to following primitive type:
 * - [Char]
 * - [Byte]
 * - [Short]
 * - [Int]
 * - [Long]
 * - [Float]
 * - [Double]
 *
 */
inline fun <reified N : Number> Number.toPrimitiveType(): N {
    return when (N::class) {
        Char::class -> this.toInt().toChar() as N
        Byte::class -> this.toByte() as N
        Short::class -> this.toShort() as N
        Int::class -> this.toInt() as N
        Long::class -> this.toLong() as N
        Float::class -> this.toFloat() as N
        Double::class -> this.toDouble() as N
        else -> throw IllegalArgumentException("${N::class.java.name} is not valid Number type.")
    }
}

/**
 * Scale to targeted range.
For example, scale *5* from 0 to 10 will be *50* in 0 to 100* range.
 *
 * @param valueFrom minimum value
 * @param valueTo maximum value
 * @param scaleFrom minimum target value
 * @param scaleTo maximum target value
 * @return scaled value between [scaleFrom] to [scaleTo]
 */
inline fun <reified N : Number> Number.scale(
    valueFrom: Number,
    valueTo: Number,
    scaleFrom: Number,
    scaleTo: Number
): N {
    if (this is Long || this is Double) {
        val vFrom = valueFrom.toDouble()
        val vTo = valueTo.toDouble()
        val sFrom = scaleFrom.toDouble()
        val sTo = scaleTo.toDouble()
        return ((((this.toDouble() - vFrom) * (sTo - sFrom)) / (vTo - vFrom)) + sFrom).toPrimitiveType()
    }
    val vFrom = valueFrom.toFloat()
    val vTo = valueTo.toFloat()
    val sFrom = scaleFrom.toFloat()
    val sTo = scaleTo.toFloat()
    return ((((this.toFloat() - vFrom) * (sTo - sFrom)) / (vTo - vFrom)) + sFrom).toPrimitiveType()
}


val emptyBitmap: Bitmap
    get() = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

/**
 * Returns the [Size] of this Bitmap.
 */
val Bitmap.size: Size
    get() = Size(width, height)

fun Bitmap.withPadding(padding: Float = 0F): Bitmap {
    if (padding <= 0) {
        return this
    }
    val paddedBitmap = Bitmap.createBitmap(
        width + (padding * 2).toInt(),
        height + (padding * 2).toInt(),
        Bitmap.Config.ARGB_8888
    )
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val canvas = Canvas(paddedBitmap)
    canvas.drawBitmap(this, padding, padding, paint)
    return paddedBitmap
}

fun Bitmap.fitCenter(
    width: Int,
    height: Int,
    padding: Float = 0F,
    alwaysCopy: Boolean = true
): Bitmap {
    if (this.width == width && this.height == height) {
        return if (padding > 0) {
            withPadding(padding)
        } else if (alwaysCopy) copy() else this
    }
    val scaleFactor =
        if (this.width.toFloat() / width.toFloat() > this.height.toFloat() / height.toFloat()) {
            width.toFloat() / this.width.toFloat()
        } else {
            height.toFloat() / this.height.toFloat()
        }
    val scaled = scale(
        (this.width.toFloat() * scaleFactor).toInt(),
        (this.height.toFloat() * scaleFactor).toInt()
    )
    return if (padding > 0) {
        scaled.withPadding(padding)
    } else scaled
}

/**
 * Resize and fit inside the [size].
 *
 * @param size new size to be resized
 * @param padding optional padding around the output bitmap
 */
fun Bitmap.fitCenter(size: Size, padding: Float = 0F): Bitmap {
    return fitCenter(size.width, size.height, padding)
}

/**
 * Returns in pixels[] a copy of the data in the bitmap.
 * Each value is a packed int representing a [Color].
 * The returned colors are non-premultiplied ARGB
 * values in the [ColorSpace.Named.SRGB] color space.
 *
 * @param pixels the array to receive the [Bitmap]'s colors
 */
fun Bitmap.getPixels(@ColorInt pixels: IntArray) {
    getPixels(pixels, 0, width, 0, 0, width, height)
}

/**
 * @return Returns in [IntArray] a copy of the data in the bitmap. Each value is a packed int representing a [Color].
 * The returned colors are non-premultiplied ARGB values in the [ColorSpace.Named.SRGB] color space.
 */
fun Bitmap.getPixels(): IntArray {
    val array = IntArray(width * height)
    getPixels(array)
    return array
}

/**
 * Replace pixels in the bitmap with the colors in the array. Each element
 * in the array is a packed int representing a non-premultiplied ARGB
 * [Color] in the [ColorSpace.Named.SRGB] color space.
 *
 * @param pixels The colors to write to the bitmap
 */
fun Bitmap.setPixels(@ColorInt pixels: IntArray) {
    setPixels(pixels, 0, width, 0, 0, width, height)
}

/**
 * Check if [x],[y] is a valid pixel in the [Bitmap]
 *
 * @param x x coordinate of the pixel
 * @param y y coordinate of the pixel
 * @return true if [x],[y] is a valid pixel
 */
fun Bitmap.isValidPixel(x: Int, y: Int): Boolean = x in 0 until width && y in 0 until height

/**
 * Transform [x],[y] coordinate into index for [Bitmap.getPixels] array.
 *
 * @param x x coordinate
 * @param y y coordinate
 * @param stride The number of entries in pixels[] to skip between rows (must be >= [Bitmap.getWidth]). Can be negative.
 * @return index for [Bitmap.getPixels] array.
 */
fun Bitmap.pointToIndex(x: Int, y: Int, stride: Int = width): Int {
    if (isValidPixel(x, y)) {
        return x + y * stride
    }
    return -1
}

/**
 * Performs the given [action] on each pixel around [x],[y] pixel.
 *
 * @param x x coordinate of the pivot
 * @param y y coordinate of the pivot
 */
fun Bitmap.forEachNeighbourHoodPixel(
    x: Int,
    y: Int,
    includeCorner: Boolean = false,
    action: (Int, Int) -> Unit
) {
    fun process(x: Int, y: Int) {
        if (isValidPixel(x, y)) {
            action(x, y)
        }
    }
    process(x, y - 1)
    process(x - 1, y)
    process(x, y + 1)
    process(x + 1, y)
    if (includeCorner) {
        process(x - 1, y - 1)
        process(x + 1, y + 1)
        process(x + 1, y - 1)
        process(x - 1, y + 1)
    }
}

/**
 * Get a bounds for actual content (without transparent area) from this [Bitmap] object.
 *
 * @param padding apply padding within this bounds
 * @return a [Rect] object with `left`,  `top`, `right` and `bottom` value
 */
fun Bitmap.getContentBounds(padding: Int = 0): Rect {
    var left = Int.MAX_VALUE
    var top = Int.MAX_VALUE
    var right = Int.MIN_VALUE
    var bottom = Int.MIN_VALUE
    repeat(width) { x ->
        repeat(height) { y ->
            if (getPixel(x, y) != Color.TRANSPARENT) {
                left = min(left, max(0, x - padding))
                top = min(top, max(0, y - padding))
                right = max(right, min(width, x + padding))
                bottom = max(bottom, min(height, y + padding))
            }
        }
    }
    if (left == Int.MAX_VALUE) {
        return Rect(0, 0, width, height)
    }
    return Rect(left, top, right, bottom)
}


/**
 * Draw alpha mask of this Bitmap to [canvas] with [paint].
 */
fun Bitmap.drawAlphaMask(
    canvas: Canvas,
    paint: Paint? = null,
    doBeforeDraw: Canvas.() -> Unit = {}
) {
    val offset = intArrayOf(0, 0)
    val alphaMask = extractAlpha(paint, offset)
    doBeforeDraw(canvas)
    canvas.drawBitmap(alphaMask, offset[0].toFloat(), offset[1].toFloat(), paint)
    alphaMask.recycle()
}

fun Bitmap.copy(): Bitmap {
    return copy(Bitmap.Config.ARGB_8888, true)
}

val Bitmap.isLandscape: Boolean
    get() = width > height

fun Bitmap.cropToContent(): Bitmap {
    val contentBounds = getContentBounds()
    return Bitmap.createBitmap(
        this,
        contentBounds.left,
        contentBounds.top,
        contentBounds.width(),
        contentBounds.height()
    )
}

fun Bitmap.getContentFrom(other: Bitmap): Bitmap {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        maskFilter = BlurMaskFilter(2.5f, BlurMaskFilter.Blur.INNER)
        color = Color.WHITE
    }
    val contentBounds = getContentBounds()
    val originalBitmap =
        Bitmap.createBitmap(
            this,
            contentBounds.left,
            contentBounds.top,
            contentBounds.width(),
            contentBounds.height()
        )
    val scale = other.width.toFloat() / width.toFloat()
    val contentBoundWithoutScale = Rect(
        (contentBounds.left * scale).toInt(),
        (contentBounds.top * scale).toInt(),
        (contentBounds.right * scale).toInt(),
        (contentBounds.bottom * scale).toInt()
    )
    val offset = intArrayOf(0, 0)
    val alphaMask = originalBitmap.extractAlpha(paint, offset)
    val outputRect = Rect(
        offset[0],
        offset[1],
        contentBoundWithoutScale.width(),
        contentBoundWithoutScale.height()
    )
    val outputBitmap =
        Bitmap.createBitmap(outputRect.width(), outputRect.height(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)
    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC)
    canvas.drawBitmap(alphaMask, null, outputRect, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
    canvas.drawBitmap(other, contentBoundWithoutScale, outputRect, paint)
    return outputBitmap
}