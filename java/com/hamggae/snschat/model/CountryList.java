package com.hamggae.snschat.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by seungjun on 2017-01-25.
 */

public class CountryList implements Serializable{
    String[] Thumb_KOR_name;
    ArrayList<String> ConList;
    String tmp;
    Map<String,List<String>> Thumb_Dic, Con_Dic;

    public CountryList(){

    }

    public CountryList(String[] Thumb_id, String[] Thumb_KOR_name, String[] Thumb_Eng_name, ArrayList<String> ConList, Map<String,List<String>> Dic_Con){
        this.Thumb_Dic=new HashMap<String,List<String>>();
        List<String> tmp2;
        for(int i=0;i<6;i++) {
            tmp2= new ArrayList<String>();
            tmp2.add(Thumb_id[i]);
            tmp2.add(Thumb_Eng_name[i]);
            this.Thumb_Dic.put(Thumb_KOR_name[i],tmp2);
        }
        this.Con_Dic=Dic_Con;
        this.Thumb_KOR_name=Thumb_KOR_name;
        this.ConList=ConList;


    }

    //public Set<String> getThumb_id(){return this.Thumb_id;}
    //public Set<String> getThumb_KOR_name(){return this.Thumb_KOR_name;}
    //public Set<String> getThumb_Eng_name(){return this.Thumb_Eng_name;}

    //public String[] getThumb_id_StringArray(){return this.Thumb_id;}
    public String[] getThumb_KOR_name_StringArray(){return this.Thumb_KOR_name;}
    //public String[] getThumb_Eng_name_StringArray(){return this.Thumb_Eng_name;}
    //public Set<String> getCountryList_ID(){return this.CountryList_ID;}
    //public Set<String> getCountryList_KOR(){return this.CountryList_KOR;}
    //public Set<String> getCountryList_Eng(){return this.CountryList_Eng;}


    public ArrayList<String> getCoutryList_KOR_StringList(){
        //ArrayList<String> Conlist_KOR = new ArrayList<>(this.CountryList_KOR);
        return this.ConList;
    }
    /*
    public ArrayList<String> getCoutryList_ENG_StringList(){
        ArrayList<String> Conlist_ENG = new ArrayList<>(this.CountryList_Eng);
        return Conlist_ENG;
    }
    public ArrayList<String> getCoutryList_ID_StringList(){
        ArrayList<String> Conlist_ID = new ArrayList<>(this.CountryList_ID);
        return Conlist_ID;
    }
    public void setCountryList_ID(Set<String> countryList_ID){
        this.CountryList_ID=countryList_ID;
    }
    public void setCountryList_KOR(Set<String> countryList_KOR){
        this.CountryList_KOR=countryList_KOR;
    }
    public void setCountryList_Eng(Set<String> countryList_Eng){
        this.CountryList_Eng=countryList_Eng;
    }

    public void setThumb_id(Set<String> thumb_id){
        this.Thumb_id=thumb_id;
    }

    public void setThumb_KOR_name(Set<String> thumb_KOR_name){
        this.Thumb_KOR_name=thumb_KOR_name;
    }
    public void setThumb_Eng_name(Set<String> thumb_Eng_name){
        this.Thumb_Eng_name=thumb_Eng_name;
    }
    */

    public Map<String,List<String>> getDic_con(){
        /*
        Map<String,List<String>> Dic_Con = new HashMap<String,List<String>>();
        ArrayList<String> Id_= new ArrayList<>();
        ArrayList<String> ENG_= new ArrayList<>();
        ArrayList<String> KOR_= new ArrayList<>();
        List<String> tmp;
        Id_=this.getCoutryList_ID_StringList();
        ENG_=this.getCoutryList_ENG_StringList();
        KOR_=this.getCoutryList_KOR_StringList();
        for(int i=0;i<Id_.size();i++){
            tmp=new ArrayList<String>();
            tmp.add(Id_.get(i));
            tmp.add(ENG_.get(i));
            Dic_Con.put(KOR_.get(i),tmp);
        }
        */
        return this.Con_Dic;
    }

    public Map<String,List<String>> getThumb_Dic(){
        return this.Thumb_Dic;
    }

    public void setThumb_KOR_name(String[] Thumb_KOR_name){
        this.Thumb_KOR_name=Thumb_KOR_name;
    }
    public void setCountryList_KOR(ArrayList<String> Con_Kor){
        this.ConList=Con_Kor;
    }
    public void setThumb_Dic(Map<String,List<String>> thumb_dic){
        this.Thumb_Dic=thumb_dic;
    }
    public void setCon_Dic(Map<String,List<String>> con_dic){
        this.Con_Dic=con_dic;
    }
}
