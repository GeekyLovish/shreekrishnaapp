package com.raman.kumar.shrikrishan.adminPanel.addPosts;

public class AudioUploadInfo {

    private String songID;
    private String songTitle;
    private String displayName;
    private String songPath;
    private String songDuration;
    private String audioType;
    private String createdAt;
    private String position;

    public AudioUploadInfo(String imageUploadId, String url, String time, String duration, String songTitle, String audioType, String position) {
        songID = imageUploadId;
        this.songPath = url;
        this.createdAt = time;
        this.songDuration = duration;
        this.songTitle = songTitle;
        this.audioType = audioType;
        this.position = position;
    }

    public AudioUploadInfo() {
    }

    public String getSongID() {
        return songID;
    }

    public void setSongID(String songID) {
        this.songID = songID;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    public String getAudioType() {
        return audioType;
    }

    public void setAudioType(String audioType) {
        this.audioType = audioType;
    }
//    public String getPosition() {
//        return position;
//    }
//
//    public void setPosition(String position) {
//        this.position = position;
//    }

}
