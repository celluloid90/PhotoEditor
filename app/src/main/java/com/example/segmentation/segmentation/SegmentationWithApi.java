package com.example.segmentation.segmentation;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.lifecycle.ViewModel;

import com.example.segmentation.segmentation.models.SegmentedImageDownloadListener;

import java.util.concurrent.Executors;

public class SegmentationWithApi extends ViewModel {
    private Context context;
    private Bitmap preProcessedBitmap;

    private SegmentedImageDownloadListener listener;

    public void initSegmentationWithApi(Context context, Bitmap bitmap, SegmentedImageDownloadListener listener) {
        this.listener = listener;
        this.context = context;
        this.preProcessedBitmap = bitmap;

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                startSegmentation();
            }
        });
    }

    private void startSegmentation() {
        new ApiSegmentationManager(context, new SegmentedImageDownloadListener() {
            @Override
            public void onCompleted(Uri imagePath) {
                listener.onCompleted(imagePath);
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError(errorMessage);
            }
        }).startImageSegmentation(preProcessedBitmap);
    }
}
