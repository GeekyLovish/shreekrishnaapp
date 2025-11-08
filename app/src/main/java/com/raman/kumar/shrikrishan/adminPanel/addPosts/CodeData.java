package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CodeData implements Serializable {

    @SerializedName("Code")
    int code;

    public CodeData() {
    }

    public CodeData(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
