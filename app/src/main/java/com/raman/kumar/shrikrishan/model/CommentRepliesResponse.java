package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.CommentFiles.UserReply;

import java.io.Serializable;
import java.util.ArrayList;

public class CommentRepliesResponse implements Serializable {

    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    ArrayList<UserReply> userReply = new ArrayList<>();

    public CommentRepliesResponse(Boolean success, String msg, ArrayList<UserReply> userReply) {
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

    public ArrayList<UserReply> getUserReply() {
        return userReply;
    }

    public void setUserReply(ArrayList<UserReply> userReply) {
        this.userReply = userReply;
    }
}
