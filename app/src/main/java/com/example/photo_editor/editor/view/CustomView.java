package com.example.photo_editor.editor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.LongDef;
import androidx.annotation.Nullable;

import com.example.photo_editor.R;

import java.util.Timer;
import java.util.TimerTask;


public class CustomView extends View {
    private static final int SQUARE_SIZE_DFF = 400;
    Rect mRectSquare;
    Paint mPaintSquare;
    private int mSquareColor;
    private int mSquareSize;
    Paint mPaintCircle;
    Bitmap mBitmap;
    Bitmap mBackGroundBitmap;
    float dX, dY;
    float x, y;
    float imageBackGroundY;
    float imageBackGroundX;
    int bm_offsetx;
    int bm_offsety;
    int bm_x = 0;
    int bm_y = 0;
    float imageX;
    float imageY;
    boolean dm_touched = false;
    boolean touching = false;

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
        canvas.drawBitmap(mBitmap, imageX, imageY, null);
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
        boolean value = super.onTouchEvent(event);
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
                float distanceX = moveX - x;
                float distanceY = moveY - y;
                imageX = distanceX;
                imageY = distanceY;
                postInvalidate();
                return true;

            }
            case MotionEvent.ACTION_UP: {
                touching = false;
                dm_touched = false;
                return true;
            }
        }
        invalidate();
        return value;

    }

    public interface ViewSizeChangedListener {
        void customViewSizeChange();
    }
}
