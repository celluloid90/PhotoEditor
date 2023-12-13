package com.example.segmentation.segmentation;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;

import com.example.segmentation.ai.MagicAiBackgroundCaller;
import com.example.segmentation.ai.MagicAiProgressListener;
import com.example.segmentation.segmentation.models.InternalSegmentedDemoData;
import com.example.segmentation.segmentation.models.SegmentationFileUploadedData;
import com.example.segmentation.segmentation.models.SegmentedImageDownloadListener;
import com.example.segmentation.segmentation.models.SegmentedImageResponseData;
import com.example.segmentation.segmentation.models.SegmentedUidData;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author: Raihan Uddin Piash (raihan.uddin@braincraftapps.com)
 * @project: StickerMaker
 * @date: On 10/30/23 at 5:44 PM
 */
public class ApiSegmentationManager {
    private Context context;
    private String uid;
    private SegmentationApi segmentedImageCaller;
    public static final String SEGMENTED_FOLDER = "SegmentedImage";
    private final String MULTIPART_S3_BUCKET_NAME = "s3_bucket_name";
    private final String MULTIPART_S3_BUCKET_VALUE = "segmentation-braincraft";
    private final String MULTIPART_IMAGE_KEY = "image";
    private final String MULTIPART_MAIN_IMAGE_KEY = "main_image_key";
    private final String MEDIA_TYPE = "image/*";
    private final String COMPLETE_STATUS = "COMPLETED";
    private SegmentedImageDownloadListener segmentedImageDownloadListener;

    public ApiSegmentationManager(Context context, SegmentedImageDownloadListener segmentedImageDownloadListener) {
        this.context = context;
        this.segmentedImageDownloadListener = segmentedImageDownloadListener;
        segmentedImageCaller = SegmentationApiClient.getRetrofitInstanceForSegmentImage().create(SegmentationApi.class);
    }

    public void startImageSegmentation(Bitmap bitmap, int selectedBtnId) {
        if (selectedBtnId == 1) {
            proceedToApiSegmentation(bitmap);
        } else if (selectedBtnId == 2) {
            startInternalImageSegmentation(bitmap);
        } else {
            startLocalSegmentation(bitmap);
        }
    }

