package com.raman.kumar.shrikrishan.CommentFiles;

import java.util.ArrayList;
import java.util.List;

public class CommentListPojo {

    String userName, userComment, userId;
    private List<String> usersLike;
    private List<UserReply> usersReply = new ArrayList<>();

    CommentListPojo(String userName, String userComment, String userId, List<String> usersLike, List<UserReply> usersReply) {
        this.userName = userName;
        this.userComment = userComment;
        this.userId = userId;
        this.usersLike = usersLike;
        this.usersReply = usersReply;
    }


    List<String> getUserLike() {
        return usersLike;
    }

    public void setUserLike(ArrayList<String> usersLike) {
        this.usersLike = usersLike;
    }

    public List<UserReply> getUsersReply() {
        return usersReply;
    }

    public void setUsersReply(ArrayList<UserReply> usersReply) {
        this.usersReply = usersReply;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
