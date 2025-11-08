package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.GeetaModel;
import com.raman.kumar.shrikrishan.util.User;

import java.io.Serializable;

public class AartiGeetaResponse  implements Serializable {

    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    GeetaModel aartiGeeta;

    public AartiGeetaResponse(Boolean success, String msg, GeetaModel aartiGeeta) {
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

    public GeetaModel getAartiGeeta() {
        return aartiGeeta;
    }

    public void setAartiGeeta(GeetaModel aartiGeeta) {
        this.aartiGeeta = aartiGeeta;
    }
}
