package com.hamggae.snschat.model;

/**
 * Created by seungjun on 2016-12-23.
 */

import java.io.Serializable;

public class ChatRoom implements Serializable {
    String id, name, lastMessage, timestamp, info, KOR_name, MemberCount;
    int unreadCount, country_id;
    Boolean isRoom;

    public ChatRoom() {
    }

    public ChatRoom(String id, String name, String lastMessage, String timestamp, int unreadCount, int country_id, String info) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
        this.country_id=country_id;
        this.info =info;
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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setCountry_id(int country_id){ this.country_id=country_id;}
    public void setInfo(String info){ this.info=info;}
    public int getCountry_id() {return this.country_id;}
    public String getInfo() {return this.info; }
    public void setKOR_name(String KOR_name){ this.KOR_name = KOR_name; }
    public String getKOR_name(){return this.KOR_name;}
    public void setMemberCount(String MemberCount){this.MemberCount=MemberCount;}
    public String getMemberCount(){return this.MemberCount;}

    public void setIsRoom(Boolean isRoom){this.isRoom=isRoom;}
    public Boolean getIsRoom(){return this.isRoom;}
}