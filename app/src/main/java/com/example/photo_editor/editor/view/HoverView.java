package com.example.photo_editor.editor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

public class HoverView extends View {
    Bitmap mBitmap;

    public HoverView(Context context) {
        super(context);
        init(null);
    }
    public HoverView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HoverView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public HoverView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }
    public void init(@Nullable AttributeSet set){
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int padding = 50;
                mBitmap = getResizedBitmap(mBitmap, getWidth() - padding, getHeight() - padding);
            }
        });
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }
    public void setPicture(Bitmap bitmap){
        this.mBitmap = bitmap;
        invalidate();

    }
    @Override
    protected void onDraw(Canvas canvas) {
        float imageX = (getWidth() - mBitmap.getWidth()) / 2;
        float imageY = (getHeight() - mBitmap.getHeight()) / 2;

        canvas.drawBitmap(mBitmap, imageX, imageY, null);
    }
    private Bitmap getResizedBitmap(Bitmap bitmap, int width, int height) {
        Matrix matrix = new Matrix();
        RectF src = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF dest = new RectF(0, 0, width, height);
        matrix.setRectToRect(src, dest, Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
