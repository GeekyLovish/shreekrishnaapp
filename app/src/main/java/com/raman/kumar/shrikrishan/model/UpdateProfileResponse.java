package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.ByPartsData;

import java.io.Serializable;

public class UpdateProfileResponse implements Serializable {
    @SerializedName("status")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    UpdateProfile updateProfile;

    public UpdateProfileResponse(Boolean success, String msg, UpdateProfile updateProfile) {
        this.success = success;
        this.msg = msg;
        this.updateProfile = updateProfile;
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

    public UpdateProfile getUpdateProfile() {
        return updateProfile;
    }

    public void setUpdateProfile(UpdateProfile updateProfile) {
        this.updateProfile = updateProfile;
    }
}
