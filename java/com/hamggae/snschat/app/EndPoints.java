package com.hamggae.snschat.app;

/**
 * Created by seungjun on 2016-12-23.
 */

public class EndPoints {

    // localhost url -
    public static final String BASE_URL = "http://52.78.17.67/gcm_chat/v1";

    /*
     here we delcare REST API endpoint urls.
     If you are testing the app on localhost, use the correct Ip address of the computer on which
     php services are running.
    */

    public static final String LOGIN = BASE_URL + "/user/login";
    public static final String USER = BASE_URL + "/user/_ID_";
    public static final String CHAT_ROOMS = BASE_URL + "/chat_rooms";
    public static final String CHAT_MYROOMS = BASE_URL + "/MY_chat_rooms/_ID_";
    public static final String CHAT_MYROOMS_IDS = BASE_URL + "/MY_chat_rooms_ids/_ID_";
    public static final String FETCH_CHATROOM_MSG = BASE_URL + "/CHATROOM/_ID_/_UID_/LAST_MSG/_MID_/_IID_";
    public static final String CHAT_THREAD = BASE_URL + "/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";
    public static final String READ_CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/readMessage";
    public static final String READ_CHAT_PRIVATE_MESSAGE = BASE_URL + "/chat_private/_ID_/readMessage";
    public static final String Country_Names = BASE_URL + "/FetchAllCountry";
    public static final String Country_ROOMS = BASE_URL + "/COUNTRY_chat_rooms/_ID_";
    public static final String CHAT_JOIN = BASE_URL + "/JoinChatroom";
    public static final String CHAT_CREATE = BASE_URL + "/CreateChatroom";
    public static final String COUNTRY_THUMB_URL = BASE_URL + "/country_thumbs/_COUNTRY_.png";
    public static final String COUNTRY_HIT = BASE_URL + "/Countryhits/_ID_";
    public static final String PROFILE_UPDATE = BASE_URL + "/Profile_update";
    public static final String PROFILE_UPLOAD_URL = BASE_URL+"/profile_Upload.php";
    public static final String LEAVE_CHAT = BASE_URL + "/LeaveChatroom";
    public static final String Country_Six = BASE_URL + "/FetchSixCountry";
    public static final String CHECK_VERSION = BASE_URL + "/CheckVersion";
    public static final String PRIVATE_MESSAGE = BASE_URL + "/users/_ID_/message"; // private chat handler
    public static final String GET_CHAT_USER = BASE_URL + "/GetUserInfo/_ID_";
    public static final String LEAVE_PRIVATE_CHAT = BASE_URL + "/LeavePrivateChat";
    public static final String CHAT_MYPRIVATE_IDS = BASE_URL + "/MY_chat_private_ids/_ID_";
    public static final String FETCH_CHATPRIVATE_MSG = BASE_URL + "/RECEIVER/_RID_/_SID_/LAST_MSG/_MID_/_IID_";
    public static final String UPDATE_ROOM_PREVIOUS_LAST = BASE_URL + "/SharedPref/Room";
    public static final String UPDATE_PRIVATE_PREVIOUS_LAST = BASE_URL + "/SharedPref/Private";
    public static final String CHAT_MYPRIVATES = BASE_URL + "/MY_chat_privates/_RID_";
    public static final String REQUEST_PROFILE = BASE_URL + "/profile/request";
    public static final String FETCH_PROFILE = BASE_URL + "/profile/fetch/_RID_";
    public static final String ALLOW_PROFILE = BASE_URL + "/profile/allow";
    public static final String DENY_PROFILE = BASE_URL + "/profile/deny";
    public static final String UPDATE_ISOPEN_PROFILE = BASE_URL + "/profile/update";
    public static final String TEST_ISOPENTO = BASE_URL + "/profile/test/_RID_/_SID_";
    public static final String GET_USERS_CLOSE= BASE_URL + "/marker/loc/_LATITUDE_/_LONGITUDE_/_MULTIPLIER_";
    public static final String MARKER_UPDAE= BASE_URL + "/marker/user_update";
    public static final String MARKER_ADD= BASE_URL + "/marker/marker_add";
    public static final String MARKER_UPLOAD_URL=BASE_URL+"/marker_Upload.php";
    public static final String MARKER_GET_MEMORY=BASE_URL+"/marker/get_Memory/_UID_";
    public static final String ROOM_UPLOAD_URL=BASE_URL+"/Room_Upload.php";
    public static final String PRIVATE_UPLOAD_URL=BASE_URL+"/Private_Upload.php";
}