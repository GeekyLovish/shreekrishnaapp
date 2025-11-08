package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ByPartsData implements Serializable {

    @SerializedName("id")
    String id;
    @SerializedName("url")
    String url;
    @SerializedName("section")
    String section;

    public ByPartsData(String imageUploadId, String url, String picType) {
        id = imageUploadId;
        this.url = url;
        section = picType;
    }

    public ByPartsData() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }


}
