package com.example.photo_editor.editor.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;

import com.example.photo_editor.R;
import com.example.photo_editor.editor.enums.BorderType;

public class BorderUtils {
    public static void setBorder(float mScaleFactor, BorderType borderType, Canvas canvas,
                                 float bmLeft, float bmTop, float bmRight, float bmBottom,
                                 Context context, Bitmap mBitmap) {
        int borderSize = (int) (12 * mScaleFactor);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (borderType.equals(BorderType.WHITE)) {
            paint.setColor(Color.WHITE);
            canvas.drawRect(bmLeft - borderSize, bmTop - borderSize, bmRight + borderSize, bmBottom + borderSize, paint);
        } else if (borderType.equals(BorderType.BLACK)) {
            paint.setColor(Color.BLACK);
            canvas.drawRect(bmLeft - borderSize, bmTop - borderSize, bmRight + borderSize, bmBottom + borderSize, paint);
        } else if (borderType.equals(BorderType.NONE)) {
            paint.setColor(Color.TRANSPARENT);
            canvas.drawRect(bmLeft - borderSize, bmTop - borderSize, bmRight + borderSize, bmBottom + borderSize, paint);
        } else if (borderType.equals(BorderType.COLOR)) {
            int[] rainbow = context.getResources().getIntArray(R.array.rainbow);
            Shader shader = new SweepGradient(mBitmap.getWidth() / 2, mBitmap.getHeight() / 2, rainbow, null);
            paint.setShader(shader);
            canvas.drawRect(bmLeft - borderSize, bmTop - borderSize, bmRight + borderSize, bmBottom + borderSize, paint);
        }

    }
}
