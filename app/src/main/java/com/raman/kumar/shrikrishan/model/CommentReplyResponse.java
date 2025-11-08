package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.CommentFiles.UserReply;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.CodeData;

import java.io.Serializable;

public class CommentReplyResponse implements Serializable {

    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    UserReply userReply;

    public CommentReplyResponse(Boolean success, String msg, UserReply userReply) {
        this.success = success;
        this.msg = msg;
        this.userReply = userReply;
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

    public UserReply getUserReply() {
        return userReply;
    }

    public void setUserReply(UserReply userReply) {
        this.userReply = userReply;
    }
}
