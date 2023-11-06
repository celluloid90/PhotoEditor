package com.example.segmentation.segmentation;

import com.example.photo_editor.BuildConfig;
import com.example.segmentation.segmentation.models.SegmentationFileUploadedData;
import com.example.segmentation.segmentation.models.SegmentedImageResponseData;
import com.example.segmentation.segmentation.models.SegmentedUidData;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * @author: Raihan Uddin Piash (raihan.uddin@braincraftapps.com)
 * @project: StickerMaker
 * @date: On 7/18/23 at 2:41 PM
 */
public interface SegmentationApi {

    @Headers({BuildConfig.SEGMENTATION_ACCESS_KEY,})
    @Multipart
    @POST("file-upload")
    Call<SegmentationFileUploadedData> segmentation_FileUpload(@Part MultipartBody.Part imagePart, @Part MultipartBody.Part bucketNamePart);

    @Headers({BuildConfig.SEGMENTATION_ACCESS_KEY,})
    @Multipart
    @POST("segment-image")
    Call<SegmentedUidData> segmentation_segmentImage(@Part MultipartBody.Part part);

    @Headers({BuildConfig.SEGMENTATION_ACCESS_KEY,})
    @GET("info?")
    Call<SegmentedImageResponseData> getSegmentationResult(@Query("uid") String uid);
}
