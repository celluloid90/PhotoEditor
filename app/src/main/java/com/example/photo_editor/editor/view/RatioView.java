package com.example.photo_editor.editor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.photo_editor.editor.model.DataModel;
import com.example.photo_editor.editor.utils.BackgroundType;
import com.example.photo_editor.editor.utils.BlurBitmap;
import com.example.photo_editor.editor.utils.CheckButtonType;
import com.example.photo_editor.editor.utils.RoateImage;

public class RatioView extends View {

    float pointAx;
    float pointAy;
    float pointBx;
    float pointBy;
    private Uri uri;
    private Bitmap mBitmap;
    private Bitmap mBackGroundBitmap;
    private Bitmap mGalleryBackgroundBm;
    private Matrix bgMatrix;
    private Matrix matrixMain;
    private float mRatio;
    private float bitmapRatio;
    private float viewRatio;
    private float initialScale = .5f;
    private float centerX = initialScale, centerY = initialScale;
    private float bmLeft, bmTop, bmRight, bmBottom;
    private RectF rectF;
    private float bitmapWidth;
    private float bitmapHeight;
    private float finalWidth;
    private float finalHeight;
    private float x, y;
    float saveScale = 1f;
    CheckButtonType checkButtonType;
    private float mScaleFactor = 1.0f;
    private ScaleGestureDetector mScaleDetector;
    boolean buttonClicked = false;
    boolean touched = false;
    private DataModel dataModel;
    boolean centerBool = false;
    boolean leftBool = false;
    boolean rightBool = false;
    boolean leftBoolX = false;
    boolean leftBoolY = false;
    boolean rightBoolY = false;
    boolean rightBoolX = false;
    boolean isDoubleTouched = false;
    private static final int LINE_SPACING = 20;
    private Paint paint, paintEgds;
    float rotation;
    double degree;
    private double mLastAngle = 0;
    float moveX, moveY;
    double rotaionNew;
    BackgroundType backgroundType;
    boolean edges = false;


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {
             mScaleFactor *= detector.getScaleFactor();
            return true;
        }
    }

    public RatioView(Context context) {
        super(context);
        init(null, context);
    }

    public RatioView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(null, context);
    }

    public RatioView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(null, context);
    }

    public RatioView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(null, context);
    }

    public void init(@Nullable AttributeSet set, Context context) {

        bgMatrix = new Matrix();
        matrixMain = new Matrix();
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());

        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        dataModel = new DataModel();
        paint = new Paint();
        paintEgds = new Paint();
    }

    public float getViewHeight() {
        return getHeight();
    }

    public float getViewWidth() {
        return getWidth();
    }

    public float getFinalWidth() {
        return finalWidth;
    }

    public float getFinalHeight() {
        return finalHeight;
    }

    public void setImageUri(Uri uri) {
        // this.uri = uri;
        mBitmap = RoateImage.getRotatedBitmap(getContext(), uri);
        mBackGroundBitmap = RoateImage.getRotatedBitmap(getContext(), uri);
        mBackGroundBitmap = BlurBitmap.Companion.blurBitmap(mBackGroundBitmap, getContext());
        invalidate();
    }

    public void setOriginalRatio() {
        centerY = .5f;
        centerX = .5f;
        mScaleFactor = saveScale;
    }

    public void setBackBround(BackgroundType backgroundType) {
        this.backgroundType = backgroundType;
    }

    public void setgellaryUri(Uri uri) {
        mGalleryBackgroundBm = RoateImage.getRotatedBitmap(getContext(), uri);
        invalidate();
    }

    public void checkClickedButtonType(CheckButtonType checkButtonType) {

        buttonClicked = true;
        touched = false;
        this.checkButtonType = checkButtonType;
        // float ratio = dataModel.setViewRatio(this);
        //centerX = dataModel.setCenterX(this);
        // centerY = dataModel.setCenterY(this);
        if (checkButtonType.equals(CheckButtonType.CENTER)) {
            centerX = initialScale;
            centerY = initialScale;
            centerBool = true;
            leftBool = false;
            rightBool = false;
        }
        if (checkButtonType.equals(CheckButtonType.LEFT)) {
            centerBool = false;
            leftBool = true;
            rightBool = false;
            if (getWidth() > getHeight()) {
                centerX = ((finalWidth / 2) / getWidth());
                centerY = initialScale;
                leftBoolX = true;
                leftBoolY = false;
            } else {
                centerY = ((finalHeight) / 2) / getHeight();
                centerX = initialScale;
                leftBoolY = true;
                leftBoolX = false;
            }
        }
        if (checkButtonType.equals(CheckButtonType.RIGHT)) {
            centerBool = false;
            leftBool = false;
            rightBool = true;

            if (getWidth() > getHeight()) {
                centerX = (1 - (finalWidth / 2) / getWidth());
                centerY = initialScale;
                rightBoolX = true;
                rightBoolY = false;
            } else {
                centerY = (1 - (finalHeight / 2) / getHeight());
                centerX = initialScale;
                rightBoolY = true;
                rightBoolX = false;
            }
        }

        invalidate();

    }

    public CheckButtonType mCheckButtonType() {
        return checkButtonType;
    }

    private void scaleForegroundImageCanvas() {
        bitmapWidth = mBitmap.getWidth();
        bitmapHeight = mBitmap.getHeight();

        bitmapRatio = bitmapWidth / bitmapHeight;
        viewRatio = (float) getWidth() / (float) getHeight();

        finalWidth = getWidth();
        finalHeight = getHeight();
        if (viewRatio >= bitmapRatio) {
            finalWidth = finalHeight * bitmapRatio;
        } else {
            finalHeight = finalWidth / bitmapRatio;
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        scaleForegroundImageCanvas();
        if (buttonClicked) {
            if (leftBool) {
                if (leftBoolY) {
                    if (h > finalHeight) {
                        centerY = (finalHeight / 2) / h;
                    } else {
                        centerY = initialScale;
                    }
                } else if (leftBoolX) {
                    if (w > finalWidth) {
                        centerX = (finalWidth / 2) / w;
                    } else if (h > finalHeight) {
                        centerX = initialScale;
                    }
                }
            }
            if (rightBool) {
                if (rightBoolY) {
                    if (h > finalHeight) {
                        centerY = (1 - (finalHeight / 2) / h);
                    } else {
                        centerY = initialScale;
                    }
                } else if (rightBoolX) {
                    if (w > finalWidth) {
                        centerX = (1 - (finalWidth / 2) / w);
                    } else if (h > finalHeight) {
                        centerX = initialScale;
                    }

                }
            }
        }

    }

    float temp = 0f;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // BackgroundView.createBackground(canvas, mBackGroundBitmap, bgMatrix);
        drawCanvasBackground(canvas);
        bmLeft = getWidth() * centerX - (finalWidth / 2) * mScaleFactor;
        bmTop = getHeight() * centerY - (finalHeight / 2) * mScaleFactor;
        bmRight = getWidth() * centerX + (finalWidth / 2) * mScaleFactor;
        bmBottom = getHeight() * centerY + (finalHeight / 2) * mScaleFactor;
        rectF = new RectF(bmLeft, bmTop, bmRight, bmBottom);
        canvas.save();
        canvas.rotate((float) rotaionNew, rectF.centerX(), rectF.centerY());
        canvas.drawBitmap(mBitmap, null, rectF, null);
        canvas.restore();
        // drawEdgs(canvas);
        drawLines(canvas);
    }

    private void drawEdgs(Canvas canvas) {
        paintEgds.setColor(Color.WHITE);
        paintEgds.setStyle(Paint.Style.STROKE);
        paintEgds.setStrokeWidth(2f);


        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight() / 7, paintEgds);
        canvas.drawLine(getWidth() / 2, getHeight() - getHeight() / 7, getWidth() / 2, getHeight(), paintEgds);

        canvas.drawLine(0, getHeight() / 2, getWidth() / 7, getHeight() / 2, paintEgds);
        canvas.drawLine(getWidth(), getHeight() / 2, getWidth() - getWidth() / 7, getHeight() / 2, paintEgds);


    }

    private void drawCanvasBackground(Canvas canvas) {
        if (backgroundType == BackgroundType.WHITE) {
            canvas.drawColor(Color.WHITE);
        } else if (backgroundType == BackgroundType.BLACK) {
            canvas.drawColor(Color.BLACK);
        } else if (backgroundType == BackgroundType.BLUR) {
            BackgroundView.createBackground(canvas, mBackGroundBitmap, bgMatrix);
        } else if (backgroundType == BackgroundType.PHOTO) {
            if (mGalleryBackgroundBm != null) {
                BackgroundView.createBackground(canvas, mGalleryBackgroundBm, bgMatrix);
            }
        } else if (backgroundType == BackgroundType.GRADIENT) {
            int color = mBitmap.getPixel(mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
            int color2 = mBitmap.getPixel(0, 0);
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            int r1 = Color.red(color2);
            int g1 = Color.green(color2);
            int b1 = Color.blue(color2);

            Paint m_Paint = new Paint();
            m_Paint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(), Color.rgb(r, g, b), Color.rgb(r1, g1, b1), Shader.TileMode.REPEAT));
            canvas.drawPaint(m_Paint);
        }
        invalidate();
    }

    private void drawLines(Canvas canvas) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        paint.setPathEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        if (isDoubleTouched) {
            canvas.drawLine((rectF.left + rectF.right) / 2, rectF.top - 50,
                    (rectF.left + rectF.right) / 2, rectF.bottom + 50, paint);
            canvas.drawLine(rectF.left, (rectF.top + rectF.bottom) / 2,
                    rectF.right, (rectF.top + rectF.bottom) / 2, paint);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touched = true;
        buttonClicked = false;
        mScaleDetector.onTouchEvent(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN: {
                if (event.getPointerCount() > 1) {
                    pointAx = event.getX(0);
                    pointAy = event.getY(0);
                    pointBx = event.getX(1);
                    pointBy = event.getY(1);
                }
            }
            case MotionEvent.ACTION_UP: {
                mLastAngle = rotaionNew;
            }
        }

        if (event.getPointerCount() > 1) {
            isDoubleTouched = true;
            doRotationEvent(event);
        } else {
            isDoubleTouched = false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                x = event.getX();
                y = event.getY();

            }
            case MotionEvent.ACTION_MOVE: {


                moveX = event.getX();
                moveY = event.getY();

                centerX += ((moveX - x) / getWidth());
                centerY += ((moveY - y) / getHeight());
                x = moveX;
                y = moveY;


            }
            case MotionEvent.ACTION_UP: {

            }
        }
        invalidate();
        return true;

    }

    private boolean doRotationEvent(MotionEvent event) {
        //Calculate the angle between the two fingers
        float deltaX = event.getX(0) - event.getX(1);
        float deltaY = event.getY(0) - event.getY(1);
        double radians = Math.atan(deltaY / deltaX);
        //Convert to degrees
        int degrees = (int) (radians * 180 / Math.PI);


        float pointCx;
        float pointCy;
        float pointDx;
        float pointDy;
        double angle1;
        double angle2;

        /*
         * Must use getActionMasked() for switching to pick up pointer events.
         * These events have the pointer index encoded in them so the return
         * from getAction() won't match the exact action constant.
         */
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                //Mark the initial angle

                break;
            case MotionEvent.ACTION_MOVE:


                pointCx = event.getX(0);
                pointDx = event.getX(1);
                pointCy = event.getY(0);
                pointDy = event.getY(1);
                angle1 = Math.atan2((pointAy - pointBy), (pointAx - pointBx));
                angle2 = Math.atan2((pointCy - pointDy), (pointCx - pointDx));
                rotaionNew = -((angle1 - angle2) * (180F / Math.PI));

                rotaionNew += mLastAngle;

                rotaionNew = rotaionNew % 360;
                Log.d("TAG", "doRotationEvent: " + rotaionNew);


                //rotation = (float) (degrees - mLastAngle);

                // mLastAngle = degrees;

                break;
        }

        return true;
    }
}
