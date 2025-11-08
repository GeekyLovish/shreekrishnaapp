
package com.raman.kumar.modals.gallary.getGallary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LikedByMeType {

    @SerializedName("type")
    @Expose
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
