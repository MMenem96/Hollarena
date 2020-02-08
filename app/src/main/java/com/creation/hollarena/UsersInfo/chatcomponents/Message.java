package com.creation.hollarena.UsersInfo.chatcomponents;

/**
 * Created by HP on 31/07/17.
 */

public class Message {
    private String message,user_name;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getUser_name() {
        return user_name;
    }
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
    public Message() {
    }
    public Message(String message, String user_name) {
        this.message = message;
        this.user_name = user_name;
    }
}
