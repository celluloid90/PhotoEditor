package com.example.photo_editor.editor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.photo_editor.editor.utils.ResizeBitmap;

/*

https://developer.android.com/codelabs/advanced-android-kotlin-training-clipping-canvas-objects#0

http://www.java2s.com/example/android/graphics/scale-bitmap-image-in-imageview-center-crop.html

* */

public class EditorView extends View {

    float mRatio;
    Bitmap mBackGroundBitmap;
    BitmapDrawable mDrawable;
    Bitmap mBitmap;
    String left, center, right;
    String recievedString;
    boolean isZoomed;
    private RectF rect;
    Matrix matrix;
    Matrix matrixMain;
    float bitmapWidth;
    float bitmapHeight;
    float bmRatio;
    float rectRatio;
    float finalWidth;
    float finalHeight;
    float positionX;
    float positionY;
    float scaleValueX;
    float scaleValueY;
    float scaleValueFinal;

    public EditorView(Context context) {
        super(context);
        init(null);
    }

    public EditorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EditorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);

    }

    public EditorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public void init(@Nullable AttributeSet set) {
        rect = new RectF();
        matrix = new Matrix();
        matrixMain = new Matrix();
    }

    public void setBackgroundPicture(Bitmap bitmap) {
        this.mBackGroundBitmap = bitmap;
        invalidate();
    }

    public void setPicture(Bitmap bitmap) {
        this.mBitmap = bitmap;
        invalidate();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setRatio(float w, float h) {
        mRatio = (float) w / h;

        scaleBackGroundMatrix(rect, matrix);
        scaleForGroundImageMatrix(rect, matrixMain);
        invalidate();
    }

    public void setLeftString(String recievedString) {
        this.recievedString = recievedString;

        if (recievedString.equals("left")) {
            matrixMain.setScale(scaleValueFinal, scaleValueFinal);
            Log.d("TAG", "setLeftString: " + scaleValueFinal);
            if (rect.width() > finalWidth) {
                matrixMain.postTranslate(0, rect.top);
            } else {
                matrixMain.postTranslate(rect.left, rect.top);
            }
        } else if (recievedString.equals("right")) {
            matrixMain.setScale(scaleValueFinal, scaleValueFinal);
            Log.d("TAG", "setLeftString: " + scaleValueFinal);
            if (rect.width() > finalWidth) {
                matrixMain.postTranslate(rect.right - finalWidth, rect.top);
            } else {
                matrixMain.postTranslate(rect.left, rect.bottom - finalHeight);
            }
        } else if (recievedString.equals("center")) {
            if (isZoomed == false) {


                float scalingNewX = rect.width() / mBitmap.getWidth();
                float scalingNewY = rect.height() / mBitmap.getHeight();
                float scaleMaX = Math.max(scalingNewX, scalingNewY);
                float scaleWidth = scaleMaX*mBitmap.getWidth();
                float scaleHeight = scaleMaX*mBitmap.getHeight();


                matrixMain.setTranslate(rect.left + positionX, rect.top + positionY);
                matrixMain.postScale(scaleMaX, scaleMaX, getWidth() / 2, getHeight() / 2);
                isZoomed = true;

                // matrixMain.postScale(rect.width(),rect.height(),getWidth()/2,getHeight()/2);

            } else {
                matrixMain.setTranslate(rect.left + positionX, rect.top + positionY);
                matrixMain.postScale(scaleValueFinal, scaleValueFinal, getWidth() / 2, getHeight() / 2);
                isZoomed = false;
            }
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.clipRect(rect);
        canvas.drawBitmap(mBackGroundBitmap, matrix, null);
        //     canvas.drawRect(rect, new Paint(Paint.ANTI_ALIAS_FLAG));
        canvas.restore();

        canvas.drawBitmap(mBitmap, matrixMain, null);

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
        positionX = (rect.width() - mBitmap.getWidth()) / 2;
        positionY = (rect.height() - mBitmap.getHeight()) / 2;
        float positionFX = (rect.width() - finalWidth) / 2;
        float positionFY = (rect.height() - finalHeight) / 2;

        scaleValueX = (finalWidth / mBitmap.getWidth());
        scaleValueY = (finalHeight / mBitmap.getHeight());
        scaleValueFinal = Math.max(scaleValueX, scaleValueY);
        matrixMain.setTranslate(rect.left + positionX, rect.top + positionY);
        matrixMain.postScale(scaleValueFinal, scaleValueFinal, getWidth() / 2, getHeight() / 2);

        Log.d("TAG", "getWidth(): " + getWidth() + " getHeight(): " + getHeight());
        Log.d("TAG", "rectW : " + rect.width() + " rectH: " + rect.height());
        Log.d("TAG", "finalWidth: " + finalWidth + " finalHeight: " + finalHeight);
        Log.d("TAG", "scaleForGroundImageMatrix: " + scaleValueFinal);


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

        /*matrix.setTranslate(rect.left+translateX, rect.top+translatey);
        matrix.postScale(scalePosition, scalePosition,getWidth()/2,getHeight()/2);*/
        //matrix.postTranslate(rect.left + translateX, rect.top + translatey);
        float pointX = (getWidth() - mBackGroundBitmap.getWidth()) / 2;
        float pointY = (getHeight() - mBackGroundBitmap.getHeight()) / 2;

        /*matrix.postTranslate(pointX, pointY);
        matrix.postScale(scalePosition, scalePosition, rect.left + rect.width() / 2, rect.top + rect.height() / 2);
*/

        matrix.setScale(scalePosition, scalePosition, getWidth() / 2, getHeight() / 2);
        matrix.preTranslate(rect.left + translateX, rect.top + translatey);

       /* canvas.save();
        canvas.clipRect(rect);
        canvas.drawBitmap(mBackGroundBitmap, matrix, null);
        canvas.drawRect(rect, paint);
        canvas.restore();*/
    }
}
