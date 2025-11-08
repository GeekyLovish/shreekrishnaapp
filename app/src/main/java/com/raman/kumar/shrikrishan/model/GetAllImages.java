package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.Pojo.ImagesData;

import java.io.Serializable;

public class GetAllImages implements Serializable {

    @SerializedName("error")
    String error;

    @SerializedName("message")
    String msg;

    @SerializedName("ImagesData")
    ImagesData imagesData;

    public GetAllImages(String error, String msg, ImagesData imagesData) {
        this.error = error;
        this.msg = msg;
        this.imagesData = imagesData;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ImagesData getImagesData() {
        return imagesData;
    }

    public void setImagesData(ImagesData imagesData) {
        this.imagesData = imagesData;
    }
}
