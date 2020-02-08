package com.creation.hollarena.UsersInfo.navigationdrawer.hollarenaalterego;

/**
 * Created by MMenem on 7/20/2017.
 */

public class Topics {

    private String title;
    private String desc;
    private String image;
    private String username;
    private String userimage;
    private String timeCreated;

    public Topics() {

    }

    public Topics(String username, String title, String desc, String image, String userimage, String timeCreated) {
        this.username = username;
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.userimage = userimage;
        this.timeCreated = timeCreated;


    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }
}
