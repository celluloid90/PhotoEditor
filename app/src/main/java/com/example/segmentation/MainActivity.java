package com.example.segmentation;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.photo_editor.R;
import com.example.segmentation.segmentation.SaveBitmapWrapperKt;
import com.example.segmentation.segmentation.ScaleBitmapWrapperKt;
import com.example.segmentation.segmentation.SegmentationWithApi;
import com.example.segmentation.segmentation.Utils;
import com.example.segmentation.segmentation.models.SegmentedImageDownloadListener;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Uri imagePath, segmentedPath;
    private ImageView segmentedImageView, segmentedBg;
    private Button saveBtn, gallery;
    private CircularProgressIndicator progressBar;
    private TextView progressText;
    private boolean processOn = false;
    private CardView black, white;
    private int colorBlack = Color.BLACK, colorWhite = 0;
    private String imageName;
    private final int maxImageSize = 640;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        segmentedImageView = findViewById(R.id.segmented_image_view);
        saveBtn = findViewById(R.id.saveBnt);
        gallery = findViewById(R.id.openGallery);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progress_text);
        segmentedBg = findViewById(R.id.segment_bg);
        black = findViewById(R.id.color_black_container);
        white = findViewById(R.id.color_white_container);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryFolder();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (segmentedPath != null) {
                    try {
                        Bitmap showedImg = MediaStore.Images.Media.getBitmap(getContentResolver(), segmentedPath);
                        SaveBitmapWrapperKt.saveToGallery(MainActivity.this, changeBgColor(showedImg), "Pixelcut", imageName);
                        Toast.makeText(MainActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (processOn) return;

                black.setAlpha(1f);
                white.setAlpha(.3f);
                colorBlack = Color.BLACK;
                colorWhite = 0;
            }
        });

        white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (processOn) return;

                black.setAlpha(.3f);
                white.setAlpha(1f);
                colorWhite = Color.WHITE;
                colorBlack = 0;
            }
        });
    }

    private Bitmap changeBgColor(Bitmap showedImg) {
        Bitmap imageWithBG = Bitmap.createBitmap(showedImg.getWidth(), showedImg.getHeight(), showedImg.getConfig());
        if (colorWhite != 0) {
            imageWithBG.eraseColor(colorWhite);
        } else {
            imageWithBG.eraseColor(colorBlack);
        }
        Canvas canvas = new Canvas(imageWithBG);
        canvas.drawBitmap(showedImg, 0f, 0f, null);
        showedImg.recycle();
        return imageWithBG;
    }

    private void openGalleryFolder() {
        if (processOn) return;

        segmentedPath = null;
        processOn = true;
        segmentedImageView.setVisibility(View.INVISIBLE);
        segmentedBg.setVisibility(View.INVISIBLE);
        findViewById(R.id.processed).setVisibility(View.GONE);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
        intent.setDataAndType(uri, "image/*");
        startActivityForResult(Intent.createChooser(intent, "Open folder"), 100);
    }

    private String queryName(Uri uri) {
        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            if (data != null) {
                segmentedImageView.setVisibility(View.VISIBLE);
                imagePath = data.getData();
                imageName = queryName(imagePath).split("\\.")[0];

                try {
                    segmentedImageView.setImageURI(imagePath);

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                    bitmap = Utils.rotateBitmap(MainActivity.this, imagePath.getPath(), bitmap);

                    prepareSegmentation(ScaleBitmapWrapperKt.scaleIfNeeded(bitmap, maxImageSize));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                processOn = false;
            }
        } else {
            processOn = false;
        }
    }

    private void prepareSegmentation(Bitmap bitmap) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                progressText.setVisibility(View.VISIBLE);
                progressText.setText("Processing...");

                new SegmentationWithApi().initSegmentationWithApi(MainActivity.this, bitmap, new SegmentedImageDownloadListener() {
                    @Override
                    public void onCompleted(Uri parse) {
                        segmentedPath = parse;
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), parse);
                            bitmap = Utils.rotateBitmap(MainActivity.this, imagePath.getPath(), bitmap);
                            segmentedImageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        segmentedBg.setVisibility(View.VISIBLE);
                        segmentedBg.setBackground(getDrawable(R.drawable.bg_save_block));
                        progressBar.setVisibility(View.INVISIBLE);
                        progressText.setVisibility(View.INVISIBLE);
                        findViewById(R.id.processed).setVisibility(View.VISIBLE);
                        processOn = false;
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(MainActivity.this, "Segmentation Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 50);
    }
}