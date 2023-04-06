package com.example.photo_editor.editor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.photo_editor.editor.utils.BlurBitmap;
import com.example.photo_editor.editor.utils.CheckButtonType;
import com.example.photo_editor.editor.utils.RoateImage;

/*

https://developer.android.com/codelabs/advanced-android-kotlin-training-clipping-canvas-objects#0

http://www.java2s.com/example/android/graphics/scale-bitmap-image-in-imageview-center-crop.html

* */

public class EditorView extends View {

    private Uri uri;
    private float mRatio;
    private Bitmap mBackGroundBitmap;
    private Bitmap mBitmap;
    private boolean isZoomed;
    private RectF rect;
    private Matrix matrix;
    private Matrix matrixMain;
    private float bitmapWidth;
    private float bitmapHeight;
    private float bmRatio;
    private float rectRatio;
    private float finalWidth;
    private float finalHeight;
    private float positionX;
    private float positionY;
    private float scaleValueX;
    private float scaleValueY;
    private float scaleValueFinal;
    CheckButtonType checkButtonType;
    private int mLastAngle = 0;
    private int mPivotX, mPivotY;
    /* private ScaleGestureDetector mScaleDetector;*/
    private float x, y;
    private float mScaleFactor = 1.0f;
    private final static float mMinZoom = 1.0f;
    private final static float mMaxZoom = 5.0f;
    private boolean buttonClicked;
    float offsetFromCenterX = 0f, offsetFromCenterY = 0f;

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(mScaleFactor, Math.min(mScaleFactor, mMinZoom));
            return true;
        }
    }

    public EditorView(Context context) {
        super(context);
        init(null, context);
    }

    public EditorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public EditorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context);

    }

    public EditorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, context);
    }

    public void init(@Nullable AttributeSet set, Context context) {
        rect = new RectF();
        matrix = new Matrix();
        matrixMain = new Matrix();

        //mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    public void setImageUri(Uri uri) {
        this.uri = uri;
        mBitmap = RoateImage.getRotatedBitmap(getContext(), uri);
        mBackGroundBitmap = RoateImage.getRotatedBitmap(getContext(), uri);
        mBackGroundBitmap = BlurBitmap.Companion.blurBitmap(mBackGroundBitmap, getContext());
        invalidate();
    }


    public void setRatio(float w, float h) {
        buttonClicked = true;
        mRatio = (float) w / h;
        scaleBackGroundMatrix(rect, matrix);
        float tx = 12f;
        float ty = 10f;
        scaleForGroundImageMatrix(rect, matrixMain);
        //matrixMain.postTranslate(tx, ty);
        invalidate();
    }

    public void checkClickedButtonType(CheckButtonType checkButtonType) {
        this.checkButtonType = checkButtonType;
        offsetFromCenterY=0f;
        offsetFromCenterX=0f;
        if (checkButtonType.equals(CheckButtonType.LEFT)) {
            matrixMain.setScale(scaleValueFinal, scaleValueFinal);
            if (rect.width() > finalWidth) {
                matrixMain.postTranslate(0, rect.top);
            } else {
                matrixMain.postTranslate(rect.left, rect.top);
            }
        } else if (checkButtonType.equals(CheckButtonType.RIGHT)) {
            matrixMain.setScale(scaleValueFinal, scaleValueFinal);
            if (rect.width() > finalWidth) {
                matrixMain.postTranslate(rect.right - finalWidth, rect.top);
            } else {
                matrixMain.postTranslate(rect.left, rect.bottom - finalHeight);
            }
        } else if (checkButtonType.equals(CheckButtonType.CENTER)) {
            float x = (rect.width() - mBitmap.getWidth()) / 2;
            float y = (rect.height() - mBitmap.getHeight()) / 2;
            if (!isZoomed) {
                float scalingNewX = rect.width() / mBitmap.getWidth();
                float scalingNewY = rect.height() / mBitmap.getHeight();
                float scaleMaX = Math.max(scalingNewX, scalingNewY);

                matrixMain.setTranslate(rect.left + x, rect.top + y);
                matrixMain.postScale(scaleMaX, scaleMaX, rect.centerX(), rect.centerY());
                isZoomed = true;

            } else {
                matrixMain.setTranslate(rect.left + x, rect.top + y);
                matrixMain.postScale(scaleValueFinal, scaleValueFinal, getWidth() / 2, getHeight() / 2);
                isZoomed = false;
            }
        }

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.clipRect(rect);
        canvas.drawBitmap(mBackGroundBitmap, matrix, null);
        canvas.drawBitmap(mBitmap, matrixMain, null);
        canvas.restore();


    }


    private void scaleForGroundImageMatrix(RectF rect, Matrix matrixMain) {
        bitmapWidth = mBitmap.getWidth();
        bitmapHeight = mBitmap.getHeight();

        bmRatio = bitmapWidth / bitmapHeight;
        rectRatio = rect.width() / rect.height();

        finalWidth = rect.width();
        finalHeight = rect.height();

        if (rectRatio >= bmRatio) {
            finalWidth = rect.height() * bmRatio;
        } else {
            finalHeight = rect.width() / bmRatio;
        }
        x = (rect.width() - mBitmap.getWidth()) / 2;
        y = (rect.height() - mBitmap.getHeight()) / 2;


        float[] matValues = new float[9];
        matrixMain.getValues(matValues);

        float prevTx = matValues[Matrix.MTRANS_X];
        float prevTy = matValues[Matrix.MTRANS_Y];
        float[] arrayFt = {prevTx,prevTy};


        scaleValueX = (finalWidth / mBitmap.getWidth());
        scaleValueY = (finalHeight / mBitmap.getHeight());
        scaleValueFinal = Math.max(scaleValueX, scaleValueY);

        matrixMain.setTranslate(rect.left + x, rect.top + y);
        matrixMain.postScale(scaleValueFinal, scaleValueFinal, getWidth()/2f, getHeight()/2f);


    }

    private void scaleBackGroundMatrix(RectF rect, Matrix matrix) {
        int width = getWidth();
        int height = getHeight();

        float newWidth = width;
        float newHeight = height;

        if (mRatio >= 1.0f) {
            newHeight = newWidth / mRatio;
            if (newHeight > height) {
                newHeight = height;
                newWidth = height * mRatio;
            }
        } else {
            newWidth = newHeight * mRatio;
            if (newWidth > width) {
                newWidth = width;
                newHeight = width / mRatio;
            }
        }

        float rectW = newWidth;
        float rectH = newHeight;

        rect.top = ((height - rectH) / 2);
        rect.left = ((width - rectW) / 2);

        rect.right = (rect.left + rectW);
        rect.bottom = (rect.top + rectH);

        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        float scaleWidth = (rect.width() / mBackGroundBitmap.getWidth());
        float scaleHeight = (rect.height() / mBackGroundBitmap.getHeight());
        float scalePosition = Math.max(scaleWidth, scaleHeight);

        float translateX = (rect.width() - mBackGroundBitmap.getWidth()) / 2;
        float translatey = (rect.height() - mBackGroundBitmap.getHeight()) / 2;

        matrix.setScale(scalePosition, scalePosition, getWidth() / 2, getHeight() / 2);
        matrix.preTranslate(rect.left + translateX, rect.top + translatey);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        buttonClicked = false;
        //mScaleDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {

                x = event.getX();
                y = event.getY();

            }
            case MotionEvent.ACTION_MOVE: {
                // Log.d("TAG", "onTouchEvent: " + "ActionMove");
                float moveX, moveY;
                moveX = event.getX();
                moveY = event.getY();

                matrixMain.postTranslate(moveX - x, moveY - y);

                offsetFromCenterX += moveX - x;
                offsetFromCenterY += moveY - y;

                x = moveX;
                y = moveY;

                float[] matValues = new float[9];
                matrixMain.getValues(matValues);


                //  invalidate();
                // return true;
            }
            case MotionEvent.ACTION_UP: {
                Log.d("TAG", "onTouchEvent: " + "ActionUp");
                //return true;
            }
        }
        invalidate();
        return true;
    }
}
