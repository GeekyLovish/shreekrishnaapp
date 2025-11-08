package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.ByPartsData;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.ImageUploadInfo;

import java.io.Serializable;
import java.util.ArrayList;

public class GetImagesByPartsResponse implements Serializable {

    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    ArrayList<ByPartsData> byPartsData;

    public GetImagesByPartsResponse(Boolean success, String msg, ArrayList<ByPartsData> byPartsData) {
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

    public ArrayList<ByPartsData> getByPartsData() {
        return byPartsData;
    }

    public void setByPartsData(ArrayList<ByPartsData> byPartsData) {
        this.byPartsData = byPartsData;
    }
}
