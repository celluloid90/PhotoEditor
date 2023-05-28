package com.example.photo_editor.editor.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.Log;
import android.widget.Toast;

import com.example.photo_editor.editor.enums.BackgroundType;
import com.example.photo_editor.editor.enums.CanvasBackgroundType;
import com.example.photo_editor.editor.utils.CheckButtonType;

public class BackgroundView {

    public static void setBackground(CanvasBackgroundType canvasBackgroundType, Canvas canvas,
                                     Bitmap mBackGroundBitmap, Matrix bgMatrix, Bitmap galleryImageBitmap,
                                     Bitmap bitmap, float height, float width,String mcolor) {

        if (canvasBackgroundType == CanvasBackgroundType.WHITE) {
            canvas.drawColor(Color.WHITE);
        } else if (canvasBackgroundType == CanvasBackgroundType.BLACK) {
            canvas.drawColor(Color.BLACK);
        } else if (canvasBackgroundType == CanvasBackgroundType.BLUR) {
            BackgroundView.createBackground(canvas, mBackGroundBitmap, bgMatrix);
        } else if (canvasBackgroundType == CanvasBackgroundType.PHOTO) {
            if (galleryImageBitmap != null) {
                BackgroundView.createBackground(canvas, galleryImageBitmap, bgMatrix);
            }
        } else if (canvasBackgroundType == CanvasBackgroundType.GRADIENT) {
            int color = bitmap.getPixel(bitmap.getWidth() / 2, bitmap.getHeight() / 2);
            int color2 = bitmap.getPixel(0, 0);
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            int r1 = Color.red(color2);
            int g1 = Color.green(color2);
            int b1 = Color.blue(color2);

            Paint m_Paint = new Paint();
            m_Paint.setShader(new LinearGradient(0, 0, width, height, Color.rgb(r, g, b), Color.rgb(r1, g1, b1), Shader.TileMode.REPEAT));
            canvas.drawPaint(m_Paint);
        } else if (canvasBackgroundType == CanvasBackgroundType.COLOR) {
            canvas.drawColor(Color.parseColor(mcolor));
        }
    }

    public static void createBackground(Canvas canvas, Bitmap mBackGroundBitmap, Matrix bgMatrix) {
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
