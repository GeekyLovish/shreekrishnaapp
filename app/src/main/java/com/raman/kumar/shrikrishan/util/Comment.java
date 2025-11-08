package com.raman.kumar.shrikrishan.util;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;
import com.raman.kumar.shrikrishan.CommentFiles.UserReply;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Comment implements Serializable {

    @SerializedName("comment")
    private String comment;
    @SerializedName("createdAt")
    private String date_added;
    @SerializedName("username")
    private String user_name;
    @SerializedName("userId")
    private String userId;
    @SerializedName("image")
    private String image;
    @SerializedName("id")
    private String id;
    private Uri uri;
    @SerializedName("post_id")
    private String post_id;
    @SerializedName("token")
    private String user_token;
    @SerializedName("user_image")
    private String user_image;
    @SerializedName("comment_likes")
    private List<String> comment_likes = new ArrayList<>();
    @SerializedName("user_reply")
    private List<UserReply> userReplies = new ArrayList<>();

    public List<UserReply> getUserReplies() {
        return userReplies;
    }

    public void setUserReplies(List<UserReply> userReplies) {
        this.userReplies = userReplies;
    }

    public Comment() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Comment(String comment, String date_added, String userName, String userId, String post_id, List<String> commentLikes, List<UserReply> userReplies, String user_token, String image, String id, String userImage) {
        this.comment = comment;
        this.date_added = date_added;
        this.user_name = userName;
        this.userId = userId;
        this.post_id = post_id;
        this.comment_likes = commentLikes;
        this.userReplies = userReplies;
        this.user_token = user_token;
        this.image = image;
        this.id = id;
        this.user_image = userImage;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


    public String getUser_token() {
        return user_token;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }

    public List<String> getComment_likes() {
        return comment_likes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setComment_likes(List<String> comment_likes) {
        this.comment_likes = comment_likes;
    }

//    public Uri getUri() {
//        return uri;
//    }
//
//    public void setUri(Uri uri) {
//        this.uri = uri;
//    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }
}
