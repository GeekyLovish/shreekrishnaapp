package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VideoModel implements Serializable {

    @SerializedName("id")
    String videoUploadId;

    @SerializedName("title")
    String title;

    @SerializedName("content")
    String content;

    @SerializedName("url")
    String videoUrl;

    public VideoModel(String videoUploadId, String title, String content, String url) {
        this.videoUploadId = videoUploadId;
        this.title = title;
        this.content = content;
        videoUrl = url;
    }

    public VideoModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoUploadId() {
        return videoUploadId;
    }

    public void setVideoUploadId(String videoUploadId) {
        this.videoUploadId = videoUploadId;
    }
}
