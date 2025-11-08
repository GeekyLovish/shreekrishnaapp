package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.Pojo.PostLIkePojo;
import com.raman.kumar.shrikrishan.util.Comment;

import java.io.Serializable;
import java.util.ArrayList;

public class CommentsResponse implements Serializable {

    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    ArrayList<Comment> comment = new ArrayList<>();

    public CommentsResponse(Boolean success, String msg, ArrayList<Comment> comment) {
        this.success = success;
        this.msg = msg;
        this.comment = comment;
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

    public ArrayList<Comment> getComment() {
        return comment;
    }

    public void setComment(ArrayList<Comment> comment) {
        this.comment = comment;
    }
}
