package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ImageUploadInfo implements Serializable {
    @SerializedName("id")
    public String id;
    @SerializedName("title")
    public String title;
    @SerializedName("url")
    public String url;
    @SerializedName("content")
    public String content;
    @SerializedName("gallery")  
    public String gallery;
    @SerializedName("amrit")
    private String amrit;
    @SerializedName("byParts")
    private String byParts;
    @SerializedName("createdAt")
    private String createdAt;
   @SerializedName("updatedAt")
    private String updatedAt;
    @SerializedName("linkType")
    private String linkType;
    @SerializedName("adTitle")
    private String adTitle;
    @SerializedName("update")
    private int update;

    public ImageUploadInfo(String id, String title, String url, String content, String gallery, String amrit, String byParts, String createdAt,String updatedAt, String linkType, String adTitle, int update) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.content = content;
        this.gallery = gallery;
        this.amrit = amrit;
        this.byParts = byParts;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.linkType = linkType;
        this.adTitle = adTitle;
        this.update = update;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGallery() {
        return gallery;
    }

    public void setGallery(String gallery) {
        this.gallery = gallery;
    }

    public String getAmrit() {
        return amrit;
    }

    public void setAmrit(String amrit) {
        this.amrit = amrit;
    }

    public String getByParts() {
        return byParts;
    }

    public void setByParts(String byParts) {
        this.byParts = byParts;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getAdTitle() {
        return adTitle;
    }

    public void setAdTitle(String adTitle) {
        this.adTitle = adTitle;
    }

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }
}

