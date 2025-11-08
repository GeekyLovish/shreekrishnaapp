package com.raman.kumar.shrikrishan.CommentFiles;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserReply  implements Serializable{
    @SerializedName("username")
    String username;
    @SerializedName("userComment")
    String userComment;
    @SerializedName("userId")
    String userId;
    @SerializedName("userimage")
    String userImage;
    @SerializedName("id")
    String id;
//    Uri uri = null;
    @SerializedName("token")
    String user_token;
    @SerializedName("createdAt")
    String createdAt;
    @SerializedName("image")
    String image = "";

//    protected UserReply(Parcel in) {
//        username = in.readString();
//        userComment = in.readString();
//        userId = in.readString();
//        userImage = in.readString();
//        id = in.readString();
////        uri = in.readParcelable(Uri.class.getClassLoader());
//        user_token = in.readString();
//        createdAt = in.readString();
//        image = in.readString();
//    }

//    public static final Creator<UserReply> CREATOR = new Creator<UserReply>() {
//        @Override
//        public UserReply createFromParcel(Parcel in) {
//            return new UserReply(in);
//        }
//
//        @Override
//        public UserReply[] newArray(int size) {
//            return new UserReply[size];
//        }
//    };

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }



    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public UserReply(String userComment, String userId, String userName, String time, String device_token, String id, String userImage) {
        this.userComment = userComment;
        this.userId = userId;
        this.username = userName;
        this.createdAt = time;
        this.user_token = device_token;
        this.id = id;
        this.userImage = userImage;
    }

    public UserReply(String userComment, String userId, String userName, String time, String device_token, String userImage) {
        this.userComment = userComment;
        this.userId = userId;
        this.username = userName;
        this.createdAt = time;
        this.user_token = device_token;
        this.userImage = userImage;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserReply() {
    }

    public String getTime() {
        return createdAt;
    }

    public void setTime(String time) {
        this.createdAt = time;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getUserComment() {
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

    public String getUser_token() {
        return user_token;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }

//    public Uri getUri() {
//        return uri;
//    }
//
//    public void setUri(Uri uri) {
//        this.uri = uri;
//    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeString(username);
//        parcel.writeString(userComment);
//        parcel.writeString(userId);
//        parcel.writeString(userImage);
//        parcel.writeString(id);
////        parcel.writeParcelable(uri, i);
//        parcel.writeString(user_token);
//        parcel.writeString(createdAt);
//        parcel.writeString(image);
//    }
}
