package com.example.segmentation.segmentation.models;

import com.google.gson.annotations.SerializedName;

/**
 * @author: Raihan Uddin Piash (raihan.uddin@braincraftapps.com)
 * @project: StickerMaker
 * @date: On 10/30/23 at 5:23 PM
 */
public class InternalSegmentedDemoData {
    @SerializedName("mask_url")
    private String maskUrl;

    public String getMaskUrl() {
        return maskUrl;
    }

    public void setMaskUrl(String maskUrl) {
        this.maskUrl = maskUrl;
    }
}
