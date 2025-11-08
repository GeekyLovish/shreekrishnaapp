
package com.raman.kumar.modals.authentication.loginMobile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class Data {

    @SerializedName("id")
    @Expose
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
