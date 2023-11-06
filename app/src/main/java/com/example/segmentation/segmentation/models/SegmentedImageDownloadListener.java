package com.example.segmentation.segmentation.models;

import android.net.Uri;

/**
 * @author: Raihan Uddin Piash (raihan.uddin@braincraftapps.com)
 * @project: StickerMaker
 * @date: On 7/19/23 at 12:22 PM
 */
public interface SegmentedImageDownloadListener {
    void onCompleted(Uri parse);

    void onError(String errorMessage);
}
