package com.hamggae.snschat.model;

/**
 * Created by seungjun on 2017-01-11.
 */

import java.io.Serializable;

public class Country implements Serializable {
    String id, Eng_name, Kor_name, Countryhits;

    public Country() {
    }

    public Country(String id, String Eng_name, String Kor_name, String Countryhits) {
        this.id = id;
        this.Eng_name = Eng_name;
        this.Kor_name = Kor_name;
        this.Countryhits = Countryhits;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEngName() {
        return Eng_name;
    }

    public void setEngName(String name) {
        this.Eng_name = name;
    }

    public String getKorName() {
        return Kor_name;
    }

    public void setKorName(String name) {
        this.Kor_name = name;
    }

    public String getCountryhits() {
        return Countryhits;
    }

    public void setCountryhits(String Countryhits) {
        this.Countryhits = Countryhits;
    }
}



