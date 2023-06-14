package com.bcl.android.collage_editor.customview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.toRectF
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * @author: Raihan Uddin Piash (raihan.uddin@braincraftapps.com)
 * @project: PHTDTR
 * @date: On 6/1/23 at 12:14 PM
 */
class CenteredBitmapImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private var bitmap: Bitmap? = null
    private var modifiedBitmap: Bitmap? = null
    private var topCroppedBitmap: Bitmap? = null
    private var bottomCroppedBitmap: Bitmap? = null
    private var isDrawing: Boolean = false
    private var isMoving: Boolean = false
    private var pointsList: ArrayList<Pair<Float, Float>> = ArrayList()
    private var path1 = Path()
    private var path2 = Path()
    private var leftStart: Float = 0.0f
    private var topStart: Float = 0.0f
    private val rightPointsList: ArrayList<Pair<Float, Float>> = ArrayList()
    private val leftPointsList: ArrayList<Pair<Float, Float>> = ArrayList()
    private val middlePointsList: ArrayList<Pair<Float, Float>> = ArrayList()
    private var lastRightPointY = 0.0f
    private var lastLeftPointY = 0.0f
    private var topCircleX: Float = 0f
    private var bottomCircleX: Float = 0f
    private var topCircleY: Float = 0f
    private var bottomCircleY: Float = 0f
    private var radius: Float = 30f
    private var topRegionTouch = false
    private var bottomRegionTouch = false
    private var topTranslationX: Float = 0f
    private var topTranslationY: Float = 0f
    private var bottomTranslationX: Float = 0f
    private var bottomTranslationY: Float = 0f
    private var topLastTouchPoint: PointF = PointF()
    private var bottomLastTouchPoint: PointF = PointF()
    private var determinationTouch = 0.0f
    private var deltaX: Float = 0.0f
    private val GAP_BETWEEN: Float = 0f

    //    private var topRegion: Region = Region()
