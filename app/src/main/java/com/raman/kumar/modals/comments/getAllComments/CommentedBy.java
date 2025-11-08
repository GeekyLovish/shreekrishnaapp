
package com.raman.kumar.modals.comments.getAllComments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentedBy {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("is_blocked")
    @Expose
    private Integer isBlocked;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(Integer isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

}
