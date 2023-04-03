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
import android.view.View;

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
    public void setImageUri(Uri uri){
        this.uri = uri;
        mBitmap = RoateImage.getRotatedBitmap(getContext(),uri);
        mBackGroundBitmap = RoateImage.getRotatedBitmap(getContext(),uri);
        mBackGroundBitmap = BlurBitmap.Companion.blurBitmap(mBackGroundBitmap,getContext());
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

    public void checkClickedButtonType(CheckButtonType checkButtonType) {
        this.checkButtonType = checkButtonType;

        if (checkButtonType.equals(CheckButtonType.LEFT) ) {
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
            if (!isZoomed) {
                float scalingNewX = rect.width() / mBitmap.getWidth();
                float scalingNewY = rect.height() / mBitmap.getHeight();
                float scaleMaX = Math.max(scalingNewX, scalingNewY);

                matrixMain.setTranslate(rect.left + positionX, rect.top + positionY);
                matrixMain.postScale(scaleMaX, scaleMaX, rect.centerX(), rect.centerY());
                isZoomed = true;

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
        positionX = (rect.width() - mBitmap.getWidth()) / 2;
        positionY = (rect.height() - mBitmap.getHeight()) / 2;

        scaleValueX = (finalWidth / mBitmap.getWidth());
        scaleValueY = (finalHeight / mBitmap.getHeight());
        scaleValueFinal = Math.max(scaleValueX, scaleValueY);
        matrixMain.setTranslate(rect.left + positionX, rect.top + positionY);
        matrixMain.postScale(scaleValueFinal, scaleValueFinal, getWidth() / 2, getHeight() / 2);

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
}
