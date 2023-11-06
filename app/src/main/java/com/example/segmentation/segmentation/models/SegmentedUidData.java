package com.example.segmentation.segmentation.models;

import com.google.gson.annotations.SerializedName;

/**
 * @author: Raihan Uddin Piash (raihan.uddin@braincraftapps.com)
 * @project: StickerMaker
 * @date: On 10/30/23 at 5:23 PM
 */
public class SegmentedUidData {
    @SerializedName("uid")
    private String imageUid;

    public String getImageUid() {
        return imageUid;
    }

    public void setImageUid(String imageUid) {
        this.imageUid = imageUid;
    }
}
