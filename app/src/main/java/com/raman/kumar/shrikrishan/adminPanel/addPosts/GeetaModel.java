package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GeetaModel implements Serializable {

    @SerializedName("id")
    String id;

    @SerializedName("title")
    String title;

    @SerializedName("content")
    String content;

    public GeetaModel(String id, String title, String html) {
        this.id = id;
        this.title = title;
        content = html;
    }

    public GeetaModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
