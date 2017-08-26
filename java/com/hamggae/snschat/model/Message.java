package com.hamggae.snschat.model;

/**
 * Created by seungjun on 2016-12-23.
 */

import java.io.Serializable;

public class Message implements Serializable {
    String id, message, createdAt, messagetype;
    User user;
    private boolean isSelf, isRead;

    public Message() {
    }

    public Message(String id, String message, String createdAt, User user, boolean isSelf) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.user = user;
        this.isSelf = isSelf;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    public void setType(String type){ this.messagetype=type;}

    public String getType(){return this.messagetype;}

    public void setIsRead(Boolean isRead){this.isRead=isRead;}
    public Boolean getIsRead(){return this.isRead;}

}