package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.GeetaModel;

import java.io.Serializable;
import java.util.ArrayList;

public class GetAartGitaResponse implements Serializable {

    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    ArrayList<GeetaModel> aartiGeeta;

    public GetAartGitaResponse(Boolean success, String msg, ArrayList<GeetaModel> aartiGeeta) {
        this.success = success;
        this.msg = msg;
        this.aartiGeeta = aartiGeeta;
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

    public ArrayList<GeetaModel> getAartiGeeta() {
        return aartiGeeta;
    }

    public void setAartiGeeta(ArrayList<GeetaModel> aartiGeeta) {
        this.aartiGeeta = aartiGeeta;
    }
}
