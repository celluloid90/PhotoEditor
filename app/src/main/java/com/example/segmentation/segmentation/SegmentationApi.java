package com.example.segmentation.segmentation;

import com.example.photo_editor.BuildConfig;
import com.example.segmentation.segmentation.models.Base64_RequestModel;
import com.example.segmentation.segmentation.models.Base64_ResponseModel;
import com.example.segmentation.segmentation.models.InternalSegmentedDemoData;
import com.example.segmentation.segmentation.models.SegmentationFileUploadedData;
import com.example.segmentation.segmentation.models.SegmentedImageResponseData;
import com.example.segmentation.segmentation.models.SegmentedUidData;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
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


    //    Internal Demo calling.
    @Multipart
    @POST("api/segmentation/")
    Call<InternalSegmentedDemoData> segmentedDemoData(@Part MultipartBody.Part part);


    //    Base 64.
    @Headers({"Content-Type: application/json", "Authorization: Bearer c8aca83933b1773122ba65ed6429f6f13c61yu8aacecdfff0bfa9bb714f01de6"})
    @POST("segment-image")
    Call<Base64_ResponseModel> segmentedDemoData(@Body Base64_RequestModel model);
}
