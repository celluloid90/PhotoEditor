package com.example.segmentation;

import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

    private static final int REQUEST_WRITE_PERMISSION = 999;
    private Uri imagePath;
    private ImageView segmentedBg;
    private TouchImageView segmentedImageView;
    private Button saveBtn, gallery;
    private CircularProgressIndicator progressBar;
    private TextView progressText;
    private boolean processOn = false;
    private CardView black, white;
    private int colorBlack = Color.BLACK, colorWhite = 0;
    private String imageName;
    private Button preview;
    private Bitmap originalBitmap, segmentedBitmap;
    private boolean isLongPressed = false;
    private RadioGroup radioGroup;
    private int selectedButtonId = 1;
    private Bitmap showedImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        segmentedImageView = (TouchImageView) findViewById(R.id.segmented_image_view);
        saveBtn = findViewById(R.id.saveBnt);
        gallery = findViewById(R.id.openGallery);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progress_text);
        segmentedBg = findViewById(R.id.segment_bg);
        black = findViewById(R.id.color_black_container);
        white = findViewById(R.id.color_white_container);
        preview = findViewById(R.id.preview);
        radioGroup = findViewById(R.id.radioGroup);
        RadioButton defaultRadioButton = findViewById(R.id.radioOption1);
        defaultRadioButton.setChecked(true);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioOption1) {
                    selectedButtonId = 1;
                    Log.d("shdgsgedge 1", " " + checkedId);
                } else if (checkedId == R.id.radioOption2) {
                    selectedButtonId = 2;
                    Log.d("shdgsgedge 2", " " + checkedId);
                } else {
                    selectedButtonId = 3;
                    Log.d("shdgsgedge 3", " " + checkedId);
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryFolder();
            }
        });

        segmentedImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasStoragePermission(REQUEST_WRITE_PERMISSION)) {
                    saveToStorage();
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

        preview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongPressed = true;
                segmentedImageView.setImageBitmap(originalBitmap);
                return true;
            }
        });

        preview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                preview.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (isLongPressed) {
                        isLongPressed = false;
                        segmentedImageView.setImageBitmap(segmentedBitmap);
                    }
                }
                return false;
            }
        });
    }

    private void saveToStorage() {
        if (showedImg != null) {
            SaveBitmapWrapperKt.saveToGallery(MainActivity.this, changeBgColor(showedImg), "Pixelcut", imageName);
            Toast.makeText(MainActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "No image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasStoragePermission(int requestCode) {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_WRITE_PERMISSION) {
                saveToStorage();
            }
        }
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

        showedImg = null;
        processOn = true;
        segmentedImageView.setVisibility(View.INVISIBLE);
        segmentedBg.setVisibility(View.INVISIBLE);
        preview.setVisibility(View.INVISIBLE);
        findViewById(R.id.processed).setVisibility(View.INVISIBLE);
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
                segmentedImageView.resetView(MainActivity.this);
                segmentedImageView.requestLayout();
                segmentedImageView.setVisibility(View.VISIBLE);
                imagePath = data.getData();
                imageName = queryName(imagePath).split("\\.")[0];

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                    bitmap = Utils.rotateBitmap(MainActivity.this, imagePath.toString(), bitmap);
                    bitmap = ScaleBitmapWrapperKt.scaleIfNeeded(bitmap, 1920);
                    segmentedImageView.setImageBitmap(bitmap);
                    originalBitmap = bitmap;
                    prepareSegmentation(bitmap);
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
                    public void onCompleted(Uri parse, Bitmap b) {
                        Bitmap bitmap = null;
                        try {
                            if (b != null) {
                                bitmap = b;
                            } else {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), parse);
                            }

                            showedImg = bitmap;
                            if (bitmap == null) {
                                onError("error");
                                return;
                            } else {
                                segmentedImageView.setImageBitmap(bitmap);
                                segmentedBitmap = bitmap;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }

//                        segmentedBg.setVisibility(View.VISIBLE);
                        segmentedBg.setBackground(getDrawable(R.drawable.bg_save_block));
                        progressBar.setVisibility(View.INVISIBLE);
                        progressText.setVisibility(View.INVISIBLE);
                        preview.setVisibility(View.VISIBLE);
                        findViewById(R.id.processed).setVisibility(View.VISIBLE);
                        processOn = false;
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(MainActivity.this, "Segmentation Failed.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        progressText.setVisibility(View.INVISIBLE);
                        processOn = false;
                    }
                }, selectedButtonId);
            }
        }, 50);
    }
}