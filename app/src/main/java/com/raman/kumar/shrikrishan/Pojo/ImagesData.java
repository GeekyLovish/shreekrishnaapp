package com.raman.kumar.shrikrishan.Pojo;

/**
 * Created by Dell- on 3/10/2018.
 */

public class ImagesData {

    String id;
    String name;
    String image;

    public ImagesData(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
