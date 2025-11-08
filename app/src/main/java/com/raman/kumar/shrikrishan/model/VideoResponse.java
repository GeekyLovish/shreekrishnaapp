package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.GeetaModel;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.VideoModel;

import java.io.Serializable;

public class VideoResponse implements Serializable {

    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    VideoModel videoData;

    public VideoResponse(Boolean success, String msg, VideoModel videoData) {
        this.success = success;
        this.msg = msg;
        this.videoData = videoData;
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

    public VideoModel getVideoData() {
        return videoData;
    }

    public void setVideoData(VideoModel videoData) {
        this.videoData = videoData;
    }
}
