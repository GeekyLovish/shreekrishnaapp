package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.AudioUploadInfo;

import java.io.Serializable;
import java.util.ArrayList;

public class GetAllAudioResponse implements Serializable {

    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    ArrayList<AudioUploadInfo> audioInfo;

    public GetAllAudioResponse(Boolean success, String msg, ArrayList<AudioUploadInfo> audioInfo) {
        this.success = success;
        this.msg = msg;
        this.audioInfo = audioInfo;
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

    public ArrayList<AudioUploadInfo> getAudioInfo() {
        return audioInfo;
    }

    public void setAudioInfo(ArrayList<AudioUploadInfo> audioInfo) {
        this.audioInfo = audioInfo;
    }
}
