package com.raman.kumar.shrikrishan.util;

import android.net.Uri;

public class PostLike {
    private String user_id;
    private String type;
    private String username;
    private Uri uri =null;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image;

    public PostLike() {

    }

    public PostLike(String user_id, String type, String username) {
        this.user_id = user_id;
        this.type = type;
        this.username = username;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getType() {

        if(type == null) return "";

        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
