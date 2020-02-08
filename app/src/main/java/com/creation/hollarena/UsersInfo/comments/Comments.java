package com.creation.hollarena.UsersInfo.comments;

/**
 * Created by MMenem on 7/30/2017.
 */

public class Comments {

    private String userid;
    private String username;
    private String userimage;
    private String comment;
    private String timeCreated;

    public Comments() {
    }

    public Comments(String username, String userimage, String comment, String timeCreated, String userid) {
        this.username = username;
        this.userimage = userimage;
        this.comment = comment;
        this.timeCreated = timeCreated;
        this.userid = userid;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
