package com.raman.kumar.shrikrishan.CommentFiles;

import java.util.ArrayList;
import java.util.List;

public class PostLikePojo {

    public PostLikePojo() {
    }

    public PostLikePojo(String post_id, String userId) {
        this.post_id = post_id;
        this.userId = userId;
    }

    public String getPostId() {
        return post_id;
    }

    public void setPostId(String like) {
        this.post_id = like;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    String post_id, userId;

    public List<String> getPostLikes() {
        return postLikes;
    }

    public void setPostLikes(List<String> postLikes) {
        this.postLikes = postLikes;
    }

    List<String> postLikes = new ArrayList<>();

}