    private void startLocalSegmentation(Bitmap bit) {
        MagicAiBackgroundCaller caller = new MagicAiBackgroundCaller(context, new MagicAiProgressListener() {
            @Override
            public void onMagicDone(Bitmap bitmap) {
                if (bitmap != null) {
                    bitmap.setHasAlpha(true);
                    segmentedImageDownloadListener.onCompleted(null, bitmap);
                } else {
                    segmentedImageDownloadListener.onError("Failed");
                }
            }
        });

        if (bit != null) caller.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bit);
    }

    private void startInternalImageSegmentation(Bitmap bitmap) {
        internalImageSegment(bitmap);
    }

    private void internalImageSegment(Bitmap bitmap) {
        File file = Utils.saveBeforeSegmentationBitmapToStorage(bitmap, context);
        RequestBody requestBody = RequestBody.create(MediaType.parse(MEDIA_TYPE), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData(MULTIPART_IMAGE_KEY, file.getName(), requestBody);

        SegmentationApi segmentationApi = SegmentationApiClient.getRetrofitInstanceForDemo().create(SegmentationApi.class);
        Call<InternalSegmentedDemoData> call = segmentationApi.segmentedDemoData(part);
        call.enqueue(new Callback<InternalSegmentedDemoData>() {
            @Override
            public void onResponse(@NotNull Call<InternalSegmentedDemoData> call, @NotNull Response<InternalSegmentedDemoData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    segmentDemoData(response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<InternalSegmentedDemoData> call, @NotNull Throwable t) {
                segmentedImageDownloadListener.onError(t.getMessage());
            }
        });
    }

    private void segmentDemoData(InternalSegmentedDemoData body) {
        downloadSegmentedImage(body.getMaskUrl());
    }

    private void proceedToApiSegmentation(Bitmap bitmap) {
        File file = Utils.saveBeforeSegmentationBitmapToStorage(bitmap, context);
        RequestBody requestBody = RequestBody.create(MediaType.parse(MEDIA_TYPE), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData(MULTIPART_IMAGE_KEY, file.getName(), requestBody);
        MultipartBody.Part part1 = MultipartBody.Part.createFormData(MULTIPART_S3_BUCKET_NAME, MULTIPART_S3_BUCKET_VALUE);

        SegmentationApi segmentationApi = SegmentationApiClient.getRetrofitInstanceForFileUpload().create(SegmentationApi.class);
        Call<SegmentationFileUploadedData> call = segmentationApi.segmentation_FileUpload(part, part1);
        call.enqueue(new Callback<SegmentationFileUploadedData>() {
            @Override
            public void onResponse(@NotNull Call<SegmentationFileUploadedData> call, @NotNull Response<SegmentationFileUploadedData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    segmentImage(response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<SegmentationFileUploadedData> call, @NotNull Throwable t) {
                segmentedImageDownloadListener.onError(t.getMessage());
            }
        });
    }

    private void segmentImage(SegmentationFileUploadedData body) {
        MultipartBody.Part part = MultipartBody.Part.createFormData(MULTIPART_MAIN_IMAGE_KEY, body.getMainImageKey());

        SegmentationApi segmentationApi = SegmentationApiClient.getRetrofitInstanceForSegmentImage().create(SegmentationApi.class);
        Call<SegmentedUidData> call = segmentationApi.segmentation_segmentImage(part);
        call.enqueue(new Callback<SegmentedUidData>() {
            @Override
            public void onResponse(@NotNull Call<SegmentedUidData> call, @NotNull Response<SegmentedUidData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SegmentedUidData body = response.body();
                    segmentedUidData(body.getImageUid());
                }
            }

            @Override
            public void onFailure(@NotNull Call<SegmentedUidData> call, @NotNull Throwable t) {
                segmentedImageDownloadListener.onError(t.getMessage());
            }
        });
    }

    private void segmentedUidData(String imageUid) {
        uid = imageUid;

        Call<SegmentedImageResponseData> call = segmentedImageCaller.getSegmentationResult(imageUid);
        call.enqueue(new Callback<SegmentedImageResponseData>() {
            @Override
            public void onResponse(@NotNull Call<SegmentedImageResponseData> call, @NotNull Response<SegmentedImageResponseData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    segmentedImageResponse(response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<SegmentedImageResponseData> call, @NotNull Throwable t) {
                segmentedImageDownloadListener.onError(t.getMessage());
            }
        });
    }

    private void segmentedImageResponse(SegmentedImageResponseData body) {
        long timeInMillis = getTimeInMillis(body);

        if (!body.getStatus().equals(COMPLETE_STATUS)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    segmentedUidData(uid);
                }
            }, timeInMillis);
        } else {
            if (body.getResult() != null) {
                downloadSegmentedImage(body.getResult());
            }
        }
    }

    private void downloadSegmentedImage(String imagePath) {
        Utils.deleteFolderContents(SEGMENTED_FOLDER, context);
        DownloadFromServer downloadFromServer = new DownloadFromServer(context);
        downloadFromServer.downloadSegmentedImage(imagePath, new SegmentedImageDownloadListener() {
            @Override
            public void onCompleted(Uri imagePath, Bitmap bitmap) {
                segmentedImageDownloadListener.onCompleted(imagePath, null);
            }

            @Override
            public void onError(String errorMessage) {
                segmentedImageDownloadListener.onError(errorMessage);
            }
        });
    }

    private long getTimeInMillis(SegmentedImageResponseData body) {
        String time = null;
        float timeInSec;
        if (body.getRemainTimeInSec() != null) {
            time = body.getRemainTimeInSec();
            timeInSec = Float.parseFloat(time);
            return (long) (timeInSec * 1000L);
        } else {
            return 10;
        }
    }
}
