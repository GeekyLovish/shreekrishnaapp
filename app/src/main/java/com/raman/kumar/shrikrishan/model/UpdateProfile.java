package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UpdateProfile implements Serializable {
    @SerializedName("id")
    String id;
  @SerializedName("name")
    String name;
  @SerializedName("image")
    String image;
  @SerializedName("block")
    String block;
  @SerializedName("admin_user_id")
    String admin_user_id;
  @SerializedName("createdAt")
    String createdAt;
  @SerializedName("updatedAt")
    String updatedAt;

    public UpdateProfile(String id, String name, String image, String block, String admin_user_id, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.block = block;
        this.admin_user_id = admin_user_id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getAdmin_user_id() {
        return admin_user_id;
    }

    public void setAdmin_user_id(String admin_user_id) {
        this.admin_user_id = admin_user_id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
