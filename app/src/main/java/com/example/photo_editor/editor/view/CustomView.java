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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

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
    float mCircleX, mCircleY;
    float mCircleRadius = 100f;
    Bitmap mBitmap;


    public CustomView(Context context, Bitmap bitmap) {
        super(context);
        init(null, bitmap);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, Bitmap bitmap) {
        super(context, attrs);
        init(attrs, bitmap);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, Bitmap bitmap) {
        super(context, attrs, defStyleAttr);
        init(attrs, bitmap);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes, Bitmap bitmap) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, bitmap);
    }

    public void init(@Nullable AttributeSet set, Bitmap bitmap) {
        this.mBitmap = bitmap;
        mRectSquare = new Rect();
        mPaintSquare = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCircle = new Paint();
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setColor(Color.parseColor("#00ccff"));

        // mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.friends_main);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int padding = 50;
                mBitmap = getResizedBitmap(mBitmap, getWidth() - padding, getHeight() - padding);

             /*   new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        int newWidth = mBitmap.getWidth() - 50;
                        int newHeight = mBitmap.getHeight() - 50;

                        if (newWidth <= 0 ||newHeight<=0){
                            cancel();
                            return;
                        }

                        mBitmap = getResizedBitmap(mBitmap, newWidth,newHeight);
                        postInvalidate();
                    }
                }, 2000, 500);*/
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

    public void swapColor() {
        mPaintSquare.setColor(mPaintSquare.getColor() == mSquareColor ? Color.RED : mSquareColor);
        postInvalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        mRectSquare.left = 50;
        mRectSquare.top = 50;
        mRectSquare.right = mRectSquare.left + mSquareSize;
        mRectSquare.bottom = mRectSquare.top + mSquareSize;


        //canvas.drawRect(mRectSquare, mPaintSquare);

      /*  float cy, cx;
        float radius = 100f;
        cx =getWidth() - radius - 50f;
        cy = mRectSquare.top + (mRectSquare.height()/2);*/

        if (mCircleX == 0f || mCircleY == 0f) {
            mCircleX = getWidth() / 2;
            mCircleY = getHeight() / 2;

        }
        //canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mPaintCircle);
        float imageX = (getWidth() - mBitmap.getWidth()) / 2;
        float imageY = (getHeight() - mBitmap.getHeight()) / 2;

        canvas.drawBitmap(mBitmap, imageX, imageY, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value = super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {

                float x = event.getX();
                float y = event.getY();

                if (mRectSquare.left < x && mRectSquare.right > x)
                    if (mRectSquare.top < y && mRectSquare.bottom > y) {
                        mCircleRadius += 10f;
                        postInvalidate();
                    }
                return true;
            }
            case MotionEvent.ACTION_MOVE: {

                float x = event.getX();
                float y = event.getY();

                double dx = Math.pow(x - mCircleX, 2);
                double dy = Math.pow(y - mCircleY, 2);

                if (dx + dy < Math.pow(mCircleRadius, 2)) {
                    mCircleX = x;
                    mCircleY = y;
                    postInvalidate();
                    return true;
                }
                return true;
            }

        }
        return value;

    }

    private Bitmap getResizedBitmap(Bitmap bitmap, int width, int height) {
        Matrix matrix = new Matrix();
        RectF src = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF dest = new RectF(0, 0, width, height);
        matrix.setRectToRect(src, dest, Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
