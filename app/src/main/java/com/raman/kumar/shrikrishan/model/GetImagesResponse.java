package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.ImageUploadInfo;

import java.io.Serializable;
import java.util.ArrayList;

public class GetImagesResponse implements Serializable {

    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    ArrayList<ImageUploadInfo> imageUploadInfo;

    public GetImagesResponse(Boolean success, String msg, ArrayList<ImageUploadInfo> imageUploadInfo) {
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

    public ArrayList<ImageUploadInfo> getImageUploadInfo() {
        return imageUploadInfo;
    }

    public void setImageUploadInfo(ArrayList<ImageUploadInfo> imageUploadInfo) {
        this.imageUploadInfo = imageUploadInfo;
    }
}
