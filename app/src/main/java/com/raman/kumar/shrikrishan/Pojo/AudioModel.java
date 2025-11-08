package com.raman.kumar.shrikrishan.Pojo;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Dell- on 3/24/2018.
 */

public class AudioModel implements Serializable,Comparable<AudioModel>{

    String url;

    public String getUrl() {
        return url;
    }

    Integer id;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String title;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AudioModel [id=" + id + "]";
    }

    @Override
    public int compareTo(@NonNull AudioModel audioModel) {
        return getId().compareTo(audioModel.getId());
    }
}
