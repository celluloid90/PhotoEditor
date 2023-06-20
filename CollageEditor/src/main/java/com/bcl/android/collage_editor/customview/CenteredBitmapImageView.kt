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
    private var lastRightPointY1 = 0.0f
    private var lastLeftPointY = 0.0f
    private var lastLeftPointY1 = 0.0f
    private var lastTopPointX = 0.0f
    private var lastTopPointX1 = 0.0f
    private var lastBottomPointX = 0.0f
    private var lastBottomPointX1 = 0.0f
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
    private val GAP_BETWEEN: Float = 20f
    private var initX: Float = 0f
    private var initY: Float = 0f
    private var moveLeft: Boolean = false
    private var moveRight: Boolean = false
    private var moveTop: Boolean = false
    private var moveDown: Boolean = false
    private var abovePointList: ArrayList<Pair<Float, Float>> = ArrayList()
    private var belowPointList: ArrayList<Pair<Float, Float>> = ArrayList()

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
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 20f
        isAntiAlias = true
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private var paint2 = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 20f
        isAntiAlias = true
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    init {
        setBackgroundColor(Color.WHITE)
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

                restore()
            }
        }

        if (topCroppedBitmap != null) {
            canvas?.drawBitmap(topCroppedBitmap!!, topTranslationX, topTranslationY, null)
//            canvas?.drawRect(topBounds, paint1)
            canvas?.drawCircle(topBounds.left, topBounds.top, radius, paint1)
        }

        if (bottomCroppedBitmap != null) {
            canvas?.drawBitmap(bottomCroppedBitmap!!, bottomTranslationX, bottomTranslationY, null)
//            canvas?.drawRect(bottomBounds, paint2)
            canvas?.drawCircle(
                bottomBounds.right, bottomBounds.bottom + GAP_BETWEEN, radius, paint2
            )
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!isDrawing) {
                    paint.color = Color.BLACK
                    paint.strokeWidth = 10f

                    initX = x
                    initY = y
                    isDrawing = true
                    isMoving = true
                    path.reset()
                    path1.reset()
                    path2.reset()
                    pointsList.clear()

                    path.moveTo(x, y)
                    pointsList.add(Pair(x, y))
                    determinationTouch = x

                    moveLeft = false
                    moveTop = false
                    moveRight = false
                    moveDown = false
                }

                if (topBounds.contains(x, y)) {
                    topLastTouchPoint.set(x, y)
                    topRegionTouch = true
                    bottomRegionTouch = false
                }

                if (bottomBounds.contains(x, y)) {
                    bottomLastTouchPoint.set(x, y)
                    topRegionTouch = false
                    bottomRegionTouch = true
                }

                if (isInsideTopCircle(x, y)) {
                    topLastTouchPoint.set(x, y)
                    topRegionTouch = true
                    bottomRegionTouch = false
                }

                if (isInsideBottomCircle(x, y)) {
                    bottomLastTouchPoint.set(x, y)
                    topRegionTouch = false
                    bottomRegionTouch = true
                }

                invalidate()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (isMoving) {
                    path.lineTo(x, y)
                    pointsList.add(Pair(x, y))
                    deltaX = x - determinationTouch

                    if (x < leftStart) {
                        moveLeft = true
                    }

                    if (y > modifiedBitmap!!.height + topStart) {
                        moveDown = true
                    }

                    if (x > modifiedBitmap!!.width + leftStart) {
                        moveRight = true
                    }

                    if (y < topStart) {
                        moveTop = true
                    }
                }

                if (topCroppedBitmap != null && topRegionTouch) {
                    val dx = x - topLastTouchPoint.x
                    val dy = y - topLastTouchPoint.y

                    topBounds.offset(dx, dy)

                    topTranslationX += dx
                    topTranslationY += dy

                    topLastTouchPoint.set(x, y)

                    paint1.color = Color.RED
                }

                if (bottomCroppedBitmap != null && bottomRegionTouch) {
                    val dx = x - bottomLastTouchPoint.x
                    val dy = y - bottomLastTouchPoint.y

                    bottomBounds.offset(dx, dy)

                    bottomTranslationX += dx
                    bottomTranslationY += dy

                    bottomLastTouchPoint.set(x, y)

                    paint2.color = Color.GREEN
                }

                invalidate()
                return true
            }

            MotionEvent.ACTION_UP -> {
                if (isMoving) {
                    path.lineTo(x, y)
                    pointsList.add(Pair(x, y))
                    determinationTouch = x

                    cutShape(x, y, deltaX < 0)
                    topCroppedBitmap = cropBitmapByPath(
                        modifiedBitmap!!.copy(modifiedBitmap!!.config, true), path1, true
                    )
                    bottomCroppedBitmap = cropBitmapByPath(
                        modifiedBitmap!!.copy(modifiedBitmap!!.config, true), path2, false
                    )
                }

                topLastTouchPoint = PointF()
                bottomLastTouchPoint = PointF()
                topRegionTouch = false
                bottomRegionTouch = false

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

    private fun createRegionFromPath(path: Path, rectF: RectF): Rect {
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

    private fun cutShape(x: Float, y: Float, rightToLeft: Boolean) {
        var totalBound = RectF()
        path1.reset()
        path2.reset()
        leftPointsList.clear()
        rightPointsList.clear()
        middlePointsList.clear()

        lastRightPointY = 0.0f
        lastRightPointY1 = 0.0f
        lastLeftPointY = 0.0f
        lastLeftPointY1 = 0.0f
        lastTopPointX = 0.0f
        lastTopPointX1 = 0.0f
        lastBottomPointX = 0.0f
        lastBottomPointX1 = 0.0f

        val path = Path()
        path.moveTo(leftStart, topStart)
        path.lineTo(modifiedBitmap!!.width + leftStart, topStart)
        path.lineTo(modifiedBitmap!!.width + leftStart, modifiedBitmap!!.height + topStart)
        path.lineTo(leftStart, modifiedBitmap!!.height + topStart)
        path.close()

        totalBound = createRegionFromPath(path, totalBound).toRectF()

        if (!rightToLeft) {
            Log.d(TAG, "cutShape: 1 $rightToLeft")
            for (i in pointsList.indices.reversed()) {
                if (totalBound.contains(pointsList[i].first, pointsList[i].second)) {
                    middlePointsList.add(Pair(pointsList[i].first, pointsList[i].second))
                } else if (pointsList[i].first >= modifiedBitmap!!.width + leftStart) {
                    rightPointsList.add(Pair(pointsList[i].first, pointsList[i].second))
                } else if (pointsList[i].second <= topStart) {
                    rightPointsList.add(Pair(pointsList[i].first, pointsList[i].second))
                } else if (pointsList[i].first <= leftStart) {
                    leftPointsList.add(Pair(pointsList[i].first, pointsList[i].second))
                } else if (pointsList[i].second >= modifiedBitmap!!.height + topStart) {
                    leftPointsList.add(Pair(pointsList[i].first, pointsList[i].second))
                }
            }
        } else {
            Log.d(TAG, "cutShape: 2 $rightToLeft")
            for (i in pointsList.indices) {
                if (totalBound.contains(pointsList[i].first, pointsList[i].second)) {
                    middlePointsList.add(Pair(pointsList[i].first, pointsList[i].second))
                } else if (pointsList[i].first >= modifiedBitmap!!.width + leftStart) {
                    rightPointsList.add(Pair(pointsList[i].first, pointsList[i].second))
                } else if (pointsList[i].second <= topStart) {
                    rightPointsList.add(Pair(pointsList[i].first, pointsList[i].second))
                } else if (pointsList[i].first <= leftStart) {
                    leftPointsList.add(Pair(pointsList[i].first, pointsList[i].second))
                } else if (pointsList[i].second >= modifiedBitmap!!.height + topStart) {
                    leftPointsList.add(Pair(pointsList[i].first, pointsList[i].second))
                }
            }
        }

        Log.d(TAG, "cutShape: left $leftPointsList")
        Log.d(TAG, "cutShape: middle $middlePointsList")
        Log.d(TAG, "cutShape: right $rightPointsList")

        /* if (leftPointsList.isNotEmpty()) {
             lastLeftPointY = findLastLeftPointY()
             lastLeftPointY1 = findLastLeftPointY1()
             lastBottomPointX = findLastBottomPointX()
             lastBottomPointX1 = findLastBottomPointX1()
         } else {
             lastLeftPointY = middlePointsList[middlePointsList.size - 1].second
             lastLeftPointY1 = middlePointsList[0].second
             lastBottomPointX = middlePointsList[middlePointsList.size - 1].first
             lastBottomPointX1 = middlePointsList[0].first
         }

         if (rightPointsList.isNotEmpty()) {
             lastRightPointY = findLastRightPointY()
             lastRightPointY1 = findLastRightPointY1()
             lastTopPointX = findLastTopPointX()
             lastTopPointX1 = findLastTopPointX1()
         } else {
             lastRightPointY = middlePointsList[0].second
             lastRightPointY1 = middlePointsList[middlePointsList.size - 1].second
             lastTopPointX = middlePointsList[0].first
             lastTopPointX1 = middlePointsList[middlePointsList.size - 1].first
         }*/

        calculatePath()

        isMoving = false
    }

    private fun calculatePath() {
        abovePointList = ArrayList()
        belowPointList = ArrayList()

        if (moveLeft && !moveRight && !moveTop && !moveDown) {
            Log.d(TAG, "calculateSlope: left to left")
            updateLeftLeftPathPointsList()
//            left to left
        } else if (!moveLeft && moveRight && !moveTop && !moveDown) {
            Log.d(TAG, "calculateSlope: right to right")
            updateRightRightPathPointsList()
//            right to right
        } else if (!moveLeft && !moveRight && moveTop && !moveDown) {
            Log.d(TAG, "calculateSlope: top to top")
            updateTopTopPathPointsList()
//            top to top
        } else if (!moveLeft && !moveRight && !moveTop && moveDown) {
            Log.d(TAG, "calculateSlope: down to down")
            updateDownDownPathPointsList()
//            down to down
        } else if (moveLeft && moveRight) {
            Log.d(TAG, "calculateSlope: left & right")
            updateLeftRightPathPointsList()
//            left & right
        } else if (moveTop && moveDown) {
            Log.d(TAG, "calculateSlope: top & down")
            updateTopDownPathPointsList()
//            top & down
        } else if (moveLeft && moveTop) {
            Log.d(TAG, "calculateSlope: left & top")
            updateLeftTopPathPointsList()
//            left & top
        } else if (moveLeft && moveDown) {
            Log.d(TAG, "calculateSlope: left & down")
            updateLeftDownPathPointsList()
//            left & down
        } else if (moveRight && moveTop) {
            Log.d(TAG, "calculateSlope: right & top")
            updateRightTopPathPointsList()
//            right & top
        } else if (moveRight && moveDown) {
            Log.d(TAG, "calculateSlope: right & down")
            updateRightDownPathPointsList()
//            right & down
        } else {
            updateOnlyMiddlePathPointsList()
        }

        for (point in abovePointList.indices) {
            if (point == 0) {
                path1.moveTo(abovePointList[point].first, abovePointList[point].second)
            } else {
                path1.lineTo(abovePointList[point].first, abovePointList[point].second)
            }
        }
        path1.close()

        for (point in belowPointList.indices) {
            if (point == 0) {
                path2.moveTo(belowPointList[point].first, belowPointList[point].second)
            } else {
                path2.lineTo(belowPointList[point].first, belowPointList[point].second)
            }
        }
        path2.close()

        topBounds = createRegionFromPath(path1, RectF()).toRectF()
        bottomBounds = createRegionFromPath(path2, RectF()).toRectF()
    }

    private fun updateOnlyMiddlePathPointsList() {
        abovePointList.add(Pair(leftStart, middlePointsList[middlePointsList.size - 1].second))
        abovePointList.add(Pair(leftStart, topStart))
        abovePointList.add(Pair(modifiedBitmap!!.width + leftStart, topStart))
        abovePointList.add(Pair(modifiedBitmap!!.width + leftStart, middlePointsList[0].second))
        abovePointList.addAll(middlePointsList)

        belowPointList.add(Pair(leftStart, middlePointsList[middlePointsList.size - 1].second))
        belowPointList.add(Pair(leftStart, modifiedBitmap!!.height + topStart))
        belowPointList.add(
            Pair(
                modifiedBitmap!!.width + leftStart, modifiedBitmap!!.height + topStart
            )
        )
        belowPointList.add(Pair(modifiedBitmap!!.width + leftStart, middlePointsList[0].second))
        belowPointList.addAll(middlePointsList)
    }

    private fun updateDownDownPathPointsList() {
        if (leftPointsList.isNotEmpty()) {
            lastBottomPointX = findLastBottomPointX()
            lastBottomPointX1 = findLastBottomPointX1()
        } else {
            lastBottomPointX = middlePointsList[middlePointsList.size - 1].first
            lastBottomPointX1 = middlePointsList[0].first
        }

        abovePointList.add(Pair(lastBottomPointX, modifiedBitmap!!.height + topStart))
        abovePointList.addAll(middlePointsList)
        abovePointList.add(Pair(lastBottomPointX1, modifiedBitmap!!.height + topStart))

        belowPointList.add(Pair(leftStart, modifiedBitmap!!.height + topStart))
        belowPointList.add(Pair(lastBottomPointX, modifiedBitmap!!.height + topStart))
        belowPointList.addAll(middlePointsList)
        belowPointList.add(Pair(lastBottomPointX1, modifiedBitmap!!.height + topStart))
        belowPointList.add(
            Pair(
                modifiedBitmap!!.width + leftStart, modifiedBitmap!!.height + topStart
            )
        )
        belowPointList.add(Pair(modifiedBitmap!!.width + leftStart, topStart))
        belowPointList.add(Pair(leftStart, topStart))
    }

    private fun updateTopTopPathPointsList() {
        if (rightPointsList.isNotEmpty()) {
            lastTopPointX = findLastTopPointX()
            lastTopPointX1 = findLastTopPointX1()
        } else {
            lastTopPointX = middlePointsList[0].first
            lastTopPointX1 = middlePointsList[middlePointsList.size - 1].first
        }

        abovePointList.add(Pair(lastTopPointX, topStart))
        abovePointList.addAll(middlePointsList)
        abovePointList.add(Pair(lastTopPointX1, topStart))

        belowPointList.add(Pair(leftStart, topStart))
        belowPointList.add(Pair(lastTopPointX, topStart))
        belowPointList.addAll(middlePointsList)
        belowPointList.add(Pair(lastTopPointX1, topStart))
        belowPointList.add(Pair(modifiedBitmap!!.width + leftStart, topStart))
        belowPointList.add(
            Pair(
                modifiedBitmap!!.width + leftStart, modifiedBitmap!!.height + topStart
            )
        )
        belowPointList.add(Pair(leftStart, modifiedBitmap!!.height + topStart))
    }

    private fun updateRightRightPathPointsList() {
        if (rightPointsList.isNotEmpty()) {
            lastRightPointY = findLastRightPointY()
            lastRightPointY1 = findLastRightPointY1()
        } else {
            lastRightPointY = middlePointsList[0].second
            lastRightPointY1 = middlePointsList[middlePointsList.size - 1].second
        }

        abovePointList.add(Pair(modifiedBitmap!!.width + leftStart, lastRightPointY))
        abovePointList.addAll(middlePointsList)
        abovePointList.add(Pair(modifiedBitmap!!.width + leftStart, lastRightPointY1))

        belowPointList.add(
            Pair(
                modifiedBitmap!!.width + leftStart, modifiedBitmap!!.height + topStart
            )
        )
        belowPointList.add(Pair(leftStart, modifiedBitmap!!.height + topStart))
        belowPointList.add(Pair(leftStart, topStart))
        belowPointList.add(Pair(modifiedBitmap!!.width + leftStart, topStart))
        belowPointList.add(Pair(modifiedBitmap!!.width + leftStart, lastRightPointY))
        belowPointList.addAll(middlePointsList)
        belowPointList.add(Pair(modifiedBitmap!!.width + leftStart, lastRightPointY1))
    }

    private fun updateLeftLeftPathPointsList() {
        if (leftPointsList.isNotEmpty()) {
            lastLeftPointY = findLastLeftPointY()
            lastLeftPointY1 = findLastLeftPointY1()
        } else {
            lastLeftPointY = middlePointsList[middlePointsList.size - 1].second
            lastLeftPointY1 = middlePointsList[0].second
        }

        abovePointList.add(Pair(leftStart, lastLeftPointY))
        abovePointList.addAll(middlePointsList)
        abovePointList.add(Pair(leftStart, lastLeftPointY1))

        belowPointList.add(Pair(leftStart, topStart))
        belowPointList.add(Pair(leftStart, lastLeftPointY))
        belowPointList.addAll(middlePointsList)
        belowPointList.add(Pair(leftStart, lastLeftPointY1))
        belowPointList.add(Pair(leftStart, modifiedBitmap!!.height + topStart))
        belowPointList.add(
            Pair(
                modifiedBitmap!!.width + leftStart, modifiedBitmap!!.height + topStart
            )
        )
        belowPointList.add(Pair(modifiedBitmap!!.width + leftStart, topStart))
    }

    private fun updateRightDownPathPointsList() {
        lastBottomPointX = if (leftPointsList.isNotEmpty()) {
            findLastBottomPointX()
        } else {
            middlePointsList[middlePointsList.size - 1].first
        }

        lastRightPointY = if (rightPointsList.isNotEmpty()) {
            findLastRightPointY()
        } else {
            middlePointsList[0].second
        }

        abovePointList.add(
            Pair(
                modifiedBitmap!!.width + leftStart, modifiedBitmap!!.height + topStart
            )
        )
        abovePointList.add(Pair(lastBottomPointX, modifiedBitmap!!.height + topStart))
        abovePointList.addAll(middlePointsList.reversed())
        abovePointList.add(Pair(modifiedBitmap!!.width + leftStart, lastRightPointY))

        belowPointList.add(Pair(leftStart, topStart))
        belowPointList.add(Pair(modifiedBitmap!!.width + leftStart, topStart))
        belowPointList.add(Pair(modifiedBitmap!!.width + leftStart, lastRightPointY))
        belowPointList.addAll(middlePointsList)
        belowPointList.add(Pair(lastBottomPointX, modifiedBitmap!!.height + topStart))
        belowPointList.add(Pair(leftStart, modifiedBitmap!!.height + topStart))
    }

    private fun updateRightTopPathPointsList() {
        if (rightPointsList.isNotEmpty()) {
            lastRightPointY = findLastRightPointY()
            lastTopPointX = findRightTopPointX()
        } else {
            lastRightPointY = middlePointsList[0].second
            lastTopPointX = middlePointsList[0].first
        }

        abovePointList.add(Pair(modifiedBitmap!!.width + leftStart, topStart))
        abovePointList.add(Pair(modifiedBitmap!!.width + leftStart, lastRightPointY))
        abovePointList.addAll(middlePointsList)
        abovePointList.add(Pair(lastTopPointX, topStart))

        belowPointList.add(
            Pair(
                modifiedBitmap!!.width + leftStart, modifiedBitmap!!.height + topStart
            )
        )
        belowPointList.add(Pair(leftStart, modifiedBitmap!!.height + topStart))
        belowPointList.add(Pair(leftStart, topStart))
        belowPointList.add(Pair(lastTopPointX, topStart))
        belowPointList.addAll(middlePointsList.reversed())
        belowPointList.add(Pair(modifiedBitmap!!.width + leftStart, lastRightPointY))
    }

    private fun updateLeftDownPathPointsList() {
        if (leftPointsList.isNotEmpty()) {
            lastLeftPointY = findLastLeftPointY()
            lastBottomPointX = findLastBottomPointX()
        } else {
            lastLeftPointY = middlePointsList[middlePointsList.size - 1].second
            lastBottomPointX = middlePointsList[middlePointsList.size - 1].first
        }

        abovePointList.add(Pair(leftStart, modifiedBitmap!!.height + topStart))
        abovePointList.add(Pair(leftStart, lastLeftPointY))
        abovePointList.addAll(middlePointsList.reversed())
        abovePointList.add(Pair(lastBottomPointX, modifiedBitmap!!.height + topStart))

        belowPointList.add(
            Pair(
                modifiedBitmap!!.width + leftStart, modifiedBitmap!!.height + topStart
            )
        )
        belowPointList.add(Pair(lastBottomPointX, modifiedBitmap!!.height + topStart))
        belowPointList.addAll(middlePointsList)
        belowPointList.add(Pair(leftStart, lastLeftPointY))
        belowPointList.add(Pair(leftStart, topStart))
        belowPointList.add(Pair(modifiedBitmap!!.width + leftStart, topStart))
    }

    private fun updateLeftTopPathPointsList() {
        lastLeftPointY = if (leftPointsList.isNotEmpty()) {
            findLastLeftPointY()
        } else {
            middlePointsList[middlePointsList.size - 1].second
        }

        lastTopPointX = if (rightPointsList.isNotEmpty()) {
            findLeftTopPointX()
        } else {
            middlePointsList[0].first
        }

        abovePointList.add(Pair(leftStart, topStart))
        abovePointList.add(Pair(lastTopPointX, topStart))
        abovePointList.addAll(middlePointsList)
        abovePointList.add(Pair(leftStart, lastLeftPointY))

        belowPointList.add(
            Pair(
                modifiedBitmap!!.width + leftStart, modifiedBitmap!!.height + topStart
            )
        )
        belowPointList.add(Pair(leftStart, modifiedBitmap!!.height + topStart))
        belowPointList.add(Pair(leftStart, lastLeftPointY))
        belowPointList.addAll(middlePointsList.reversed())
        belowPointList.add(Pair(lastTopPointX, topStart))
        belowPointList.add(Pair(modifiedBitmap!!.width + leftStart, topStart))
    }

    private fun updateTopDownPathPointsList() {
        lastBottomPointX = if (leftPointsList.isNotEmpty()) {
            findLastBottomPointX()
        } else {
            middlePointsList[middlePointsList.size - 1].first
        }

        lastTopPointX = if (rightPointsList.isNotEmpty()) {
            findLeftTopPointX()
        } else {
            middlePointsList[middlePointsList.size - 1].first
        }

        abovePointList.add(Pair(leftStart, topStart))
        abovePointList.add(Pair(lastTopPointX, topStart))
        abovePointList.addAll(middlePointsList)
        abovePointList.add(Pair(lastBottomPointX, modifiedBitmap!!.height + topStart))
        abovePointList.add(
            Pair(leftStart, modifiedBitmap!!.height + topStart)
        )

        belowPointList.add(
            Pair(
                modifiedBitmap!!.width + leftStart, modifiedBitmap!!.height + topStart
            )
        )
        belowPointList.add(Pair(lastBottomPointX, modifiedBitmap!!.height + topStart))
        belowPointList.addAll(middlePointsList.reversed())
        belowPointList.add(Pair(lastTopPointX, topStart))
        belowPointList.add(Pair(modifiedBitmap!!.width + leftStart, topStart))
    }

    private fun updateLeftRightPathPointsList() {
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

        abovePointList.add(Pair(leftStart, topStart))
        abovePointList.add(Pair(modifiedBitmap!!.width + leftStart, topStart))
        abovePointList.add(Pair(modifiedBitmap!!.width + leftStart, lastRightPointY))
        abovePointList.addAll(middlePointsList)
        abovePointList.add(Pair(leftStart, lastLeftPointY))
        belowPointList.add(
            Pair(
                modifiedBitmap!!.width + leftStart, modifiedBitmap!!.height + topStart
            )
        )
        belowPointList.add(Pair(leftStart, modifiedBitmap!!.height + topStart))
        belowPointList.add(Pair(leftStart, lastLeftPointY))
        belowPointList.addAll(middlePointsList.reversed())
        belowPointList.add(Pair(modifiedBitmap!!.width + leftStart, lastRightPointY))
    }

    private fun findLastTopPointX1(): Float {
        var diff =
            ((rightPointsList[0].first - middlePointsList[middlePointsList.size - 1].first) / (rightPointsList[0].second - middlePointsList[middlePointsList.size - 1].second))
        diff *= (topStart - middlePointsList[middlePointsList.size - 1].second)
        return diff + middlePointsList[middlePointsList.size - 1].first
    }

    private fun findLastRightPointY1(): Float {
        var diff =
            ((rightPointsList[0].second - middlePointsList[middlePointsList.size - 1].second) / (rightPointsList[0].first - middlePointsList[middlePointsList.size - 1].first))
        diff *= ((modifiedBitmap!!.width + leftStart) - middlePointsList[middlePointsList.size - 1].first)
        return diff + middlePointsList[middlePointsList.size - 1].second
    }

    private fun findLastBottomPointX1(): Float {
        var diff =
            ((middlePointsList[0].first - leftPointsList[leftPointsList.size - 1].first) / (middlePointsList[0].second - leftPointsList[leftPointsList.size - 1].second))
        diff *= ((modifiedBitmap!!.height + topStart) - leftPointsList[leftPointsList.size - 1].second)
        return diff + leftPointsList[leftPointsList.size - 1].first
    }

    private fun findLastLeftPointY1(): Float {
        var diff =
            ((middlePointsList[0].second - leftPointsList[leftPointsList.size - 1].second) / (middlePointsList[0].first - leftPointsList[leftPointsList.size - 1].first))
        diff *= (leftStart - leftPointsList[leftPointsList.size - 1].first)
        return diff + leftPointsList[leftPointsList.size - 1].second
    }

    private fun findLastLeftPointY(): Float {
        var diff =
            ((middlePointsList[middlePointsList.size - 1].second - leftPointsList[0].second) / (middlePointsList[middlePointsList.size - 1].first - leftPointsList[0].first))
        diff *= (leftStart - leftPointsList[0].first)
        return diff + leftPointsList[0].second
    }

    private fun findLastBottomPointX(): Float {
        var diff =
            ((middlePointsList[middlePointsList.size - 1].first - leftPointsList[0].first) / (middlePointsList[middlePointsList.size - 1].second - leftPointsList[0].second))
        diff *= ((modifiedBitmap!!.height + topStart) - leftPointsList[0].second)
        return diff + leftPointsList[0].first
    }

    private fun findLastRightPointY(): Float {
        var diff =
            ((rightPointsList[rightPointsList.size - 1].second - middlePointsList[0].second) / (rightPointsList[rightPointsList.size - 1].first - middlePointsList[0].first))
        diff *= ((modifiedBitmap!!.width + leftStart) - middlePointsList[0].first)
        return diff + middlePointsList[0].second
    }

    private fun findLastTopPointX(): Float {
        var diff =
            ((rightPointsList[0].first - middlePointsList[middlePointsList.size - 1].first) / (rightPointsList[0].second - middlePointsList[middlePointsList.size - 1].second))
        diff *= (topStart - middlePointsList[middlePointsList.size - 1].second)
        return diff + middlePointsList[middlePointsList.size - 1].first
    }

    private fun findRightTopPointX(): Float {
        var diff =
            ((rightPointsList[0].first - middlePointsList[middlePointsList.size - 1].first) / (rightPointsList[0].second - middlePointsList[middlePointsList.size - 1].second))
        diff *= (topStart - middlePointsList[middlePointsList.size - 1].second)
        return diff + middlePointsList[middlePointsList.size - 1].first
    }

    private fun findLeftTopPointX(): Float {
        var diff =
            ((rightPointsList[rightPointsList.size - 1].first - middlePointsList[0].first) / (rightPointsList[rightPointsList.size - 1].second - middlePointsList[0].second))
        diff *= (topStart - middlePointsList[0].second)
        return diff + middlePointsList[0].first
    }

    fun reset() {
        paint.color = Color.WHITE
        paint.strokeWidth = 5f
        path1.reset()
        path2.reset()
        topBounds = RectF()
        bottomBounds = RectF()
        topCroppedBitmap = null
        bottomCroppedBitmap = null
        topLastTouchPoint = PointF()
        bottomLastTouchPoint = PointF()
        topTranslationX = 0f
        topTranslationY = 0f
        bottomTranslationX = 0f
        bottomTranslationY = 0f
        isDrawing = false

        lastRightPointY = 0.0f
        lastRightPointY1 = 0.0f
        lastLeftPointY = 0.0f
        lastLeftPointY1 = 0.0f
        lastTopPointX = 0.0f
        lastTopPointX1 = 0.0f
        lastBottomPointX = 0.0f
        lastBottomPointX1 = 0.0f

        moveLeft = false
        moveRight = false
        moveTop = false
        moveDown = false

        setBitmap(bitmap!!)
        Toast.makeText(context, "Reset to Original", Toast.LENGTH_SHORT).show()
        invalidate()
    }
}

