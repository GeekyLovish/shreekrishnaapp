package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.GeetaModel;

import java.io.Serializable;

public class CommentImageResponse implements Serializable {
    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    String commentImage;

    public CommentImageResponse(Boolean success, String msg, String commentImage) {
        this.success = success;
        this.msg = msg;
        this.commentImage = commentImage;
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

    public String getCommentImage() {
        return commentImage;
    }

    public void setCommentImage(String commentImage) {
        this.commentImage = commentImage;
    }
}
