package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommentImage implements Serializable {

    @SerializedName("image")
    String image;

    public CommentImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
