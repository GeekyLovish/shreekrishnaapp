
package com.raman.kumar.modals.comments.getAllComments;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("post_id")
    @Expose
    private Integer postId;
    @SerializedName("comment_id")
    @Expose
    private Object commentId;
    @SerializedName("is_parent")
    @Expose
    private Integer isParent;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("comment_likes_count")
    @Expose
    private Integer commentLikesCount;
    @SerializedName("liked_by_me")
    @Expose
    private Boolean likedByMe;
    @SerializedName("commented_by")
    @Expose
    private CommentedBy commentedBy;
    @SerializedName("replies")
    @Expose
    private List<Reply> replies;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Object getCommentId() {
        return commentId;
    }

    public void setCommentId(Object commentId) {
        this.commentId = commentId;
    }

    public Integer getIsParent() {
        return isParent;
    }

    public void setIsParent(Integer isParent) {
        this.isParent = isParent;
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

    public Integer getCommentLikesCount() {
        return commentLikesCount;
    }

    public void setCommentLikesCount(Integer commentLikesCount) {
        this.commentLikesCount = commentLikesCount;
    }

    public Boolean getLikedByMe() {
        return likedByMe;
    }

    public void setLikedByMe(Boolean likedByMe) {
        this.likedByMe = likedByMe;
    }

    public CommentedBy getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(CommentedBy commentedBy) {
        this.commentedBy = commentedBy;
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

}
