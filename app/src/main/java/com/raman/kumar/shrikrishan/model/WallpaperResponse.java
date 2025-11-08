package com.raman.kumar.shrikrishan.model;

import android.media.Image;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.Pojo.ImagesData;
import com.raman.kumar.shrikrishan.Pojo.UploadInfo;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.VideoModel;

import java.io.Serializable;
import java.util.ArrayList;

public class WallpaperResponse  implements Serializable {

    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    ArrayList<ImagesData> imagesData;

    public WallpaperResponse(Boolean success, String msg, ArrayList<ImagesData> imagesData) {
        this.success = success;
        this.msg = msg;
        this.imagesData = imagesData;
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

    public ArrayList<ImagesData> getImagesData() {
        return imagesData;
    }

    public void setImagesData(ArrayList<ImagesData> imagesData) {
        this.imagesData = imagesData;
    }
}
