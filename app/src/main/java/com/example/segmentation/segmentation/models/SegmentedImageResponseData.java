package com.example.segmentation.segmentation.models;

import com.google.gson.annotations.SerializedName;

/**
 * @author: Raihan Uddin Piash (raihan.uddin@braincraftapps.com)
 * @project: StickerMaker
 * @date: On 10/30/23 at 5:23 PM
 */
public class SegmentedImageResponseData {
    @SerializedName("eta")
    private String remainTimeInSec;
    @SerializedName("result")
    private String result;
    @SerializedName("status")
    private String status;

    public String getRemainTimeInSec() {
        return remainTimeInSec;
    }

    public void setRemainTimeInSec(String remainTimeInSec) {
        this.remainTimeInSec = remainTimeInSec;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
