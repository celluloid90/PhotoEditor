package com.example.photo_editor.editor.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;

public class BackgroundView {

    public static void createBackground(Canvas canvas, Bitmap mBackGroundBitmap, Matrix bgMatrix){
        float scalemWidth = (float) canvas.getWidth() / mBackGroundBitmap.getWidth();
        float scalemHeight = (float) canvas.getHeight() / mBackGroundBitmap.getHeight();
        float maxScale = Math.max(scalemWidth, scalemHeight);
        canvas.drawColor(Color.GREEN);
        bgMatrix.setScale(maxScale, maxScale, mBackGroundBitmap.getWidth() / 2, mBackGroundBitmap.getHeight() / 2);

        bgMatrix.postTranslate(Math.round(canvas.getWidth() - mBackGroundBitmap.getWidth()) * .5f,
                Math.round(canvas.getHeight() - mBackGroundBitmap.getHeight()) * .5f);
        canvas.drawBitmap(mBackGroundBitmap, bgMatrix, null);
    }
}
