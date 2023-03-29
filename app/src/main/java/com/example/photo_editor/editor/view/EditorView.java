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
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.photo_editor.editor.utils.ResizeBitmap;

/*

https://developer.android.com/codelabs/advanced-android-kotlin-training-clipping-canvas-objects#0

* */

public class EditorView extends View {

    float mRatio;
    Bitmap mBackGroundBitmap;
    BitmapDrawable mDrawable;
    Bitmap mBitmap;
    String left, center, right;
    String recievedString;
    boolean isZoomed;


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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setRatio(float w, float h) {
        mRatio = (float) w / h;
        Log.d("log", "setRatio: " + mRatio);
        invalidate();
    }

    public void setLeftString(String recievedString) {
        this.recievedString = recievedString;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("log", "onDraw: " + mRatio);
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        float newWidth = width;
        float newHeight = height;

        if (mRatio >= 1.0f) {
           /* newHeight = newWidth / mRatio;

            if (newHeight > height) {
                newWidth = height;
                newHeight = height / mRatio;
            }*/
            newHeight = newWidth / mRatio;
            if (newHeight > height) {
                newHeight = height;
                newWidth = height * mRatio;
            }

        } else {
           /* newWidth = newHeight * mRatio;
            if (newWidth>width){
                newHeight = width;
                newWidth = width * mRatio;
            }*/
            newWidth = newHeight * mRatio;
            if (newWidth > width) {
                newWidth = width;
                newHeight = width / mRatio;
            }
        }

        float rectW = newWidth;
        float rectH = newHeight;
        Log.d("TIKTIK", "h: " + rectW);
        Log.d("TIKTIK", "w: " + rectH);


        /*rectW = width * myRect.width();
        rectH = height * myRect.height();*/

        RectF rect = new RectF();
        rect.top = ((height - rectH) / 2);
        rect.left = ((width - rectW) / 2);

        rect.right = (rect.left + rectW);
        rect.bottom = (rect.top + rectH);

        Matrix matrix = new Matrix();
        Paint paint = new Paint();
        paint.setColor(Color.rgb(255, 0, 0));
        paint.setAlpha(120);
        float scaleWidth = (rect.width() / mBackGroundBitmap.getWidth());
        float scaleHeight = (rect.height() / mBackGroundBitmap.getHeight());
        float scalePosition = Math.max(scaleWidth, scaleHeight);

        Log.d("saadsa", "rectWidth: " + rect.width() + "rectHeight: " + rect.height());
        Log.d("saadsa", "bmWidth: " + mBackGroundBitmap.getWidth() + "bmHeight: " + mBackGroundBitmap.getHeight());

        float translateX = (rect.width() - mBackGroundBitmap.getWidth()) / 2;
        float translatey = (rect.height() - mBackGroundBitmap.getHeight()) / 2;
        Log.d("saadsa", "translateX position: " + translateX);
        Log.d("saadsa", "translateY position: " + translatey);

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

        canvas.save();
        canvas.clipRect(rect);
        canvas.drawBitmap(mBackGroundBitmap, matrix, null);
        canvas.drawRect(rect, paint);
        canvas.restore();

        //canvas.drawBitmap(mBitmap,0,0,null);


        //    Matrix matrix = scaleMatrix(rect, mBackGroundBitmap);


        //canvas.drawRect(rect, paint);


      /*  Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        // canvas.drawBitmap(mBackGroundBitmap,matrix,paint);

        canvas.save();
        canvas.clipRect(rect);
        canvas.drawBitmap(mBackGroundBitmap, matrix, null);
        canvas.drawRect(rect, paint);
        canvas.restore();*/
        //drawBitmap(canvas, rect);

    }


   /* private void drawBitmap(Canvas canvas, RectF rectF) {
        canvas.save();
        float ratioRect = rectF.width() / rectF.height();
        float ratioBitmap = (float) mBitmap.getWidth() / (float) mBitmap.getHeight();
        Log.d("TAG", "ratioBitmap: " + ratioBitmap);
        Log.d("TAG", "ratioRect: " + ratioRect);
        Log.d("TAG", "bmW: " + mBitmap.getWidth());
        Log.d("TAG", "bmH: " + mBitmap.getHeight());
        float finalWIdth = rectF.width();
        float finalHeight = rectF.height();
        Log.d("TAG", "1stW: " + finalWIdth + " " + "1stH" + finalHeight);
        if (ratioRect >= ratioBitmap) {
            finalWIdth = finalHeight * ratioBitmap;
            Log.d("TAG", "afterWidth: " + finalWIdth);
        } else {
            finalHeight = finalWIdth / ratioBitmap;
            Log.d("TAG", "afterHeight: " + finalWIdth);

        }
        Matrix matrix = new Matrix();
        float getPosition = (rectF.width() - finalWIdth) / 2;
        float getpositionY = (rectF.height() - finalHeight) / 2;
        // matrix.setTranslate(rectF.left+getPosition, rectF.top+getpositionY);


        float l = rectF.centerX() - finalWIdth / 2f;
        float t = rectF.centerY() - finalHeight / 2f;
        Log.d("TAG", "rectF.centerX(): " + rectF.centerX());
        Log.d("TAG", "rectF.width(): " + rectF.width());
        Log.d("TAG", "rectF.height(): " + rectF.height());
        float r = l + finalWIdth;
        float b = t + finalHeight;

        float scale = finalWIdth / (float) mBitmap.getWidth();


        matrix.setScale(scale, scale);
        matrix.postTranslate(l, t);

      *//*  if (recievedString.equals("center")) {
            matrix.postTranslate(l, t);
            invalidate();
        }
        else if(recievedString.equals("left")){
             matrix.postTranslate(rectF.left,t);
        }
        else if ((recievedString.equals("right"))){
            matrix.postTranslate(rectF.width()- mBitmap.getWidth(),t);
        }*//*

        canvas.drawBitmap(mBitmap, matrix, null);


        //canvas.drawBitmap(mBitmap, null, new RectF(l , t, r, b), null);


        canvas.restore();
    }*/
}
