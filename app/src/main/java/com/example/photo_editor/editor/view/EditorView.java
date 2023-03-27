package com.example.photo_editor.editor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.photo_editor.R;

/*

https://developer.android.com/codelabs/advanced-android-kotlin-training-clipping-canvas-objects#0

* */

public class EditorView extends View {

    float mRatio;
    Bitmap mBackGroundBitmap;
    BitmapDrawable mDrawable;
    Bitmap mBitmap;


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

    }

    public void setBackgroundPicture(Bitmap bitmap) {
        this.mBackGroundBitmap = bitmap;
        invalidate();
    }

    public void setPicture(Bitmap bitmap) {
        this.mBitmap = bitmap;
        invalidate();

    }

    public void setRatio(float w, float h) {
        mRatio = (float) w / h;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d("TAG", "bmHight: " + mBackGroundBitmap.getHeight());
        Log.d("TAG", "bmWidth: " + mBackGroundBitmap.getHeight());

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Log.d("TAG", "width: " + width + " height :" + height);

        RectF myRect = new RectF(0f, 0f, 1f, 1f);

        if (mRatio >= 1f) {
            myRect.right = 1f;
            myRect.bottom = myRect.right / mRatio;
        } else {
            myRect.bottom = 1f;
            myRect.right = myRect.bottom * mRatio;
        }

        float rectW = height * mRatio;
        float rectH = width / mRatio;
        Log.d("TAG", "h: " + rectW);
        Log.d("TAG", "w: " + rectH);

        rectW = width * myRect.width();
        rectH = height * myRect.height();

        RectF rect = new RectF();
        rect.top = ((height - rectH) / 2);
        rect.left = ((width - rectW) / 2);

        rect.right = (rect.left + rectW);
        rect.bottom = (rect.top + rectH);

        Matrix matrix = scaleMatrix(rect, mBackGroundBitmap);

        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        // canvas.drawBitmap(mBackGroundBitmap,matrix,paint);

        canvas.save();
        canvas.clipRect(rect);
        canvas.drawBitmap(mBackGroundBitmap, matrix, null);
        canvas.drawRect(rect, paint);
        canvas.restore();
        drawBitmap(canvas);
    }

    private void drawBitmap(Canvas canvas) {
        canvas.save();
        float pointX = (getWidth() - mBitmap.getWidth()) / 2;
        float pointy = (getHeight() - mBitmap.getHeight()) / 2;
        canvas.drawBitmap(mBitmap, pointX, pointy, null);
        canvas.restore();
    }

    private Matrix scaleMatrix(RectF rect, Bitmap mBackGroundBitmap) {

        float scaleWidth = rect.width() / mBackGroundBitmap.getWidth();
        float scaleHeight = rect.height() / mBackGroundBitmap.getHeight();
        float salePoint = Math.max(scaleWidth, scaleHeight);

        Log.d("TAG", "left " + rect.left + "top " + rect.top + "right " + rect.right + "bottom " + rect.bottom);
        Log.d("TAG", "width " + rect.width() + "height " + rect.height());

        Matrix matrix = new Matrix();


        matrix.setTranslate(rect.left + (rect.width() - mBackGroundBitmap.getWidth()) / 2, rect.top + (rect.height() - mBackGroundBitmap.getHeight()) / 2);
        matrix.postScale(salePoint, salePoint, rect.centerX(), rect.centerY());
        return matrix;
    }

}
