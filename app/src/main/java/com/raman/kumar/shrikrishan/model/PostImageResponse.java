package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.ImageUploadInfo;

import java.io.Serializable;

public class PostImageResponse implements Serializable {

    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    ImageUploadInfo imageUploadInfo;

    public PostImageResponse(Boolean success, String msg, ImageUploadInfo imageUploadInfo) {
        this.success = success;
        this.msg = msg;
        this.imageUploadInfo = imageUploadInfo;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ImageUploadInfo getImageUploadInfo() {
        return imageUploadInfo;
    }

    public void setImageUploadInfo(ImageUploadInfo imageUploadInfo) {
        this.imageUploadInfo = imageUploadInfo;
    }
}
