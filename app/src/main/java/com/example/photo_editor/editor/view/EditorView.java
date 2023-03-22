package com.example.photo_editor.editor.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class EditorView extends View {

    float mRatio;

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

    public void setRatio(float w, float h) {
        mRatio = (float) w / h;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Log.d("TAG", "width: " + width + " height :" + height);
        canvas.drawColor(Color.RED);

        float rectW = height * mRatio;
        float rectH = width / mRatio;

        Rect rect = new Rect();
        rect.top = (int) ((height - rectH)/2);
        rect.left = (int) ((width - rectW)/2);

        rect.right = (int) (rect.left + rectW);
        rect.bottom = (int) (rect.top + rectH);


        Paint paint =new Paint();
        paint.setColor(Color.GRAY);
        canvas.drawRect(rect.left,rect.top,rect.right,rect.bottom,paint);

    }
}
