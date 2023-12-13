package com.example.segmentation.ai;

import android.graphics.Bitmap;

/**
 * @author: Raihan Uddin Piash (raihan.uddin@braincraftapps.com)
 * @project: StickerMaker
 * @date: On 5/15/23 at 12:41 PM
 */
public class ProcessedBitmapSingleton {
    private static ProcessedBitmapSingleton single_instance;
    private Bitmap bitmap = null;

    private ProcessedBitmapSingleton() {
    }

    public static ProcessedBitmapSingleton getInstance() {
        if (single_instance == null) {
            single_instance = new ProcessedBitmapSingleton();
        }
        return single_instance;
    }

    public void setProcessedBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getProcessedBitmap() {
        return bitmap;
    }

    public void clearProcessedBitmap() {
        if (this.bitmap != null && !this.bitmap.isRecycled()) {
            this.bitmap.recycle();
        }

        this.bitmap = null;
    }
}
