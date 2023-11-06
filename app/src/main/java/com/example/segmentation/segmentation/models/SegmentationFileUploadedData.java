package com.example.segmentation.segmentation.models;

import com.google.gson.annotations.SerializedName;

/**
 * @author: Raihan Uddin Piash (raihan.uddin@braincraftapps.com)
 * @project: StickerMaker
 * @date: On 10/30/23 at 5:23 PM
 */
public class SegmentationFileUploadedData {
    @SerializedName("main_image_key")
    private String mainImageKey;

    public String getMainImageKey() {
        return mainImageKey;
    }

    public void setMainImageKey(String mainImageKey) {
        this.mainImageKey = mainImageKey;
    }
}
