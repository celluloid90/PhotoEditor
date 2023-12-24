package com.example.segmentation.segmentation;

import com.example.photo_editor.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SegmentationApiClient {
    private static Retrofit retrofitFileUpload;
    private static Retrofit retrofitSegmentImage;
    private static Retrofit retrofitDemo;
    private static Retrofit retrofitBase64;
    public static final String SEGMENTATION_BASE_URL = BuildConfig.SEGMENTATION_BASE_URL;
    public static final String SEGMENTATION_IMAGE_UPLOAD_BASE_URL = BuildConfig.SEGMENTATION_IMAGE_UPLOAD_BASE_URL;
    public static final String SEGMENTATION_BASE_64 = "https://57bk3pfl59zvti-8080.proxy.runpod.net/";
    public static final String SEGMENTATION_DEMO_URL = "http://103.4.146.174:8222/";

    public static Retrofit getRetrofitInstanceForFileUpload() {
        if (retrofitFileUpload == null) {
            retrofitFileUpload = new Retrofit.Builder().baseUrl(SEGMENTATION_IMAGE_UPLOAD_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofitFileUpload;
    }

    public static Retrofit getRetrofitInstanceForSegmentImage() {
        if (retrofitSegmentImage == null) {
            retrofitSegmentImage = new Retrofit.Builder().baseUrl(SEGMENTATION_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofitSegmentImage;
    }

    public static Retrofit getRetrofitInstanceForDemo() {
        if (retrofitDemo == null) {
            retrofitDemo = new Retrofit.Builder().baseUrl(SEGMENTATION_DEMO_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofitDemo;
    }

    public static Retrofit getRetrofitInstanceBase64() {
        if (retrofitBase64 == null) {
            retrofitBase64 = new Retrofit.Builder().baseUrl(SEGMENTATION_BASE_64).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofitBase64;
    }
}
