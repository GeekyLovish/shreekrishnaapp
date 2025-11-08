package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.util.Comment;

import java.io.Serializable;

public class PostCommentResponse implements Serializable {

    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    Comment postComment;

    public PostCommentResponse(Boolean success, String msg, Comment postComment) {
        this.success = success;
        this.msg = msg;
        this.postComment = postComment;
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

    public Comment getPostComment() {
        return postComment;
    }

    public void setPostComment(Comment postComment) {
        this.postComment = postComment;
    }
}
