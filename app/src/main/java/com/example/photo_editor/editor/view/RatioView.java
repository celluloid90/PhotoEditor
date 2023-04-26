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

import com.example.photo_editor.editor.model.DataModel;
import com.example.photo_editor.editor.utils.BlurBitmap;
import com.example.photo_editor.editor.utils.CheckButtonType;
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
    private float initialScale = .5f;
    private float centerX = initialScale, centerY = initialScale;
    private float bmLeft, bmTop, bmRight, bmBottom;
    private RectF rectF;
    private float bitmapWidth;
    private float bitmapHeight;
    private float finalWidth;
    private float finalHeight;
    private float x, y;
    float saveScale = 1f;
    CheckButtonType checkButtonType;
    private float mScaleFactor = 1.0f;
    private ScaleGestureDetector mScaleDetector;
    float v;
    boolean buttonClicked = false;
    boolean touched = false;
    private DataModel dataModel;
    boolean centerBool = false;
    boolean leftBool = false;
    boolean rightBool = false;
    boolean leftBoolX = false;
    boolean leftBoolY = false;
    boolean rightBoolY = false;
    boolean rightBoolX = false;

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
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
        dataModel = new DataModel();
    }

    public float getViewHeight() {
        return getHeight();
    }

    public float getViewWidth() {
        return getWidth();
    }

    public float getFinalWidth() {
        return finalWidth;
    }

    public float getFinalHeight() {
        return finalHeight;
    }

    public void setImageUri(Uri uri) {
       // this.uri = uri;
        mBitmap = RoateImage.getRotatedBitmap(getContext(), uri);
        mBackGroundBitmap = RoateImage.getRotatedBitmap(getContext(), uri);
        mBackGroundBitmap = BlurBitmap.Companion.blurBitmap(mBackGroundBitmap, getContext());
        invalidate();
    }

    public void setOriginalRatio() {
        centerY = .5f;
        centerX = .5f;
        mScaleFactor = saveScale;
    }


    public void checkClickedButtonType(CheckButtonType checkButtonType) {

        buttonClicked = true;
        touched = false;
        this.checkButtonType = checkButtonType;
        // float ratio = dataModel.setViewRatio(this);
        //centerX = dataModel.setCenterX(this);
        // centerY = dataModel.setCenterY(this);
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

    public CheckButtonType mCheckButtonType() {
        return checkButtonType;
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
        drawBackGroundCanvas(canvas);

        bmLeft = getWidth() * centerX - (finalWidth / 2) * mScaleFactor;
        bmTop = getHeight() * centerY - (finalHeight / 2) * mScaleFactor;
        bmRight = getWidth() * centerX + (finalWidth / 2) * mScaleFactor;
        bmBottom = getHeight() * centerY + (finalHeight / 2) * mScaleFactor;
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
        // canvas.drawColor(Color.YELLOW);
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
                float moveX, moveY;
                moveX = event.getX();
                moveY = event.getY();
                matrixMain.postTranslate(moveX - x, moveY - y);
                centerX += ((moveX - x) / getWidth());
                centerY += ((moveY - y) / getHeight());
                x = moveX;
                y = moveY;
                v = (float) Math.toDegrees(Math.atan2(centerY - y, centerX - x));

            }
            case MotionEvent.ACTION_UP: {

            }
        }
        invalidate();
        return true;
    }
}
