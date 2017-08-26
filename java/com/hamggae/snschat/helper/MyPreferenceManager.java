package com.hamggae.snschat.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.hamggae.snschat.app.EndPoints;
import com.hamggae.snschat.app.MyApplication;
import com.hamggae.snschat.model.CountryList;
import com.hamggae.snschat.model.Message;
import com.hamggae.snschat.model.User;
import com.hamggae.snschat.app.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyPreferenceManager {

    private String TAG = MyPreferenceManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = Config.SHARED_PREF;

    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_LinkUri = "user_LinkUri";
    private static final String KEY_USER_PROFILE = "profile_photo_path";
    private static final String KEY_USER_ISOPEN = "user_isOpen";
    private static final String KEY_NOTIFICATIONS = "notifications";

    private static final String KEY_THUMB_ID="Thumb_ID_";
    private static final String KEY_THUMB_KOR_NAME="Thumb_KOR_name_";
    private static final String KEY_THUMB_ENG_NAME="Thumb_Eng_name_";
    private static final String KEY_CON_ID="CON_ID_";
    private static final String KEY_CON_KOR="CON_KOR_";
    private static final String KEY_CON_ENG="CON_ENG_";



    // Constructor
    public MyPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void storeUser(User user) {
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_LinkUri, user.getLinkUri());
        editor.putString(KEY_USER_PROFILE, user.getProfile_path());
        editor.putBoolean(KEY_USER_ISOPEN, user.getisOpen());
        editor.commit();

        Log.e(TAG, "User is stored in shared preferences. " + user.getName() + ", " + user.getLinkUri());
    }

    public User getUser() {
        if (pref.getString(KEY_USER_ID, null) != null) {
            String id, name, LinkUri,profile_photo_path;
            id = pref.getString(KEY_USER_ID, null);
            name = pref.getString(KEY_USER_NAME, null);
            LinkUri = pref.getString(KEY_USER_LinkUri, null);
            profile_photo_path = pref.getString(KEY_USER_PROFILE,null);

            User user = new User(id, name, LinkUri,profile_photo_path);
            user.setisOpen(pref.getBoolean(KEY_USER_ISOPEN,false));
            return user;
        }
        return null;
    }

    public void addNotification(String notification) {

        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

    public void clearUser(){
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_LinkUri);
        editor.remove(KEY_USER_PROFILE);
        editor.commit();
    }

    public void storeCountryInfo(CountryList countryList){
        //editor.putStringSet(KEY_THUMB_ID,countryList.getThumb_id());
        String[] tmp_string= countryList.getThumb_KOR_name_StringArray();
        Map<String,List<String>> Thumb_dic = new HashMap<String,List<String>>();
        Thumb_dic=countryList.getThumb_Dic();
        Set<String> Thumb_kor=new HashSet<String>(Arrays.asList(tmp_string));
        editor.putStringSet(KEY_THUMB_KOR_NAME,Thumb_kor);
        for(int i=0;i<6;i++){
            editor.putString(KEY_THUMB_ENG_NAME+tmp_string[i],Thumb_dic.get(tmp_string[i]).get(1));
            editor.putString(KEY_THUMB_ID+tmp_string[i],Thumb_dic.get(tmp_string[i]).get(0));
        }
        //editor.putStringSet(KEY_THUMB_ENG_NAME,countryList.getThumb_Eng_name());
        //editor.putStringSet(KEY_CON_ID,countryList.getCountryList_ID());
        ArrayList<String> tmp_string2 = new ArrayList<>();
        tmp_string2=countryList.getCoutryList_KOR_StringList();
        Set<String> Con_Kor=new HashSet<String>(tmp_string2);
        editor.putStringSet(KEY_CON_KOR,Con_Kor);
        //editor.putStringSet(KEY_CON_ENG,countryList.getCountryList_Eng());
        Map<String,List<String>> Dic_Con = new HashMap<String,List<String>>();
        Dic_Con=countryList.getDic_con();

        for (int i=0; i<Con_Kor.size();i++){
            editor.putString(KEY_CON_ENG+tmp_string2.get(i),Dic_Con.get(tmp_string2.get(i)).get(1));
            editor.putString(KEY_CON_ID+tmp_string2.get(i),Dic_Con.get(tmp_string2.get(i)).get(0));
        }

        editor.commit();
    }

    public void clearCountryInfo(){
        ArrayList<String> Con_KOR = new ArrayList<>(pref.getStringSet(KEY_CON_KOR,null));
        String[] Thumb_KOR = pref.getStringSet(KEY_THUMB_KOR_NAME,null).toArray(new String[6]);

        for(int i=0; i<Con_KOR.size();i++){
            editor.remove(KEY_CON_ID+Con_KOR.get(i));
            editor.remove(KEY_CON_ENG+Con_KOR.get(i));
        }

        for(int i=0; i<6; i++){
            editor.remove(KEY_THUMB_ID+Thumb_KOR[i]);
            editor.remove(KEY_THUMB_ENG_NAME+Thumb_KOR[i]);
        }

        //editor.remove(KEY_THUMB_ID);
        //editor.remove(KEY_THUMB_ENG_NAME);
        editor.remove(KEY_THUMB_KOR_NAME);
        //editor.remove(KEY_CON_ID);
        //editor.remove(KEY_CON_ENG);
        editor.remove(KEY_CON_KOR);

        editor.commit();
    }

    public CountryList getCountryList() {
        if (pref.getStringSet(KEY_CON_KOR, null) != null) {
            ArrayList<String> Con_KOR = new ArrayList<>(pref.getStringSet(KEY_CON_KOR,null));
            String[] Thumb_KOR = pref.getStringSet(KEY_THUMB_KOR_NAME,null).toArray(new String[6]);
            List<String> tmp_str;
            Map<String, List<String>> Dic_Con = new HashMap<String,List<String>>();
            for (int i=0;i<Con_KOR.size();i++){
                tmp_str= new ArrayList<>();
                tmp_str.add(pref.getString(KEY_CON_ID+Con_KOR.get(i),null));
                tmp_str.add(pref.getString(KEY_CON_ENG+Con_KOR.get(i),null));
                Dic_Con.put(Con_KOR.get(i),tmp_str);
            }

            List<String> tmp_str_Thumb;
            Map<String, List<String>> Dic_Thumb = new HashMap<String,List<String>>();
            for (int i=0;i<6;i++){
                tmp_str_Thumb= new ArrayList<>();
                tmp_str_Thumb.add(pref.getString(KEY_THUMB_ID+Thumb_KOR[i],null));
                tmp_str_Thumb.add(pref.getString(KEY_THUMB_ENG_NAME+Thumb_KOR[i],null));
                Dic_Con.put(Thumb_KOR[i],tmp_str_Thumb);
            }

            CountryList countryList=new CountryList();
            countryList.setCon_Dic(Dic_Con);
            countryList.setCountryList_KOR(Con_KOR);
            countryList.setThumb_Dic(Dic_Thumb);
            countryList.setThumb_KOR_name(Thumb_KOR);

            return countryList;
        }
        return null;
    }

    public void recordVersion(String version){
        editor.putString("Version",version);
        editor.commit();
    }

    public void updateVersion(String version){
        editor.remove("Version");
        editor.commit();
        editor.putString("Version",version);
        editor.commit();
    }
    public String getVersion(){
        return pref.getString("Version",null);
    }

    // message handler

    /* * *
    * 1st, group message handler
    */

    private static final String KEY_GROUP_MSG_1="group/_GID_/msg/_MID_/";
    private static final String KEY_GROUP_MSG_2="group/_GID_/last_msg_id";
    private static final String KEY_GROUP_MSG_3="group/_GID_/init_msg_id";

    public void storeGroupMessage(Message message, String RoomID){
        String MsgID=message.getId();
        final String KEY_GROUP_MSG1_final=KEY_GROUP_MSG_1.replace("_GID_",RoomID).replace("_MID_",MsgID);
        editor.putString(KEY_GROUP_MSG1_final+"message",message.getMessage());
        editor.putString(KEY_GROUP_MSG1_final+"created_at",message.getCreatedAt());
        editor.putString(KEY_GROUP_MSG1_final+"user_id",message.getUser().getId());
        editor.putString(KEY_GROUP_MSG1_final+"messagetype",message.getType());
        editor.putBoolean(KEY_GROUP_MSG1_final+"isRead",message.getIsRead());


        editor.commit();
    }

    public void updateLastGroupMessage(String RoomID, int last_msg_id){ // last_msg_id is the number of last message_id stored in sharedpref
        final String KEY_GROUP_MSG2_final=KEY_GROUP_MSG_2.replace("_GID_", RoomID);
        if(pref.getInt(KEY_GROUP_MSG2_final,0) < last_msg_id) {
            if (pref.getInt(KEY_GROUP_MSG2_final, 0) != 0) {
                editor.remove(KEY_GROUP_MSG2_final);
                editor.commit();
            }
            editor.putInt(KEY_GROUP_MSG2_final, last_msg_id);
            editor.commit();
        }
    }

    public void updateInitGroupMessage(String RoomID, int init_msg_id){ // last_msg_id is the number of last message_id stored in sharedpref
        final String KEY_GROUP_MSG3_final=KEY_GROUP_MSG_3.replace("_GID_", RoomID);

            if (pref.getInt(KEY_GROUP_MSG3_final, 0) != 0) {
                editor.remove(KEY_GROUP_MSG3_final);
                editor.commit();
            }
            editor.putInt(KEY_GROUP_MSG3_final, init_msg_id);
            editor.commit();

    }
    public void removeInitGroupMessage(String RoomID){
        final String KEY_GROUP_MSG3_final=KEY_GROUP_MSG_3.replace("_GID_", RoomID);
        editor.remove(KEY_GROUP_MSG3_final);
        editor.commit();
    }

    public int getLastGroupMessage(String RoomID){
        final String KEY_GROUP_MSG2_final=KEY_GROUP_MSG_2.replace("_GID_", RoomID);
        return pref.getInt(KEY_GROUP_MSG2_final,0);
    }

    public int getInitGroupMessage(String RoomID){
        final String KEY_GROUP_MSG3_final=KEY_GROUP_MSG_3.replace("_GID_", RoomID);
        return pref.getInt(KEY_GROUP_MSG3_final,0);
    }

    public Message getGroupMessage(String RoomID, String MsgID){
        final String KEY_GROUP_MSG1_final=KEY_GROUP_MSG_1.replace("_GID_",RoomID).replace("_MID_",MsgID);
        Message message=new Message();
        User user= new User();
        message.setMessage(pref.getString(KEY_GROUP_MSG1_final+"message",""));
        message.setCreatedAt(pref.getString(KEY_GROUP_MSG1_final+"created_at",""));
        user=getTmpUser(pref.getString(KEY_GROUP_MSG1_final+"user_id",null));
        // inferring tmp_user handler required
        // inferring tmp_user handler required
        message.setType(pref.getString(KEY_GROUP_MSG1_final+"messagetype",null));
        if(!pref.getBoolean(KEY_GROUP_MSG1_final+"isRead",false)){
            boolean tst=pref.getBoolean(KEY_GROUP_MSG1_final+"isRead",false);
            readRoomMessage(RoomID, MsgID); // 서버에 읽었다는 신호를 보내고 isRead를 true로 바꿔주기까지 포함되어 있음
        }
        message.setIsRead(true);
        message.setUser(user);

        return message;
    }

    public void removeGroupMessage(String RoomID, String MsgID){
        final String KEY_GROUP_MSG1_final=KEY_GROUP_MSG_1.replace("_GID_",RoomID).replace("_MID_",MsgID);
        editor.remove(KEY_GROUP_MSG1_final+"message");
        editor.remove(KEY_GROUP_MSG1_final+"created_at");
        editor.remove(KEY_GROUP_MSG1_final+"user_id");
        editor.remove(KEY_GROUP_MSG1_final+"messagetype");
        editor.remove(KEY_GROUP_MSG1_final+"isRead");
        editor.commit();
    }

    public void removeLastGroupMessage(String RoomID){
        final String KEY_GROUP_MSG2_final=KEY_GROUP_MSG_2.replace("_GID_", RoomID);
        editor.remove(KEY_GROUP_MSG2_final);
        editor.commit();
    }

    public int CountUnread_Room(String RoomID){
        int init_msg_id=pref.getInt(KEY_GROUP_MSG_3.replace("_GID_",RoomID),0);
        int last_msg_id=pref.getInt(KEY_GROUP_MSG_2.replace("_GID_",RoomID),0);
        int UnreadCount=0;
        String KEY_GROUP_MSG1_final;

        for(int idx=init_msg_id;idx<=last_msg_id;idx++){
            KEY_GROUP_MSG1_final=KEY_GROUP_MSG_1.replace("_GID_",RoomID).replace("_MID_",String.valueOf(idx));
            if(!pref.getBoolean(KEY_GROUP_MSG1_final+"isRead",true)){
                UnreadCount=UnreadCount+1;
            }
        }

        return UnreadCount;

    }

    public String ReadLastMsg_Room(String RoomID){
        int last_msg_id=pref.getInt(KEY_GROUP_MSG_2.replace("_GID_",RoomID),0);

        String KEY_GROUP_MSG1_final=KEY_GROUP_MSG_1.replace("_GID_",RoomID).replace("_MID_",String.valueOf(last_msg_id));;
        String last_msg=pref.getString(KEY_GROUP_MSG1_final+"message","");

        return last_msg;
    }

    public String ReadLastTimeStamp_Room(String RoomID){
        int last_msg_id=pref.getInt(KEY_GROUP_MSG_2.replace("_GID_",RoomID),0);

        String KEY_GROUP_MSG1_final=KEY_GROUP_MSG_1.replace("_GID_",RoomID).replace("_MID_",String.valueOf(last_msg_id));;
        String time_stamp=pref.getString(KEY_GROUP_MSG1_final+"created_at","");

        return time_stamp;
    }

    /* * *
    * 2nd, private message handler
    */

    private static final String KEY_PRIVATE_MSG_1="private/_SEND_/msg/_MID_/";
    private static final String KEY_PRIVATE_MSG_2="private/_SEND_/last_msg_id";
    private static final String KEY_PRIVATE_MSG_3="private/_SEND_/init_msg_id";

    public void storePrivateMessage(String SenderID, Message message){
        String MsgID=message.getId();
        final String KEY_PRIVATE_MSG1_final=KEY_PRIVATE_MSG_1.replace("_SEND_", SenderID).replace("_MID_",MsgID);
        editor.putString(KEY_PRIVATE_MSG1_final+"message",message.getMessage());
        editor.putString(KEY_PRIVATE_MSG1_final+"created_at",message.getCreatedAt());
        editor.putString(KEY_PRIVATE_MSG1_final+"user_id",message.getUser().getId());
        editor.putString(KEY_PRIVATE_MSG1_final+"messagetype",message.getType());
        editor.putBoolean(KEY_PRIVATE_MSG1_final+"isRead",message.getIsRead());

        editor.commit();
    }

    public void updateLastPrivateMessage(String SenderID, int last_msg_id){ // last_msg_id is the number of last message_id stored in sharedpref
        final String KEY_PRIVATE_MSG2_final=KEY_PRIVATE_MSG_2.replace("_SEND_",SenderID);
        if(pref.getInt(KEY_PRIVATE_MSG2_final,0) < last_msg_id) {
            if (pref.getInt(KEY_PRIVATE_MSG2_final, 0) != 0) {
                editor.remove(KEY_PRIVATE_MSG2_final);
                editor.commit();
            }
            editor.putInt(KEY_PRIVATE_MSG2_final, last_msg_id);
            editor.commit();
        }
    }

    public void updateInitPrivateMessage(String SenderID, int init_msg_id){ // last_msg_id is the number of last message_id stored in sharedpref
        final String KEY_PRIVATE_MSG3_final=KEY_PRIVATE_MSG_3.replace("_SEND_",SenderID);
        if(pref.getInt(KEY_PRIVATE_MSG3_final,0) < init_msg_id) {
            if (pref.getInt(KEY_PRIVATE_MSG3_final, 0) != 0) {
                editor.remove(KEY_PRIVATE_MSG3_final);
                editor.commit();
            }
            editor.putInt(KEY_PRIVATE_MSG3_final, init_msg_id);
            editor.commit();
        }
    }

    public void removeInitPrivateMessage(String SenderID){
        final String KEY_PRIVATE_MSG3_final=KEY_PRIVATE_MSG_3.replace("_SEND_",SenderID);
        editor.remove(KEY_PRIVATE_MSG3_final);
        editor.commit();
    }

    public int getLastPrivateMessage(String SenderID){
        return pref.getInt(KEY_PRIVATE_MSG_2.replace("_SEND_",SenderID),0);
    }
    public int getInitPrivateMessage(String SenderID){
        return pref.getInt(KEY_PRIVATE_MSG_3.replace("_SEND_",SenderID),0);
    }

    public Message getPrivateMessage(String SenderID, String MsgID){
        final String KEY_PRIVATE_MSG1_final=KEY_PRIVATE_MSG_1.replace("_SEND_",SenderID).replace("_MID_",MsgID);
        Message message=new Message();
        User user= new User();
        message.setMessage(pref.getString(KEY_PRIVATE_MSG1_final+"message",""));
        message.setCreatedAt(pref.getString(KEY_PRIVATE_MSG1_final+"created_at",""));
        
        user=getTmpUser(pref.getString(KEY_PRIVATE_MSG1_final+"user_id",null));
        // inferring tmp_user handler required
        // inferring tmp_user handler required
        message.setType(pref.getString(KEY_PRIVATE_MSG1_final+"messagetype",null));
        if(!pref.getBoolean(KEY_PRIVATE_MSG1_final+"isRead",false)){
            readPrivateMessage(this.getUser().getId(),SenderID,MsgID);
        }
        message.setIsRead(true);
        message.setUser(user);

        return message;
    }

    public void removePrivateMessage(String SenderID, String MsgID){
        final String KEY_PRIVATE_MSG1_final=KEY_PRIVATE_MSG_1.replace("_SEND_",SenderID).replace("_MID_",MsgID);
        editor.remove(KEY_PRIVATE_MSG1_final+"message");
        editor.remove(KEY_PRIVATE_MSG1_final+"created_at");
        editor.remove(KEY_PRIVATE_MSG1_final+"user_id");
        editor.remove(KEY_PRIVATE_MSG1_final+"messagetype");
        editor.remove(KEY_PRIVATE_MSG1_final+"isRead");
        editor.commit();
    }

    public void removeLastPrivateMessage(String SenderID){
        final String KEY_PRIVATE_MSG2_final=KEY_PRIVATE_MSG_2.replace("_SEND_",SenderID);
        editor.remove(KEY_PRIVATE_MSG2_final);
        editor.commit();
    }

    public int CountUnread_Private(String UserID){
        int init_msg_id=pref.getInt(KEY_PRIVATE_MSG_3.replace("_SEND_",UserID),0);
        int last_msg_id=pref.getInt(KEY_PRIVATE_MSG_2.replace("_SEND_",UserID),0);
        int UnreadCount=0;
        String KEY_PRIVATE_MSG1_final;

        for(int idx=init_msg_id;idx<=last_msg_id;idx++){
            KEY_PRIVATE_MSG1_final=KEY_PRIVATE_MSG_1.replace("_SEND_",UserID).replace("_MID_",String.valueOf(idx));
            if(!pref.getBoolean(KEY_PRIVATE_MSG1_final+"isRead",true)){
                UnreadCount=UnreadCount+1;
            }
        }

        return UnreadCount;

    }

    public String ReadLastMsg_Private(String UserID){
        int last_msg_id=pref.getInt(KEY_PRIVATE_MSG_2.replace("_SEND_",UserID),0);

        String KEY_PRIVATE_MSG1_final=KEY_PRIVATE_MSG_1.replace("_SEND_",UserID).replace("_MID_",String.valueOf(last_msg_id));;
        String last_msg=pref.getString(KEY_PRIVATE_MSG1_final+"message","");

        return last_msg;
    }

    public String ReadLastTimeStamp_Private(String UserID){
        int last_msg_id=pref.getInt(KEY_PRIVATE_MSG_2.replace("_SEND_",UserID),0);

        String KEY_PRIVATE_MSG1_final=KEY_PRIVATE_MSG_1.replace("_SEND_",UserID).replace("_MID_",String.valueOf(last_msg_id));;
        String time_stamp=pref.getString(KEY_PRIVATE_MSG1_final+"created_at","");

        return time_stamp;
    }


    /* * *
    * 3rd, temporal user handler
    */

    private static final String KEY_UID_1="tmp_user/_UID_/";
    private static final String KEY_UID_2="tmp_user/_UID_List"; // 어딘가 쓸일이 있겠지...
    private static final String KEY_UID_3="tmp_user/tmp_UID_List"; // this is for automatic user information refresh whenever main activity starts

    public void storeTmpUser(User user){
        final String KEY_UID_1_final=KEY_UID_1.replace("_UID_",user.getId());
        Boolean tmp_isOpen1;
        tmp_isOpen1=user.getisOpen();

        //editor.putBoolean(KEY_UID_1_final+"isOpen",user.getisOpen());

        testisOpento(pref.getString(KEY_USER_ID,null),user.getId(),KEY_UID_1_final+"isOpen",tmp_isOpen1);

        editor.putString(KEY_UID_1_final+"name",user.getName());
        editor.putString(KEY_UID_1_final+"LinkUri",user.getLinkUri());
        editor.putString(KEY_UID_1_final+"profile_photo_path",user.getProfile_path());

        editor.commit();

        if(!checkTmpUser(user.getId())) {
            updateUIDList(user.getId());
        }
    }

    public void updateUIDList(String UserID){
        /*
        Boolean toggle=false;

        String[] UID_List_array=UID_List.toArray(new String[UID_List.size()]);
        for(int i=0; i<UID_List.size(); i++){
            if(UID_List_array[i].equals(UserID)) {
                toggle = true;
                break;
            }
        }
        if(!toggle){ // update only if UserID not pre-exist in UID_List
        */
        Set<String> UID_List=pref.getStringSet(KEY_UID_2,new HashSet<String>());
            UID_List.add(UserID);
            editor.remove(KEY_UID_2);
            editor.commit();
            editor.putStringSet(KEY_UID_2,UID_List);
            editor.commit();
        //}


    }
    public Boolean checkTmpUser(String UserID) {
        // first check if whether UserID is listed in UID_List
        Boolean toggle = false;
        User user = new User();
        Set<String> UID_List = pref.getStringSet(KEY_UID_2, new HashSet<String>());
        if(UID_List!=null) {
            String[] UID_List_array = UID_List.toArray(new String[UID_List.size()]);
            for (int i = 0; i < UID_List.size(); i++) {
                if (UID_List_array[i].equals(UserID)) {
                    toggle = true; // UserID exist in UID_List
                    break;
                }
            }
        }
        return toggle;
    }


    public void removeTmpUsrTmpList(){
        editor.remove(KEY_UID_3);
        editor.commit();
    }

    public Boolean checkTmpUserTmpList(String UserID) { // tmp_UID_List에 해당 UserID가 이미 존재하는지 확인
        // first check if whether UserID is listed in UID_List
        Boolean toggle = false;
        User user = new User();
        Set<String> UID_List = pref.getStringSet(KEY_UID_3, new HashSet<String>());
        if(UID_List!=null) {
            String[] UID_List_array = UID_List.toArray(new String[UID_List.size()]);
            for (int i = 0; i < UID_List.size(); i++) {
                if (UID_List_array[i].equals(UserID)) {
                    toggle = true; // UserID exist in UID_List
                    break;
                }
            }
        }
        return toggle;
    }

    public void updateTmpUIDList(String UserID){ // tmp_UID_List에 UserID 등록 업데이트, 이미 존재하면 추가하지 않음.

        Set<String> UID_List=pref.getStringSet(KEY_UID_3,new HashSet<String>());

        UID_List.add(UserID);
        editor.remove(KEY_UID_3);
        editor.commit();
        editor.putStringSet(KEY_UID_3,UID_List);
        editor.commit();

    }

    public void removeTmpUser(String UserID){
        final String KEY_UID_1_final=KEY_UID_1.replace("_UID_",UserID);
        editor.remove(KEY_UID_1_final+"isOpen");
        editor.remove(KEY_UID_1_final+"name");
        editor.remove(KEY_UID_1_final+"LinkUri");
        editor.remove(KEY_UID_1_final+"profile_photo_path");
        editor.commit();
    }

    public void updateTmpUsers(){
        Set<String> UID_List=pref.getStringSet(KEY_UID_2,new HashSet<String>());
        String[] UID_array=UID_List.toArray(new String[UID_List.size()]);
        for(int i=0; i < UID_List.size(); i++){
            updateTmpUserInfo(UID_array[i],false);
        }

    }

    public User getTmpUser(String UserID){ //언제나 안심하고 호출하길! 최초 UID 등록은 FIREBASE NOTIFICATION 혹은 후에 추가할
        // fetchmyAllmessage 시작 시에 추가할 것이고, (checkTmpUser로 UID_LIST에 존재하는 지 확인 후 존재하지 않으면 추가하는 방식으로 이루어짐,
        // checkTmpUserTmpList는 ChatRoomActivity 시작시 마다 call되며, 항상 공백상태로 시작되고, 각 유저에 대한 프로필 정보를 tmp_user에 업데이트
        // 할 시, 이미 업데이트가 완료 된 유저 프로필 정보를 저장하는 공간임.
        // get target User Info from shared preference
        final String KEY_UID_1_final=KEY_UID_1.replace("_UID_",UserID);
        User user=new User();
        user.setId(UserID);
        user.setisOpen(pref.getBoolean(KEY_UID_1_final+"isOpen",false));
        user.setName(pref.getString(KEY_UID_1_final+"name",""));
        user.setLinkUri(pref.getString(KEY_UID_1_final+"LinkUri",""));
        user.setProfile_path(pref.getString(KEY_UID_1_final+"profile_photo_path","default"));

        return user;

    }

    public void updateTmpUserInfo(String userID, Boolean TmpLIST_update){ // sharepref에 user관련 정보를 업데이트 한다는 뜻임
        final String userID_=userID;
        final Boolean TmpLIST_update_=TmpLIST_update;

        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.GET_CHAT_USER.replace("_ID_",userID_), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        // user successfully logged in

                        JSONObject userObj = obj.getJSONObject("user");
                        User user = new User(userObj.getString("user_id"),
                                userObj.getString("name"),
                                userObj.getString("LinkUri"),userObj.getString("profile_photo_path"));

                        user.setisOpen(userObj.getBoolean("isOpen"));

                        // storing user in shared preferences
                        if(!checkTmpUserTmpList(user.getId())) { // 아직 업데이트가 진행되지 않은 ID임
                            try {
                                removeTmpUser(user.getId()); // 이전 기록 지우고
                            }catch(Exception e){}
                            storeTmpUser(user); // 새 기록 작성
                            if(TmpLIST_update_)
                            updateTmpUIDList(user.getId()); // 업데이트 진행 완료 되었다는 것을 기입함
                        } // 그 외의 경우에는 관련 정보를 보유 중...


                    } else {
                        // login error - simply toast the message
                    }

                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
            }
        }) {

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    public void readRoomMessage(String RoomID, final String MsgID){
        final String RoomID_=RoomID;
        final String KEY_GROUP_MSG1_final=KEY_GROUP_MSG_1.replace("_GID_",RoomID).replace("_MID_",MsgID);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.READ_CHAT_ROOM_MESSAGE.replace("_ID_",RoomID_), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        // user successfully logged in

                        editor.remove(KEY_GROUP_MSG1_final+"isRead");
                        editor.commit();
                        editor.putBoolean(KEY_GROUP_MSG1_final+"isRead",true);
                        editor.commit();


                    } else {
                        // login error - simply toast the message
                    }

                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
            }
        }) {

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("message_id",MsgID);

                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    public void readPrivateMessage(String UserId, String SenderID,final String MsgID){
        final String UserId_=UserId;
        final String SenderID_=SenderID;
        final String KEY_PRIVATE_MSG1_final=KEY_PRIVATE_MSG_1.replace("_MID_",MsgID).replace("_SEND_",SenderID);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.READ_CHAT_PRIVATE_MESSAGE.replace("_ID_",UserId_), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        // user successfully logged in

                        editor.remove(KEY_PRIVATE_MSG1_final+"isRead");
                        editor.commit();
                        editor.putBoolean(KEY_PRIVATE_MSG1_final+"isRead",true);
                        editor.commit();


                    } else {
                        // login error - simply toast the message
                    }

                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
            }
        }) {

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("message_id",MsgID);
                params.put("senderID",SenderID_);

                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    public void updateSharedRoomLast(String RoomID){
        final String RoomID_=RoomID;
        final String KEY_GROUP_MSG2_final=KEY_GROUP_MSG_2.replace("_GID_",RoomID);
        final int last_msg_id=pref.getInt(KEY_GROUP_MSG2_final,0);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.UPDATE_ROOM_PREVIOUS_LAST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        // previous last_msg_id successfully updated


                    } else {
                        // login error - simply toast the message
                        Log.e(TAG, "Update Failed(1)");
                    }

                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
            }
        }) {

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("message_id",String.valueOf(last_msg_id));
                params.put("chat_room_id",RoomID_);
                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }
    public void updateSharedPrivateLast(String SenderID){
        final String senderID=SenderID;
        final String KEY_PRIVATE_MSG_2_final=KEY_PRIVATE_MSG_2.replace("_SEND_",senderID);
        final int last_msg_id=pref.getInt(KEY_PRIVATE_MSG_2_final,0);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.UPDATE_PRIVATE_PREVIOUS_LAST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        // previous last_msg_id successfully updated


                    } else {
                        // login error - simply toast the message
                        Log.e(TAG, "Update Failed(2)");
                    }

                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
            }
        }) {

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("message_id",String.valueOf(last_msg_id));
                params.put("senderID",senderID);
                params.put("receiverID",pref.getString(KEY_USER_ID,null));
                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }



    public void testisOpento(final String RID, final String SID, final String SharedPref_address, final Boolean isOpen1){ // sharepref에 user관련 정보를 업데이트 한다는 뜻임


        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.TEST_ISOPENTO.replace("_RID_",RID).replace("_SID_",SID), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                final Boolean isOpen2=Boolean.valueOf(response);
                final Boolean isOpen3=(isOpen1 || isOpen2);

                editor.putBoolean(SharedPref_address,isOpen3);

                editor.commit();

                Boolean isOpen_check=pref.getBoolean(SharedPref_address,false);

                Log.e(TAG,"debug...");

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
            }
        }) {

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }



}