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
    private RectF rect;
    Matrix matrix;
    Matrix matrixMain;


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
        invalidate();
    }

    public void setLeftString(String recievedString) {
        this.recievedString = recievedString;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        scaleBackGroundMatrix(canvas,rect,matrix);
        scaleForGroundImageMatrix(canvas,rect,matrixMain);
    }

    private void scaleForGroundImageMatrix(Canvas canvas, RectF rect, Matrix matrixMain) {
        float bitmapWidth = mBitmap.getWidth();
        float bitmapHeight = mBitmap.getHeight();

        float bmRatio = bitmapWidth / bitmapHeight;
        float rectRatio = rect.width() / rect.height();

        float finalWidth = rect.width();
        float finalHeight = rect.height();

        if (rectRatio >= bmRatio) {
            finalWidth = rect.height() * bmRatio;
        } else {
            finalHeight = rect.width() / bmRatio;
        }


        float positionX = (rect.width() - mBitmap.getWidth()) / 2;
        float positionY = (rect.height() - mBitmap.getHeight()) / 2;
        float positionFX = (rect.width() - finalWidth) / 2;
        float positionFY = (rect.height() - finalHeight) / 2;

        float scaleValueX = (finalWidth / mBitmap.getWidth());
        float scaleValueY = (finalHeight / mBitmap.getHeight());
        float scaleValueFinal = Math.max(scaleValueX, scaleValueY);
        matrixMain.setTranslate(rect.left + positionX, rect.top + positionY);
        matrixMain.postScale(scaleValueFinal, scaleValueFinal,getWidth()/2,getHeight()/2);
        Log.d("TAG", "getWidth(): "+getWidth()+" getHeight(): "+getHeight());
        Log.d("TAG", "rectW : "+rect.width()+" rectH: "+rect.width());
        Log.d("TAG", "finalWidth: "+finalWidth+" finalHeight: "+finalHeight);



        //   matrixMain.postScale(scaleValueFinal, scaleValueFinal, rect.left / 2 + mBitmap.getWidth() / 2, rect.left / 2 + mBitmap.getHeight() / 2);
        canvas.drawBitmap(mBitmap, matrixMain, null);
    }

    private void scaleBackGroundMatrix(Canvas canvas,RectF rect,Matrix matrix) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

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

        canvas.save();
        canvas.clipRect(rect);
        canvas.drawBitmap(mBackGroundBitmap, matrix, null);
        canvas.drawRect(rect, paint);
        canvas.restore();
    }
}
