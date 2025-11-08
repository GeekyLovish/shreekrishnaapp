package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommonResponse implements Serializable
{
    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    public CommonResponse(Boolean success, String msg) {
        this.success = success;
        this.msg = msg;
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
}
