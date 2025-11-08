package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.Pojo.PostLIkePojo;

import java.io.Serializable;
import java.util.ArrayList;

public class LikesCount implements Serializable {

    @SerializedName("totallike")
    String likescount;
    ArrayList<PostLIkePojo> likes = new ArrayList<>();

    public LikesCount(String likescount, ArrayList<PostLIkePojo> likes) {
        this.likescount = likescount;
        this.likes = likes;
    }

    public ArrayList<PostLIkePojo> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<PostLIkePojo> likes) {
        this.likes = likes;
    }

    public String getLikescount() {
        return likescount;
    }

    public void setLikescount(String likescount) {
        this.likescount = likescount;
    }
}
