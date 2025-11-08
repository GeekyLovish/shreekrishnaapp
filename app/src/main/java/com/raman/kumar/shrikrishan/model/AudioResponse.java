package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.AudioUploadInfo;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.ImageUploadInfo;

import java.io.Serializable;

public class AudioResponse implements Serializable {
    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    AudioUploadInfo audioUploadInfo;

    public AudioResponse(Boolean success, String msg, AudioUploadInfo audioUploadInfo) {
        this.success = success;
        this.msg = msg;
        this.audioUploadInfo = audioUploadInfo;
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

    public AudioUploadInfo getAudioUploadInfo() {
        return audioUploadInfo;
    }

    public void setAudioUploadInfo(AudioUploadInfo audioUploadInfo) {
        this.audioUploadInfo = audioUploadInfo;
    }
}
