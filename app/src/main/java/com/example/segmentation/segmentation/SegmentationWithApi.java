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
    private int selectedBtnId;

    public void initSegmentationWithApi(Context context, Bitmap bitmap, SegmentedImageDownloadListener listener, int selectedBtnId) {
        this.listener = listener;
        this.context = context;
        this.preProcessedBitmap = bitmap;
        this.selectedBtnId = selectedBtnId;

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                startSegmentation();
            }
        });
    }

    private void startSegmentation() {
        Utils.deleteFolderContents("Local", context);
        new ApiSegmentationManager(context, new SegmentedImageDownloadListener() {
            @Override
            public void onCompleted(Uri imagePath, Bitmap b) {
                listener.onCompleted(imagePath, b);
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError(errorMessage);
            }
        }).startImageSegmentation(preProcessedBitmap, selectedBtnId);
    }
}
