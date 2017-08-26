package com.hamggae.snschat.model;

/**
 * Created by seungjun on 2016-12-23.
 */

import java.io.Serializable;

public class User implements Serializable {
    String id, name, LinkUri, profile_photo_path;
    boolean isOpen;

    public User() {
    }

    public User(String id, String name, String LinkUri, String profile_photo_path) {
        this.id = id;
        this.name = name;
        this.LinkUri = LinkUri;
        this.profile_photo_path = profile_photo_path;
        this.isOpen=false;
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

    public String getLinkUri() {
        return LinkUri;
    }

    public void setLinkUri(String LinkUri) {
        this.LinkUri = LinkUri;
    }

    public String getProfile_path() {
        return profile_photo_path;
    }

    public void setProfile_path(String profile_photo_path) {
        this.profile_photo_path = profile_photo_path;
    }

    public void setisOpen(boolean isOpen){
        this.isOpen=isOpen;
    }
    public boolean getisOpen(){
        return this.isOpen;
    }
}