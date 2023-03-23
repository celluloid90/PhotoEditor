package com.bcl.android.collage_editor.customview

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import kotlin.math.max


/**
 * Created by Raihan Uddin Piash on ১৬/৩/২৩

 * Copyright (c) 2023 Brain Craft LTD.
 **/
class CustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var path1: Path
    var path2: Path
    val paint1: Paint
    val paint2: Paint
    var scaleDetector: ScaleGestureDetector
    var scaleFactor: Float = 1f
    var gapBetweenFrames = 0.01f;
    var oldWidth: Int = 0
    var oldHeight: Int = 0
    var corEffect = CornerPathEffect(0f)
    private var uriLists: ArrayList<Uri> = ArrayList()
    private var bitmapLists: ArrayList<Bitmap> = ArrayList()
    private var scaleDetectorList: ArrayList<ScaleGestureDetector> = ArrayList()
    private var gestureDetectorList: ArrayList<GestureDetector> = ArrayList()
    private var mMatrix: Matrix = Matrix()
    private var matrixList: ArrayList<Matrix> = ArrayList()
    private lateinit var region: Region
    private var regionList: ArrayList<Region> = ArrayList()
    private var pointerDown: Boolean = false
    private var pointerRegionIndex: Int = -1
    private var pathLists: ArrayList<Path> = ArrayList()

    init {
        path1 = Path()
        path2 = Path()

        pathLists.add(path1)
        pathLists.add(path2)

        paint1 = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        paint1.style = Paint.Style.FILL

        paint2 = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        paint2.style = Paint.Style.FILL

        corEffect = CornerPathEffect(0.0f)
        paint1.pathEffect = corEffect
        paint2.pathEffect = corEffect

        scaleDetector = ScaleGestureDetector(context, object : OnScaleGestureListener {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f))

                invalidate()
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                TODO("Not yet implemented")
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                TODO("Not yet implemented")
            }

        })

        for (i in pathLists.indices) {
            val sDetector = ScaleGestureDetector(context, object : SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val factor = detector.scaleFactor
                    matrixList[i].postScale(factor, factor, width / 2f, height / 2f)
                    ViewCompat.postInvalidateOnAnimation(this@CustomView)
                    return true
                }
            })

            val gDetector =
                GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                    override fun onScroll(
                        e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float
                    ): Boolean {
                        matrixList[i].postTranslate(-distanceX, -distanceY)
                        ViewCompat.postInvalidateOnAnimation(this@CustomView)
                        return true
                    }
                })

            scaleDetectorList.add(sDetector)
            gestureDetectorList.add(gDetector)
        }
    }

    fun updateZoomScale(scale: Float) {
        scaleFactor = scale;
        invalidate()
    }

    fun updateFrameGap(frameGap: Float) {
        gapBetweenFrames = frameGap

        path1.reset()
        path2.reset()

        path1.moveTo((0f + gapBetweenFrames) * oldWidth, (0f + gapBetweenFrames) * oldHeight)
        path1.lineTo(
            (0.3f - gapBetweenFrames) * oldWidth, (0f + gapBetweenFrames) * oldHeight
        )
        path1.lineTo(
            (0.7f - gapBetweenFrames) * oldWidth, (1f - gapBetweenFrames) * oldHeight
        )
        path1.lineTo((0f + gapBetweenFrames) * oldWidth, (1f - gapBetweenFrames) * oldHeight)
        path1.close()

        path2.moveTo(
            (0.3f + gapBetweenFrames) * oldWidth, (0f + gapBetweenFrames) * oldHeight
        )
        path2.lineTo((1f - gapBetweenFrames) * oldWidth, (0f + gapBetweenFrames) * oldHeight)
        path2.lineTo((1f - gapBetweenFrames) * oldWidth, (1f - gapBetweenFrames) * oldHeight)
        path2.lineTo(
            (0.70f + gapBetweenFrames) * oldWidth, (1f - gapBetweenFrames) * oldHeight
        )
        path2.close()

        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        oldWidth = w
        oldHeight = h

        path1.reset()
        path2.reset()

        path1.moveTo((0f + gapBetweenFrames) * w, (0f + gapBetweenFrames) * h)
        path1.lineTo((0.3f - gapBetweenFrames) * w, (0f + gapBetweenFrames) * h)
        path1.lineTo((0.7f - gapBetweenFrames) * w, (1f - gapBetweenFrames) * h)
        path1.lineTo((0f + gapBetweenFrames) * w, (1f - gapBetweenFrames) * h)
        path1.close()

        path2.moveTo((0.3f + gapBetweenFrames) * w, (0f + gapBetweenFrames) * h)
        path2.lineTo((1f - gapBetweenFrames) * w, (0f + gapBetweenFrames) * h)
        path2.lineTo((1f - gapBetweenFrames) * w, (1f - gapBetweenFrames) * h)
        path2.lineTo((0.70f + gapBetweenFrames) * w, (1f - gapBetweenFrames) * h)
        path2.close()

        for (i in pathLists.indices) {
            val rectF = RectF()
            pathLists[i].computeBounds(rectF, true)
            region = Region()
            region.setPath(
                pathLists[i], Region(
                    rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt()
                )
            )
            regionList.add(region)

            val scale: Float = max(w / (width / 2f), h / (height / 2f))
            mMatrix = Matrix()
            mMatrix.setScale(scale, scale)
            mMatrix.postTranslate((w - scale * (width / 2f)) / 2f, (h - scale * (height / 2f)) / 2f)
            matrixList.add(mMatrix)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        canvas?.scale(scaleFactor, scaleFactor, width / 2f, height / 2f)

        paint1.pathEffect = corEffect
        paint2.pathEffect = corEffect

        canvas?.save()
        for (i in pathLists.indices) {
            canvas?.clipPath(pathLists[i])
            canvas?.drawBitmap(bitmapLists[i], matrixList[i], paint1)
            canvas?.restore()
            canvas?.save()
        }


//        canvas?.clipPath(path2)
//        canvas?.drawBitmap(bitmapLists[1], Matrix(), paint2)

        canvas?.restore()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        /*event?.let { mGestureDetector!!.onTouchEvent(it) }
        event?.let { mScaleDetector!!.onTouchEvent(it) }*/

        val point = Point()
        point.x = event!!.x.toInt()
        point.y = event.y.toInt()

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                for (i in regionList.indices) {
                    if (regionList[i].contains(point.x, point.y)) {
                        pointerDown = true
                        pointerRegionIndex = i
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
            }

            MotionEvent.ACTION_UP -> {
                pointerDown = false
            }
        }

        if (pointerDown || regionList[pointerRegionIndex].contains(point.x, point.y)) {
            event.let { gestureDetectorList[pointerRegionIndex].onTouchEvent(it) }
            event.let { scaleDetectorList[pointerRegionIndex].onTouchEvent(it) }
        }

        return true
    }

    fun updateEdgeSmooth(edgeGapValue: Float) {
        corEffect = CornerPathEffect(edgeGapValue)
        invalidate()
    }

    fun setData(uriLists: ArrayList<Uri>) {
        bitmapLists.clear()
        this.uriLists.clear()
        this.uriLists.addAll(uriLists)

        for (uri in this.uriLists) {
            bitmapLists.add(MediaStore.Images.Media.getBitmap(context.contentResolver, uri))
        }
        invalidate()
    }
}