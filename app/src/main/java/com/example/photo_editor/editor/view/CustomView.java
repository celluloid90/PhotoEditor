package com.example.photo_editor.editor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.photo_editor.R;

import java.util.Timer;
import java.util.TimerTask;

// android zoomin and zoom out a bitmap in customview cancas

public class CustomView extends View {

    private float mPositionX, mPositionY;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.0f;
    private final static float mMinZoom = 1.0f;
    private final static float mMaxZoom = 5.0f;

    private static final int SQUARE_SIZE_DFF = 400;
    Rect mRectSquare;
    Paint mPaintSquare;
    private int mSquareColor;
    private int mSquareSize;
    Paint mPaintCircle;
    Bitmap mBitmap;
    Bitmap mBackGroundBitmap;
    float x, y;
    float imageBackGroundY;
    float imageBackGroundX;
    float imageX;
    float imageY;

    PointF start = new PointF();
    PointF mid = new PointF();

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(mScaleFactor, Math.min(mScaleFactor, mMinZoom));
            invalidate();
            return true;
        }
    }

    public CustomView(Context context) {
        super(context);
        init(null);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public void init(@Nullable AttributeSet set) {

        mRectSquare = new Rect();
        mPaintSquare = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCircle = new Paint();
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setColor(Color.parseColor("#00ccff"));
        mScaleDetector = new ScaleGestureDetector(getContext(),new ScaleListener());
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int padding = 150;
                mBitmap = getResizedBitmap(mBitmap, getWidth() - padding, getHeight() - padding);
                mBackGroundBitmap = getResizedBitmap(mBackGroundBitmap, getWidth(), getHeight());

            }
        });

        if (set == null)
            return;
        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.CustomView);
        mSquareColor = ta.getColor(R.styleable.CustomView_square_color, Color.GREEN);
        mSquareSize = ta.getDimensionPixelSize(R.styleable.CustomView_square_size, SQUARE_SIZE_DFF);
        mPaintSquare.setColor(mSquareColor);
        ta.recycle();
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
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setPicture(Bitmap bitmap) {
        this.mBitmap = bitmap;
        invalidate();

    }

    public void setBackgroundPicture(Bitmap bitmap) {
        this.mBackGroundBitmap = bitmap;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
   /*     imageX = (getWidth() - mBitmap.getWidth()) / 2;
        imageY = (getHeight() - mBitmap.getHeight()) / 2;*/

        imageBackGroundX = (getWidth() - mBackGroundBitmap.getWidth()) / 2;
        imageBackGroundY = (getHeight() - mBackGroundBitmap.getHeight()) / 2;

        canvas.drawBitmap(mBackGroundBitmap, imageBackGroundX, imageBackGroundY, null);

        drawBitmap(canvas);

    }

    private void drawBitmap(Canvas canvas) {
        canvas.save();
        canvas.translate(mPositionX, mPositionY);
        canvas.scale(mScaleFactor, mScaleFactor);
        canvas.drawBitmap(mBitmap, imageX, imageY, null);
        canvas.restore();

    }

    private Bitmap getResizedBitmap(Bitmap bitmap, int width, int height) {
        Matrix matrix = new Matrix();
        RectF src = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF dest = new RectF(0, 0, width, height);
        matrix.setRectToRect(src, dest, Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //boolean value = super.onTouchEvent(event);
        mScaleDetector.onTouchEvent(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                x = event.getX();
                y = event.getY();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                float moveX, moveY;
                moveX = event.getX();
                moveY = event.getY();
                mPositionX += moveX - x;
                mPositionY += moveY - y;
                y = moveY;
                x = moveX;
                postInvalidate();
                return true;

            }
            case MotionEvent.ACTION_UP: {
                return true;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                midPoint(mid, event);
                Log.d("TAG", "onTouchEvent: " + mid);
            }
        }
        invalidate();
        return true;

    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


    public interface ViewSizeChangedListener {
        void customViewSizeChange();
    }
}
