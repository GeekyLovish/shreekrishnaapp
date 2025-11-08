package com.raman.kumar.shrikrishan.model;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.Pojo.PostLIkePojo;

import java.io.Serializable;

public class LikeDetail implements Serializable {

    @SerializedName("totallike")
    String totallike;
    @SerializedName("likedata")
    PostLIkePojo likedata;

    public LikeDetail(String totallike, PostLIkePojo likedata) {
        this.totallike = totallike;
        this.likedata = likedata;
    }

    public String getTotallike() {
        return totallike;
    }

    public void setTotallike(String totallike) {
        this.totallike = totallike;
    }

    public PostLIkePojo getLikedata() {
        return likedata;
    }

    public void setLikedata(PostLIkePojo likedata) {
        this.likedata = likedata;
    }
}
