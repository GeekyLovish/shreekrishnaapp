package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.util.User;

public class ForgetPassResponse {

    @SerializedName("status")
    Boolean success;

    @SerializedName("message")
    String msg;

    public ForgetPassResponse(Boolean success, String msg) {
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
