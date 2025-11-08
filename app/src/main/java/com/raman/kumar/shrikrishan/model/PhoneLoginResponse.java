package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PhoneLoginResponse implements Serializable {

    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    PhoneLogin phoneLogin;

    public PhoneLoginResponse(Boolean success, String msg, PhoneLogin phoneLogin) {
        this.success = success;
        this.msg = msg;
        this.phoneLogin = phoneLogin;
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

    public PhoneLogin getPhoneLogin() {
        return phoneLogin;
    }

    public void setPhoneLogin(PhoneLogin phoneLogin) {
        this.phoneLogin = phoneLogin;
    }
}
