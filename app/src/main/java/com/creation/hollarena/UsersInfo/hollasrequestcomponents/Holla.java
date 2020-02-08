package com.creation.hollarena.UsersInfo.hollasrequestcomponents;

/**
 * Created by MMenem on 8/5/2017.
 */

public class Holla {
    private String name;
    private String image;
    private String userrecid;
    private String usersendid;


    public Holla() {

    }

    public Holla(String name, String image, String userrecid, String usersendid) {
        this.name = name;
        this.image = image;
        this.userrecid = userrecid;
        this.usersendid = usersendid;
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

    public String getUserrecid() {
        return userrecid;
    }

    public void setUserrecid(String userrecid) {
        this.userrecid = userrecid;
    }

    public String getUsersendid() {
        return usersendid;
    }

    public void setUsersendid(String usersendid) {
        this.usersendid = usersendid;
    }
}
