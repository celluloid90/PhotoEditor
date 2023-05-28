package com.example.photo_editor.editor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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

import com.example.photo_editor.editor.enums.BorderType;
import com.example.photo_editor.editor.enums.CanvasBackgroundType;
import com.example.photo_editor.editor.utils.BlurBitmap;
import com.example.photo_editor.editor.utils.BorderUtils;
import com.example.photo_editor.editor.utils.CheckButtonType;
import com.example.photo_editor.editor.utils.RoateImage;

/*

https://developer.android.com/codelabs/advanced-android-kotlin-training-clipping-canvas-objects#0

http://www.java2s.com/example/android/graphics/scale-bitmap-image-in-imageview-center-crop.html

* */

public class EditorView extends View {
    private Uri uri;
    private Bitmap mBitmap;
    private Bitmap mBackGroundBitmap;

    private Bitmap mGalleryBackgroundBm;
    private Matrix bgMatrix;
    private float finalWidth;
    private float finalHeight;
    private float bitmapWidth;
    private float bitmapHeight;
    private float bitmapRatio;
    private float viewRatio;
    private float x, y;
    float moveX, moveY;
    boolean centerBool = false;
    boolean leftBool = false;
    boolean rightBool = false;
    boolean leftBoolX = false;
    boolean leftBoolY = false;
    boolean rightBoolY = false;
    boolean rightBoolX = false;
    private float bmLeft, bmTop, bmRight, bmBottom;

    boolean buttonClicked = false;
    boolean touched = false;
    private float initialScale = .5f;
    private float centerX = initialScale, centerY = initialScale;
    private RectF rectF;

    private CheckButtonType checkButtonType;
    private CanvasBackgroundType canvasBackgroundType;


    private float mScaleFactor = 1.0f;
    private ScaleGestureDetector mScaleDetector;
    private BorderType borderType;

    private String color;


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            return true;
        }
    }


    public EditorView(Context context) {
        super(context);
        init(null, context);
    }

    public EditorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(null, context);
    }

    public EditorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(null, context);
    }

    public EditorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(null, context);
    }

    public void init(@Nullable AttributeSet set, Context context) {
        bgMatrix = new Matrix();
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    public void setBackgroundType(CanvasBackgroundType canvasBackgroundType) {
        this.canvasBackgroundType = canvasBackgroundType;
        invalidate();
    }

    public void setBorder(BorderType borderType) {
        this.borderType = borderType;
        Log.d("TAG", "setBorder: " + borderType);
        invalidate();

    }

    /*public void setRotation() {
        double mode = rotaionNew % 90;
        rotaionNew += 90 - mode;
        centerX = initialScale;
        centerY = initialScale;
        scaleForegroundImageCanvas();

        invalidate();
    }*/

    public void setgellaryUri(Uri uri) {
        mGalleryBackgroundBm = RoateImage.getRotatedBitmap(getContext(), uri);
        invalidate();
    }

    public void checkClickedButtonType(CheckButtonType checkButtonType) {

        buttonClicked = true;
        touched = false;
        this.checkButtonType = checkButtonType;

        if (checkButtonType.equals(CheckButtonType.CENTER)) {
            centerX = initialScale;
            centerY = initialScale;
            centerBool = true;
            leftBool = false;
            rightBool = false;
        }
        if (checkButtonType.equals(CheckButtonType.LEFT)) {
            centerBool = false;
            leftBool = true;
            rightBool = false;
            if (getWidth() > getHeight()) {
                centerX = ((finalWidth / 2) / getWidth());
                centerY = initialScale;
                leftBoolX = true;
                leftBoolY = false;
            } else {
                centerY = ((finalHeight) / 2) / getHeight();
                centerX = initialScale;
                leftBoolY = true;
                leftBoolX = false;
            }
        }
        if (checkButtonType.equals(CheckButtonType.RIGHT)) {
            centerBool = false;
            leftBool = false;
            rightBool = true;

            if (getWidth() > getHeight()) {
                centerX = (1 - (finalWidth / 2) / getWidth());
                centerY = initialScale;
                rightBoolX = true;
                rightBoolY = false;
            } else {
                centerY = (1 - (finalHeight / 2) / getHeight());
                centerX = initialScale;
                rightBoolY = true;
                rightBoolX = false;
            }
        }

        invalidate();

    }

    public void setBlurProgressValue(int value) {
        value = value / 4;
        mBackGroundBitmap = BlurBitmap.Companion.blurBitmap(mBitmap, getContext(), value);
        invalidate();
    }

    public void setImageUri(Uri uri) {
        mBitmap = RoateImage.getRotatedBitmap(getContext(), uri);
        mBackGroundBitmap = RoateImage.getRotatedBitmap(getContext(), uri);
        mBackGroundBitmap = BlurBitmap.Companion.blurBitmap(mBackGroundBitmap, getContext(), 20);
    }

    public void setColor(String color) {
        this.color = color;
        Log.d("color", "setColor: " + color.toString());
    }

    private void scaleForegroundImageCanvas() {
        bitmapWidth = mBitmap.getWidth();
        bitmapHeight = mBitmap.getHeight();

        bitmapRatio = bitmapWidth / bitmapHeight;
        viewRatio = (float) getWidth() / (float) getHeight();

        finalWidth = getWidth();
        finalHeight = getHeight();
        if (viewRatio >= bitmapRatio) {
            finalWidth = finalHeight * bitmapRatio;
        } else {
            finalHeight = finalWidth / bitmapRatio;
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        scaleForegroundImageCanvas();
        if (buttonClicked) {
            if (leftBool) {
                if (leftBoolY) {
                    if (h > finalHeight) {
                        centerY = (finalHeight / 2) / h;
                    } else {
                        centerY = initialScale;
                    }
                } else if (leftBoolX) {
                    if (w > finalWidth) {
                        centerX = (finalWidth / 2) / w;
                    } else if (h > finalHeight) {
                        centerX = initialScale;
                    }
                }
            }
            if (rightBool) {
                if (rightBoolY) {
                    if (h > finalHeight) {
                        centerY = (1 - (finalHeight / 2) / h);
                    } else {
                        centerY = initialScale;
                    }
                } else if (rightBoolX) {
                    if (w > finalWidth) {
                        centerX = (1 - (finalWidth / 2) / w);
                    } else if (h > finalHeight) {
                        centerX = initialScale;
                    }

                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        BackgroundView.setBackground(canvasBackgroundType, canvas, mBackGroundBitmap, bgMatrix,
                mGalleryBackgroundBm, mBitmap, getHeight(), getWidth(),color);
        // BackgroundView.createBackground(canvas, mBackGroundBitmap, bgMatrix);
        bmLeft = getWidth() * centerX - (finalWidth / 2) * mScaleFactor;
        bmTop = getHeight() * centerY - (finalHeight / 2) * mScaleFactor;
        bmRight = getWidth() * centerX + (finalWidth / 2) * mScaleFactor;
        bmBottom = getHeight() * centerY + (finalHeight / 2) * mScaleFactor;
        rectF = new RectF(bmLeft, bmTop, bmRight, bmBottom);

        BorderUtils.setBorder(mScaleFactor, borderType, canvas, bmLeft, bmTop, bmRight, bmBottom, getContext(), mBitmap);
        canvas.drawBitmap(mBitmap, null, rectF, null);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touched = true;
        buttonClicked = false;
        mScaleDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                x = event.getX();
                y = event.getY();
            }
            case MotionEvent.ACTION_MOVE: {
                moveX = event.getX();
                moveY = event.getY();
                centerX += ((moveX - x) / getWidth());
                centerY += ((moveY - y) / getHeight());
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
