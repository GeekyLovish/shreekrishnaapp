package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.Pojo.PostLIkePojo;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.GeetaModel;

import java.io.Serializable;

public class LikePostResponse  implements Serializable {

    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    LikeDetail postlike;

    public LikePostResponse(Boolean success, String msg, LikeDetail postlike) {
        this.success = success;
        this.msg = msg;
        this.postlike = postlike;
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

    public LikeDetail getPostlike() {
        return postlike;
    }

    public void setPostlike(LikeDetail postlike) {
        this.postlike = postlike;
    }
}
