package com.bcl.android.collage_editor.customview

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.bcl.android.collage_editor.utils.ImageUtils


/**
 * Created by Raihan Uddin Piash on ১৬/৩/২৩

 * Copyright (c) 2023 Brain Craft LTD.
 **/
class CustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    var originalBitmapPath: Path
    var mPath: Path
    private lateinit var region: Region
    private var mMatrix: Matrix = Matrix()
    private lateinit var uri: Uri
    private lateinit var originalBitmap: Bitmap

    val paint1 = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    init {
        scaleType = ScaleType.CENTER
        originalBitmapPath = Path()
        mPath = Path()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        originalBitmapPath.reset()

        originalBitmapPath.moveTo(0.1f * w, 0.1f * h)
        originalBitmapPath.lineTo(0.9f * w, 0.1f * h)
        originalBitmapPath.lineTo(0.9f * w, 0.9f * h)
        originalBitmapPath.lineTo(0.1f * w, 0.9f * h)
        originalBitmapPath.close()

        val rectF = RectF()
        originalBitmapPath.computeBounds(rectF, true)
        region = Region()
        region.setPath(
            originalBitmapPath, Region(
                rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt()
            )
        )

        mMatrix = Matrix()
        mMatrix.set(
            ImageUtils.createMatrixToDrawImageInCenterView(
                region, originalBitmap.width.toFloat(), originalBitmap.height.toFloat()
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        canvas?.clipPath(originalBitmapPath)
        /*val viewWidth = width
        val viewHeight = height
        val bitmapWidth: Int = originalBitmap.width
        val bitmapHeight: Int = originalBitmap.height

        val left = (viewWidth - bitmapWidth) / 2
        val top = (viewHeight - bitmapHeight) / 2*/

        canvas?.drawBitmap(originalBitmap, mMatrix, paint1)
        canvas?.save()
        paint1.color = Color.RED
        canvas?.drawPath(mPath, paint1)
        canvas?.save()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mPath.reset()
                mPath.moveTo(event.x, event.y)
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                mPath.lineTo(event.x, event.y)
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
//                cutBitmap()
                invalidate()
            }
        }

        return true
    }

    fun cutBitmap() {
        val cutPath = Path(mPath) // Create a copy of the drawn path
        val cutCanvas = Canvas(originalBitmap)
        cutCanvas.clipPath(cutPath)
        cutCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }

    fun setData(uri: Uri) {
        this.uri = uri
        this.originalBitmap = (ImageUtils.getResizedBitmap(context, uri, 720, 720)!!)
        invalidate()
    }

    fun resetPath() {
        mPath.reset();
        invalidate()
    }
}