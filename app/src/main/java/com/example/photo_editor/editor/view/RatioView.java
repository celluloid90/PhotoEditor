package com.example.photo_editor.editor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.photo_editor.editor.utils.BlurBitmap;
import com.example.photo_editor.editor.utils.RoateImage;

public class RatioView extends View {

    private Uri uri;
    private Bitmap mBitmap;
    private Bitmap mBackGroundBitmap;
    private Matrix bgMatrix;
    private Matrix matrixMain;
    private float mRatio;
    private float bitmapRatio;
    private float viewRatio;
    private float centerX = .5f, centerY = .5f;
    private float bmLeft, bmTop, bmRight, bmBottom;
    private RectF rectF;
    private float bitmapWidth;
    private float bitmapHeight;
    private float finalWidth;
    private float finalHeight;
    private float x, y;
    private float mScaleFactor = 1.0f;
    private final static float mMinZoom = 1.0f;
    private final static float mMaxZoom = 5.0f;
    private ScaleGestureDetector mScaleDetector;

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(mScaleFactor, Math.min(mScaleFactor, mMinZoom));
            return true;
        }
    }

    public RatioView(Context context) {
        super(context);
        init(null, context);
    }

    public RatioView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(null, context);
    }

    public RatioView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(null, context);
    }

    public RatioView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(null, context);
    }

    public void init(@Nullable AttributeSet set, Context context) {

        bgMatrix = new Matrix();
        matrixMain = new Matrix();
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());


        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    public void setImageUri(Uri uri) {
        this.uri = uri;
        mBitmap = RoateImage.getRotatedBitmap(getContext(), uri);
        mBackGroundBitmap = RoateImage.getRotatedBitmap(getContext(), uri);
        mBackGroundBitmap = BlurBitmap.Companion.blurBitmap(mBackGroundBitmap, getContext());
        invalidate();
    }

    private void scaleForegroundImageMatrix() {
        bitmapWidth = mBitmap.getWidth();
        bitmapHeight = mBitmap.getHeight();

        bitmapRatio = bitmapWidth / bitmapHeight;
        viewRatio = (float) getWidth() / (float) getHeight();

        Log.d("TAG", "getWidth : " + getWidth() + " getHeight: " + getHeight());
        finalWidth = getWidth();
        finalHeight = getHeight();
        if (viewRatio >= bitmapRatio) {
            finalWidth = finalHeight * bitmapRatio;
        } else {
            finalHeight = finalWidth / bitmapRatio;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackGroundCanvas(canvas);
        scaleForegroundImageMatrix();
        bmLeft = getWidth() * centerX - finalWidth / 2;
        bmTop = getHeight() * centerY - finalHeight / 2;
        bmRight = getWidth() * centerX + finalWidth / 2;
        bmBottom = getHeight() * centerY + finalHeight / 2;
        rectF = new RectF(bmLeft, bmTop, bmRight, bmBottom);
        canvas.drawBitmap(mBitmap, null, rectF, null);
    }

    private void drawBackGroundCanvas(Canvas canvas) {
        float scalemWidth = (float) getWidth() / mBackGroundBitmap.getWidth();
        float scalemHeight = (float) getHeight() / mBackGroundBitmap.getHeight();
        float maxScale = Math.max(scalemWidth, scalemHeight);
        canvas.drawColor(Color.GREEN);
        bgMatrix.setScale(maxScale, maxScale, mBackGroundBitmap.getWidth() / 2, mBackGroundBitmap.getHeight() / 2);

        bgMatrix.postTranslate(Math.round(getWidth() - mBackGroundBitmap.getWidth()) * .5f,
                Math.round(getHeight() - mBackGroundBitmap.getHeight()) * .5f);
        canvas.drawBitmap(mBackGroundBitmap, bgMatrix, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                x = event.getX();
                y = event.getY();
            }
            case MotionEvent.ACTION_MOVE: {
                float moveX, moveY;
                moveX = event.getX();
                moveY = event.getY();
                matrixMain.postTranslate(moveX - x, moveY - y);
                centerX += ((moveX-x)/getWidth());
                centerY += ((moveY-y)/getHeight());
                x = moveX;
                y = moveY;
            }
            case MotionEvent.ACTION_UP: {

            }
        }
        invalidate();
        return true;
    }
}
