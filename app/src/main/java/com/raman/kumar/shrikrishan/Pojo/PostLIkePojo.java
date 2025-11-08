package com.raman.kumar.shrikrishan.Pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PostLIkePojo implements Serializable {

    @SerializedName("id")
    String id;
    @SerializedName("user_id")
    String user_id;
    @SerializedName("type")
    String type;
    @SerializedName("username")
    String username;
    @SerializedName("post_id")
    String image_id;
    @SerializedName("userimage")
    String user_image;

    public PostLIkePojo() {
    }

    public PostLIkePojo(String id, String getuId, String like, String getuName, String image_id, String user_image) {
        this.id = id;
        this.user_id = getuId;
        this.type = like;
        this.username = getuName;
        this.image_id = image_id;
        this.user_image = user_image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image) {
        this.image_id = image;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }
}
