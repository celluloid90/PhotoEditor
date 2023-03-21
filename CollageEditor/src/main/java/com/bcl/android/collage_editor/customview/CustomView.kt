package com.bcl.android.collage_editor.customview

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.View
import androidx.annotation.RequiresApi


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

    init {
        path1 = Path()
        path2 = Path()

        paint1 = Paint(Paint.ANTI_ALIAS_FLAG)
        paint1.setColor(Color.CYAN)

        paint2 = Paint(Paint.ANTI_ALIAS_FLAG)
        paint2.setColor(Color.MAGENTA)

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

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.save()
        canvas?.scale(scaleFactor, scaleFactor, width / 2f, height / 2f)

        paint1.pathEffect = corEffect
        paint2.pathEffect = corEffect

        canvas?.drawPath(path1, paint1)

        canvas?.drawPath(path2, paint2)

        canvas?.restore()
    }


    fun updateEdgeSmooth(edgeGapValue: Float) {
        corEffect = CornerPathEffect(edgeGapValue)
        invalidate()
    }
}