package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.CodeData;

import java.io.Serializable;

public class CodeResponse implements Serializable {

    @SerializedName("success")
    Boolean success;

    @SerializedName("message")
    String msg;

    @SerializedName("data")
    CodeData codeData;

    public CodeResponse(Boolean success, String msg, CodeData codeData) {
        this.success = success;
        this.msg = msg;
        this.codeData = codeData;
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

    public CodeData getCodeData() {
        return codeData;
    }

    public void setCodeData(CodeData codeData) {
        this.codeData = codeData;
    }
}
