package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.Pojo.PostLIkePojo;

import java.io.Serializable;
import java.util.ArrayList;

public class LikesResponse implements Serializable {
    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    LikesDetails likesDetails;

    public LikesResponse(Boolean success, String msg, LikesDetails likesDetails) {
        this.success = success;
        this.msg = msg;
        this.likesDetails = likesDetails;
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

    public LikesDetails getLikesDetails() {
        return likesDetails;
    }

    public void setLikesDetails(LikesDetails likesDetails) {
        this.likesDetails = likesDetails;
    }
}
