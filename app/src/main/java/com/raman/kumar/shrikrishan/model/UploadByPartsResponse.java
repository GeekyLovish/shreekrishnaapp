package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.ByPartsData;
import com.raman.kumar.shrikrishan.util.User;

import java.io.Serializable;

public class UploadByPartsResponse implements Serializable {
    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    ByPartsData byPartsData;

    public UploadByPartsResponse(Boolean success, String msg, ByPartsData byPartsData) {
        this.success = success;
        this.msg = msg;
        this.byPartsData = byPartsData;
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

    public ByPartsData getByPartsData() {
        return byPartsData;
    }

    public void setByPartsData(ByPartsData byPartsData) {
        this.byPartsData = byPartsData;
    }
}
