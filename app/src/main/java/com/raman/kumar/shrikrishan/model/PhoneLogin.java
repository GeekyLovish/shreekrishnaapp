package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PhoneLogin implements Serializable {

    @SerializedName("user_id")
    String userId;

    @SerializedName("otp")
    String otp;

    public PhoneLogin(String userId, String otp) {
        this.userId = userId;
        this.otp = otp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