//    private var bottomRegion: Region = Region()
    private var topBounds = RectF()
    private var bottomBounds = RectF()
    private val TAG = "LogcatTag"

    private var path: Path = Path()
    private var paint: Paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private var paint1 = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private var paint2 = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    fun setBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap
        modifiedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        leftStart = width / 2f - modifiedBitmap!!.width / 2f
        topStart = height / 2f - modifiedBitmap!!.height / 2f

        topCircleX = leftStart
        topCircleY = topStart

        bottomCircleX = modifiedBitmap!!.width + leftStart
        bottomCircleY = modifiedBitmap!!.height + topStart
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (modifiedBitmap != null) {

            canvas?.apply {
                save()
                drawBitmap(modifiedBitmap!!, leftStart, topStart, null)
                drawPath(path, paint)

//                drawPath(path1, paint1)
//                drawPath(path2, paint2)

                restore()
            }
        }

        if (topCroppedBitmap != null) {
            canvas?.drawBitmap(topCroppedBitmap!!, topTranslationX, topTranslationY, null)
            canvas?.drawCircle(topBounds.left, topBounds.top, radius, paint1)
        }

        if (bottomCroppedBitmap != null) {
            canvas?.drawBitmap(bottomCroppedBitmap!!, bottomTranslationX, bottomTranslationY, null)
            canvas?.drawCircle(
                bottomBounds.right, bottomBounds.bottom + GAP_BETWEEN, radius, paint2
            )
        }

        canvas?.drawRect(topBounds, paint1)
        canvas?.drawRect(bottomBounds, paint2)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x /*- width / 2f + bitmap!!.width / 2f*/
        val y = event.y /*- height / 2f + bitmap!!.height / 2f*/

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!isDrawing) {
                    isDrawing = true
                    isMoving = true
                    path.reset()
                    path1.reset()
                    path2.reset()
                    pointsList.clear()

                    path.moveTo(x, y)
                    pointsList.add(Pair(x, y))
                    determinationTouch = x
                }

                if (topBounds.contains(x, y)) {
                    Log.d("gjhsdfgge", "top touch")
                    topLastTouchPoint.set(x, y)
                    topRegionTouch = true
                    bottomRegionTouch = false
                }

                if (bottomBounds.contains(x, y)) {
                    Log.d("gjhsdfgge", " bottom touch")
                    bottomLastTouchPoint.set(x, y)
                    topRegionTouch = false
                    bottomRegionTouch = true
                }

                if (isInsideTopCircle(x, y)) {
                    paint1.color = Color.BLACK
//                    invalidate()
                }

                if (isInsideBottomCircle(x, y)) {
                    paint2.color = Color.BLACK
//                    invalidate()
                }
                Log.d("dgsjskgsgsr down", "$topRegionTouch $bottomRegionTouch")

                invalidate()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (isMoving) {
                    path.lineTo(x, y)
                    pointsList.add(Pair(x, y))
                    deltaX = x - determinationTouch
                }

                Log.d("dgsjskgsgsr move", "$topRegionTouch $bottomRegionTouch")
                if (topCroppedBitmap != null && topRegionTouch) {
                    val dx = x - topLastTouchPoint.x
                    val dy = y - topLastTouchPoint.y

                    topBounds.offset(dx, dy)

                    topTranslationX += dx
                    topTranslationY += dy

                    /*val maxTranslationX = leftStart
                    val maxTranslationY = height.toFloat() - topBounds.height() - topStart
                    topTranslationX = newTranslationX.coerceIn(-leftStart, maxTranslationX)
                    topTranslationY = newTranslationY.coerceIn(-topStart, maxTranslationY)*/

                    topLastTouchPoint.set(x, y)

                    paint1.color = Color.RED
//                    invalidate()
                }

                if (bottomCroppedBitmap != null && bottomRegionTouch) {
                    val dx = x - bottomLastTouchPoint.x
                    val dy = y - bottomLastTouchPoint.y

                    bottomBounds.offset(dx, dy)

                    bottomTranslationX += dx
                    bottomTranslationY += dy

                    /*val maxTranslationX = leftStart
                    val maxTranslationY = topStart
                    bottomTranslationX = newTranslationX.coerceIn(-leftStart, maxTranslationX)
                    bottomTranslationY = newTranslationY.coerceIn(
                        -topStart - bottomBounds.height(), maxTranslationY
                    )*/

                    bottomLastTouchPoint.set(x, y)

                    paint2.color = Color.GREEN
//                    invalidate()
                }

                invalidate()
                return true
            }

            MotionEvent.ACTION_UP -> {
                if (isMoving) {
                    path.lineTo(x, y)
                    pointsList.add(Pair(x, y))
                    determinationTouch = x

                    if (deltaX < 0) {
                        cutShape(x, y, false)
                    } else {
                        cutShape(x, y, true)
                    }
                    topCroppedBitmap = cropBitmapByPath(
                        modifiedBitmap!!.copy(modifiedBitmap!!.config, true), path1, true
                    )
                    bottomCroppedBitmap = cropBitmapByPath(
                        modifiedBitmap!!.copy(modifiedBitmap!!.config, true), path2, false
                    )
                }

//                    path.reset()
                topLastTouchPoint = PointF()
                bottomLastTouchPoint = PointF()
                topRegionTouch = false
                bottomRegionTouch = false

                Log.d("dgsjskgsgsr up", "$topRegionTouch $bottomRegionTouch")
//                path1.reset()
//                path2.reset()
                modifiedBitmap = null

                invalidate()
                return true
            }
        }

        return false
    }

    private fun isInsideBottomCircle(x: Float, y: Float): Boolean {
        val dx = x - bottomBounds.right
        val dy = y - bottomBounds.bottom - GAP_BETWEEN
        val distance = sqrt((dx * dx + dy * dy).toDouble())
        return distance <= radius
    }

    private fun isInsideTopCircle(x: Float, y: Float): Boolean {
        val dx = x - topBounds.left
        val dy = y - topBounds.top
        val distance = sqrt((dx * dx + dy * dy).toDouble())
        return distance <= radius
    }

    private fun cropBitmapByPath(
        originalBitmap: Bitmap, path: Path, isTopPath: Boolean
    ): Bitmap {
        val croppedBitmap = Bitmap.createBitmap(
            width, height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(croppedBitmap)
        if (!isTopPath) canvas.translate(0f, GAP_BETWEEN)
        canvas.clipPath(path)
        canvas.drawBitmap(originalBitmap, leftStart, topStart, null)
        return croppedBitmap
    }

    private fun createRegionFromPath(path: Path, rectF: RectF, x: Float, y: Float): Rect {
        var rectTop = rectF.top.toInt()
        path.computeBounds(rectF, true)

        if (path == path2) {
            rectTop = (rectF.top + GAP_BETWEEN).roundToInt()
        }

        val region = Region()
        region.setPath(
            path, Region(
                rectF.left.toInt(), rectTop, rectF.right.toInt(), rectF.bottom.toInt()
            )
        )

        return region.bounds
    }

    private fun cutShape(x: Float, y: Float, leftToRight: Boolean) {
        path1.reset()
        path2.reset()
        leftPointsList.clear()
        rightPointsList.clear()
        middlePointsList.clear()

        lastRightPointY = 0.0f
        lastLeftPointY = 0.0f

        if (leftToRight) {
            for (i in pointsList.indices.reversed()) {
                if (pointsList[i].first >= modifiedBitmap!!.width + leftStart) {
                    Log.d(
                        "esgjsdjgsgter 0 ",
                        pointsList[i].first.toString() + " | " + pointsList[i].second
                    )
                    rightPointsList.add(Pair(pointsList[i].first, pointsList[i].second))
                } else if (pointsList[i].first <= leftStart) {
                    Log.d(
                        "esgjsdjgsgter 2 ",
                        pointsList[i].first.toString() + " | " + pointsList[i].second
                    )
                    leftPointsList.add(Pair(pointsList[i].first, pointsList[i].second))
                } else {
                    Log.d(
                        "esgjsdjgsgter 4 ",
                        pointsList[i].first.toString() + " | " + pointsList[i].second
                    )
                    middlePointsList.add(Pair(pointsList[i].first, pointsList[i].second))
                }
            }
        } else {
            for (i in pointsList.indices) {
                if (pointsList[i].first >= modifiedBitmap!!.width + leftStart) {
                    Log.d(
                        "esgjsdjgsgter 00 ",
                        pointsList[i].first.toString() + " | " + pointsList[i].second
                    )
                    rightPointsList.add(Pair(pointsList[i].first, pointsList[i].second))
                } else if (pointsList[i].first <= leftStart) {
                    Log.d(
                        "esgjsdjgsgter 22 ",
                        pointsList[i].first.toString() + " | " + pointsList[i].second
                    )
                    leftPointsList.add(Pair(pointsList[i].first, pointsList[i].second))
                } else {
                    Log.d(
                        "esgjsdjgsgter 44 ",
                        pointsList[i].first.toString() + " | " + pointsList[i].second
                    )
                    middlePointsList.add(Pair(pointsList[i].first, pointsList[i].second))
                }
            }
        }

        lastLeftPointY = if (leftPointsList.isNotEmpty()) {
            findLastLeftPointY()
        } else {
            middlePointsList[middlePointsList.size - 1].second
        }

        lastRightPointY = if (rightPointsList.isNotEmpty()) {
            findLastRightPointY()
        } else {
            middlePointsList[0].second
        }

        path1.moveTo(leftStart, topStart)
        path1.lineTo(modifiedBitmap!!.width.toFloat() + leftStart, topStart)
        path1.lineTo(modifiedBitmap!!.width.toFloat() + leftStart, lastRightPointY)

        for (i in middlePointsList.indices) {
            path1.lineTo(middlePointsList[i].first, middlePointsList[i].second)
        }

        path1.lineTo(leftStart, lastLeftPointY)
        path1.close();

        path2.moveTo(
            modifiedBitmap!!.width.toFloat() + leftStart,
            modifiedBitmap!!.height.toFloat() + topStart
        )
        path2.lineTo(leftStart, modifiedBitmap!!.height.toFloat() + topStart)
        path2.lineTo(leftStart, lastLeftPointY)

        for (i in middlePointsList.indices.reversed()) {
            path2.lineTo(middlePointsList[i].first, middlePointsList[i].second)
        }
        path2.lineTo(modifiedBitmap!!.width.toFloat() + leftStart, lastRightPointY)
        path2.close()

        isMoving = false

        topBounds = createRegionFromPath(path1, topBounds, x, y).toRectF()
        bottomBounds = createRegionFromPath(path2, bottomBounds, x, y).toRectF()
    }

    private fun findLastLeftPointY(): Float {
        var diff =
            ((middlePointsList[middlePointsList.size - 1].second - leftPointsList[0].second) / (middlePointsList[middlePointsList.size - 1].first - leftPointsList[0].first))
        diff *= (leftStart - leftPointsList[0].first)
        return diff + leftPointsList[0].second
    }

    private fun findLastDownPointX(): Float {
        var diff =
            ((middlePointsList[middlePointsList.size - 1].first - leftPointsList[0].first) / (middlePointsList[middlePointsList.size - 1].second - leftPointsList[0].second))
        diff *= (topStart - leftPointsList[0].first)
        return diff + leftPointsList[0].first
    }

    private fun findLastRightPointY(): Float {
        var diff =
            ((rightPointsList[rightPointsList.size - 1].second - middlePointsList[0].second) / (rightPointsList[rightPointsList.size - 1].first - middlePointsList[0].first))
        diff *= ((modifiedBitmap!!.width + leftStart) - middlePointsList[0].first)
        return diff + middlePointsList[0].second
    }

    private fun findLastUpPointX(): Float {
        var diff =
            ((rightPointsList[rightPointsList.size - 1].first - middlePointsList[0].first) / (rightPointsList[rightPointsList.size - 1].second - middlePointsList[0].second))
        diff *= ((modifiedBitmap!!.height + topStart) - middlePointsList[0].second)
        return diff + middlePointsList[0].first
    }

    fun reset() {
//        path.reset()
        paint1.color = Color.RED
        paint2.color = Color.GREEN
        path1.reset()
        path2.reset()
        topBounds = RectF()
        bottomBounds = RectF()
//        topRegion = Region()
//        bottomRegion = Region()
        topCroppedBitmap = null
        bottomCroppedBitmap = null
        topLastTouchPoint = PointF()
        bottomLastTouchPoint = PointF()
        topTranslationX = 0f
        topTranslationY = 0f
        bottomTranslationX = 0f
        bottomTranslationY = 0f
        isDrawing = false
        setBitmap(bitmap!!)
        Toast.makeText(context, "Reset to Original", Toast.LENGTH_SHORT).show()
        invalidate()
    }
}

