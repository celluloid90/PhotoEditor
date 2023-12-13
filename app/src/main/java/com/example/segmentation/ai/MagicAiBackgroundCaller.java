package com.example.segmentation.ai;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.example.segmentation.ai.eraser.data.Output;

/**
 * @author: Raihan Uddin Piash (raihan.uddin@braincraftapps.com)
 * @project: StickerMaker
 * @date: On 5/11/23 at 2:50 PM
 */
public class MagicAiBackgroundCaller extends AsyncTask<Bitmap, Integer, Bitmap> {

    private MagicAiProgressListener progressListener;
    private Context context;

    public MagicAiBackgroundCaller(Context context, MagicAiProgressListener progressListener) {
        this.context = context;
        this.progressListener = progressListener;
    }

    @Override
    protected Bitmap doInBackground(Bitmap... bitmaps) {
        AiEraserManager manager = AiEraserManager.Companion.getInstance(context);
        Output magicBitmap = manager.eraseWithAi(bitmaps[0]);

        if (magicBitmap != null) {
            return magicBitmap.getBitmap();
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        progressListener.onMagicDone(bitmap);
    }
}
