package com.example.segmentation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.photo_editor.R;
import com.linecorp.apng.ApngDrawable;
import com.linecorp.apng.decoder.ApngException;

import java.io.IOException;
import java.util.ArrayList;

public class ApngLoad extends AppCompatActivity {

    //    https://github.com/line/apng-drawable
    private ImageView imageView;
    private SeekBar seekBar;
    private ArrayList<Bitmap> normalList = new ArrayList<>();
    private ArrayList<Bitmap> mirrorList = new ArrayList<>();
    private GridView gridView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apng_load);

        imageView = findViewById(R.id.imageView);
        seekBar = findViewById(R.id.seekbar);
        gridView = findViewById(R.id.grid_view);
        button = findViewById(R.id.button);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    ApngDrawable drawable = ApngDrawable.Companion.decode(getAssets(), "image.png", 360, 360);
                    imageView.setImageDrawable(drawable);
                    drawable.setLoopCount(1);

//                    seekBar.setMax(drawable.getDurationMillis()-1);
                    seekBar.setMax(drawable.getFrameCount() - 1);

                    Log.d("shaskdgbsagsdg", "start");
                    for (int i = 0; i < drawable.getFrameCount(); i++) {
//                        drawable.seekTo(i);
                        drawable.seekToFrame(drawable.getCurrentLoopIndex(), i);
                        Bitmap bitmap = drawableToBitmap(drawable.getCurrent());
                        normalList.add(bitmap);
                        Bitmap bitmap1 = mirror(bitmap);
                        mirrorList.add(bitmap1);
                    }
                    Log.d("shaskdgbsagsdg", "end");

//                    drawable.seekTo(0);
                    drawable.seekToFrame(drawable.getCurrentLoopIndex(), 0);

                    Log.d("shaskdgbsagsdg", drawable.getDurationMillis() + " " + drawable.getFrameCount() + " " + normalList.size() + " " + mirrorList.size());

//                    loadBitmapToView(mirrorList, mirrorImageLayout);
                    button.setText("Mirror");
                    gridView.setAdapter(new ImageAdapter(ApngLoad.this, normalList));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (button.getText().equals("Regular")){
                                button.setText("Mirror");
                                gridView.setAdapter(new ImageAdapter(ApngLoad.this, normalList));
                            }else {
                                button.setText("Regular");
                                gridView.setAdapter(new ImageAdapter(ApngLoad.this, mirrorList));
                            }
                        }
                    });

                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            Log.d("shaskdgbsagsdg", i + " ");
//                            drawable.seekTo(i);
                            drawable.seekToFrame(drawable.getCurrentLoopIndex(), i);
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });
                    Log.d("shdgbsegs", drawable.getFrameCount() + " " + drawable.getCurrentFrameIndex() + " " + drawable.getCurrentLoopIndex());
                } catch (ApngException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private Bitmap mirror(Bitmap src) {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return dst;
    }
}