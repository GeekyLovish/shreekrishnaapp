package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.Pojo.PostLIkePojo;

import java.io.Serializable;
import java.util.ArrayList;

public class LikesDetails implements Serializable {

    @SerializedName("totallike")
    int totallike;
    @SerializedName("likedata")
    ArrayList<PostLIkePojo> likedata = new ArrayList<>();

    public LikesDetails(int totallike, ArrayList<PostLIkePojo> likedata) {
        this.totallike = totallike;
        this.likedata = likedata;
    }

    public int getTotallike() {
        return totallike;
    }

    public void setTotallike(int totallike) {
        this.totallike = totallike;
    }

    public ArrayList<PostLIkePojo> getLikedata() {
        return likedata;
    }

    public void setLikedata(ArrayList<PostLIkePojo> likedata) {
        this.likedata = likedata;
    }
}
