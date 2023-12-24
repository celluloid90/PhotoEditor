package com.example.segmentation.segmentation.models;

import com.google.gson.annotations.SerializedName;

/**
 * @author: Raihan Uddin Piash (raihan.uddin@braincraftapps.com)
 * @project: Segmentation
 * @date: On 12/24/23 at 4:35 PM
 */
public class Base64_ResponseModel {
    @SerializedName("status")
    private String status;

    @SerializedName("image_name")
    private String imageName;

    @SerializedName("processing_time")
    private String processingTime;

    @SerializedName("image_value")
    private String imageValue;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(String processingTime) {
        this.processingTime = processingTime;
    }

    public String getImageValue() {
        return imageValue;
    }

    public void setImageValue(String imageValue) {
        this.imageValue = imageValue;
    }
}
