package com.hamggae.snschat.fragment;

/**
 * Created by seungjun on 2017-01-13.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hamggae.snschat.R;
import com.hamggae.snschat.activity.ChatPrivateActivity;
import com.hamggae.snschat.activity.ChatRoomActivity;
import com.hamggae.snschat.activity.MainActivity;
import com.hamggae.snschat.adapter.ChatRoomsAdapter;
import com.hamggae.snschat.app.Config;
import com.hamggae.snschat.app.EndPoints;
import com.hamggae.snschat.app.MyApplication;
import com.hamggae.snschat.helper.SimpleDividerItemDecoration;
import com.hamggae.snschat.model.ChatRoom;
import com.hamggae.snschat.model.Message;
import com.hamggae.snschat.model.User;
import com.hamggae.snschat.util.NotificationUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class OneFragment extends Fragment{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private FragmentActivity Activity_;
    private Context Context_;
    private View Li;
    private PopupWindow mPopupWindow;

    private ArrayList<ChatRoom> chatRoomArrayList;
    private ChatRoomsAdapter mAdapter;
    private RecyclerView recyclerView;

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private Boolean register_ = false;
    private String UserID = null;
    private LinearLayoutManager layoutManager;
    private boolean view_toggle=false;
    private TextView Roomname,chat_exit;
    private int UnreadCount;
    private boolean toggle=false;


    public OneFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    Log.e(TAG, "gcm registreation completed");

                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION) || intent.getAction().equals(Config.PUSH_NOTIFICATION_PRIVATE)) {
                    // new push notification is received

                    handlePushNotification(intent);

                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        CountUnread_Room();
        mAdapter.notifyDataSetChanged();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(Activity_).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(Activity_).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        LocalBroadcastManager.getInstance(Activity_).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION_PRIVATE));

        LocalBroadcastManager.getInstance(Activity_).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION_FIREBASE));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(Context_);


    }


    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(Activity_).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    public void fetchMyChatRooms() {

        if(!(UserID==null)) {
            String endPoint = EndPoints.CHAT_MYROOMS.replace("_ID_", UserID);
            Log.e(TAG, "endPoint" + endPoint);

            StringRequest strReq = new StringRequest(Request.Method.GET,
                    endPoint, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "response: " + response);

                    try {
                        JSONObject obj = new JSONObject(response);

                        // check for error flag
                        if (obj.getBoolean("error") == false) {
                            JSONArray chatRoomsArray = obj.getJSONArray("chat_rooms");
                            for (int i = 0; i < chatRoomsArray.length(); i++) {
                                JSONObject chatRoomsObj = (JSONObject) chatRoomsArray.get(i);
                                ChatRoom cr = new ChatRoom();
                                cr.setId(chatRoomsObj.getString("chat_room_id"));
                                cr.setName(chatRoomsObj.getString("name"));
                                cr.setLastMessage("");
                                cr.setUnreadCount(0);
                                cr.setInfo(chatRoomsObj.getString("info"));
                                cr.setCountry_id( Integer.parseInt(chatRoomsObj.getString("country_id")) );
                                cr.setTimestamp(chatRoomsObj.getString("created_at"));
                                cr.setMemberCount(chatRoomsObj.getString("MemberCount"));
                                cr.setKOR_name(chatRoomsObj.getString("KOR_name"));
                                cr.setIsRoom(true);

                                chatRoomArrayList.add(cr); // 채팅방 새로 생성 순간, 그룹 채팅방에 해당
                            }
                            CountUnread_Room();

                        } else {
                            // error in fetching chat rooms
                            Toast.makeText(Context_, "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "json parsing error: " + e.getMessage());
                        Toast.makeText(Context_, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    mAdapter.notifyDataSetChanged();

                    // subscribing to all chat room topics
                    subscribeToAllTopics();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                    Toast.makeText(Context_, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            //Adding request to request queue
            MyApplication.getInstance().addToRequestQueue(strReq);
        }
    }

    public void fetchMyChatPrivates() {

        if(!(UserID==null)) {
            String endPoint = EndPoints.CHAT_MYPRIVATES.replace("_RID_", UserID);
            Log.e(TAG, "endPoint" + endPoint);

            StringRequest strReq = new StringRequest(Request.Method.GET,
                    endPoint, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "response: " + response);

                    try {
                        JSONObject obj = new JSONObject(response);

                        // check for error flag
                        if (obj.getBoolean("error") == false) {
                            JSONArray chatRoomsArray = obj.getJSONArray("chats");
                            for (int i = 0; i < chatRoomsArray.length(); i++) {
                                JSONObject chat = (JSONObject) chatRoomsArray.get(i);
                                ChatRoom cr = new ChatRoom();
                                cr.setId(chat.getString("user_id"));
                                cr.setName(chat.getString("name"));
                                cr.setLastMessage("");
                                cr.setUnreadCount(0);
                                cr.setCountry_id(0);
                                cr.setTimestamp("");
                                cr.setMemberCount("2");
                                cr.setKOR_name("");
                                cr.setIsRoom(false);

                                chatRoomArrayList.add(cr); // 채팅방 새로 생성 순간, 개인 채팅방에 해당
                            }
                            CountUnread_Room();

                        } else {
                            // error in fetching chat rooms
                            Toast.makeText(Context_, "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "json parsing error: " + e.getMessage());
                        Toast.makeText(Context_, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    mAdapter.notifyDataSetChanged();

                    // subscribing to all chat room topics
                    subscribeToAllTopics();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                    Toast.makeText(Context_, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            //Adding request to request queue
            MyApplication.getInstance().addToRequestQueue(strReq);
        }
    }


    private void subscribeToAllTopics() {
        for (ChatRoom cr : chatRoomArrayList) {
            if(cr.getIsRoom()) {
                FirebaseMessaging.getInstance().subscribeToTopic("topic_" + cr.getId());
            }

        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(Activity_);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(Activity_, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(Context_, "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                Activity_.finish();
            }
            return false;
        }
        return true;
    }

    // Fetches reg id from shared preferences
    // and displays on the screen


    // 채팅방 메시지들 테이블 (간략 뷰) 업데이트

    public void handlePushNotification(Intent intent) {
        int type = intent.getIntExtra("type", -1);

        // if the push is of chat room message
        // simply update the UI unread messages count
        if (type == Config.PUSH_TYPE_CHATROOM) {
            Message message = (Message) intent.getSerializableExtra("message");
            String chatRoomId = intent.getStringExtra("chat_room_id");
            String chatRoomName = intent.getStringExtra("chat_room_name");
            Toast.makeText(Context_, chatRoomName+"\n"+message.getUser().getName() + ": " + message.getMessage(), Toast.LENGTH_LONG).show();

            if (message != null && chatRoomId != null) {
                updateRow(chatRoomId, "",message);
            }
        } else if (type == Config.PUSH_TYPE_USER) {
            // push belongs to user alone
            // just showing the message in a toast
            Message message = (Message) intent.getSerializableExtra("message");
            String user_id = intent.getStringExtra("user_id");
            String user_name = intent.getStringExtra("user_name");
            Toast.makeText(Context_, user_name + ": " + message.getMessage(), Toast.LENGTH_LONG).show();
            if (message != null && user_id != null) {
                updateRow(user_id, user_name, message);
            }

        }


    }

    /**
     * Updates the chat list unread count and the last message
     */

    private void CountUnread_Room(){
        for(ChatRoom cr: chatRoomArrayList){
            if(cr.getIsRoom()) {
                UnreadCount = MyApplication.getInstance().getPrefManager().CountUnread_Room(cr.getId());
                cr.setUnreadCount(UnreadCount);
                cr.setLastMessage(MyApplication.getInstance().getPrefManager().ReadLastMsg_Room(cr.getId()));
                cr.setTimestamp(MyApplication.getInstance().getPrefManager().ReadLastTimeStamp_Room(cr.getId()));
            }
            else{
                UnreadCount = MyApplication.getInstance().getPrefManager().CountUnread_Private(cr.getId());
                cr.setUnreadCount(UnreadCount);
                cr.setLastMessage(MyApplication.getInstance().getPrefManager().ReadLastMsg_Private(cr.getId()));
                cr.setTimestamp(MyApplication.getInstance().getPrefManager().ReadLastTimeStamp_Private(cr.getId()));
            }
        }
    }

    private void updateRow(String chatRoomId, String user_name, Message message) { // update chat_rooms for new messages
        Boolean chatroom_check = false;
        for (ChatRoom cr : chatRoomArrayList) {
            if (cr.getId().equals(chatRoomId)){
                if(cr.getIsRoom()) { // 그룹 채팅방 타입이며, ID가 같을 시에만. 채팅방 이미 생성된 거에서 업데이트 하는 순간임
                    int index = chatRoomArrayList.indexOf(cr);
                    cr.setLastMessage(message.getMessage());
                    cr.setUnreadCount(cr.getUnreadCount() + 1);
                    cr.setTimestamp(message.getCreatedAt());
                    chatRoomArrayList.remove(index);
                    chatRoomArrayList.add(0, cr);
                    chatroom_check=true;
                    break;
                }
                else {              // (!cr.getIsRoom()) 인 경우. 개인 채팅방 타입이며, ID가 같을 시에만. 채팅방 이미 생성된 거에서 업데이트 하는 순간임
                    int index = chatRoomArrayList.indexOf(cr);
                    cr.setLastMessage(message.getMessage());
                    cr.setUnreadCount(cr.getUnreadCount() + 1);
                    cr.setTimestamp(message.getCreatedAt());
                    chatRoomArrayList.remove(index);
                    chatRoomArrayList.add(0, cr);
                    chatroom_check = true; // chatroom_check은 기존에 있던 방이었다는 확인 토글
                    break;
                }
            }

        }
        if(!chatroom_check) // chat room id is not existing on ChatRoomArrayList, need to create one. (유저가 보낸 메시지일 경우 해당)
        {
            ChatRoom cr = new ChatRoom(chatRoomId, user_name,MyApplication.getInstance().getPrefManager().ReadLastMsg_Private(chatRoomId),"",0,0,"");
            //here, chatRoomId is UserId
            cr.setIsRoom(false);
            cr.setLastMessage(message.getMessage());
            cr.setUnreadCount(cr.getUnreadCount() + 1);  // 채팅방이 새로 생성되는 순간, 새로운 유저가 메시지를 보냈을 경우 해당.
            cr.setTimestamp(message.getCreatedAt());
            chatRoomArrayList.add(0, cr);
        }


        mAdapter.notifyDataSetChanged();
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        // checking for valid login session
        User user = MyApplication.getInstance().getPrefManager().getUser();
        if (user == null) {
            // TODO
            // user not found, redirecting him to login screen
            return;
        }

        String endPoint = EndPoints.USER.replace("_ID_", user.getId());

        Log.e(TAG, "endpoint: " + endPoint);

        StringRequest strReq = new StringRequest(Request.Method.PUT,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        // broadcasting token sent to server
                        Intent registrationComplete = new Intent(Config.SENT_TOKEN_TO_SERVER);
                        LocalBroadcastManager.getInstance(Context_).sendBroadcast(registrationComplete);
                    } else {
                        Toast.makeText(Context_, "Unable to send gcm registration id to our sever. " + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(Context_, "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(Context_, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("gcm_registration_id", token);

                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);

        Log.e(TAG, "sendRegistrationToServer: " + token);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        UserID = MyApplication.getInstance().getPrefManager().getUser().getId();
        Activity_=getActivity();
        Context_=Activity_.getApplicationContext();
        view_toggle=true;

        Li= inflater.inflate(R.layout.fragment_one, container, false);

        recyclerView = (RecyclerView) Li.findViewById(R.id.recycler_view);



        chatRoomArrayList = new ArrayList<>();
        mAdapter = new ChatRoomsAdapter(Activity_, chatRoomArrayList);
        layoutManager  = new LinearLayoutManager(Activity_);
        Log.e(TAG, "before displaying recyclerView");
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                Context_
        ));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new ChatRoomsAdapter.RecyclerTouchListener(Context_, recyclerView, new ChatRoomsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
                ChatRoom chatRoom = chatRoomArrayList.get(position);
                if(chatRoom.getIsRoom()) {
                    Intent intent = new Intent(Activity_, ChatRoomActivity.class);
                    intent.putExtra("chat_room_id", chatRoom.getId());
                    intent.putExtra("name", chatRoom.getName());

                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(Activity_, ChatPrivateActivity.class);
                    intent.putExtra("user_id", chatRoom.getId());

                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                ChatRoom chatRoom = chatRoomArrayList.get(position);
                ExitChatHandler(chatRoom);

            }
        }));

        register_=true;

        if(checkPlayServices()){
            if(register_){
                fetchMyChatRooms();
                fetchMyChatPrivates();
            }
        }

        toggle=true;
        // Inflate the layout for Activity_ fragment
        return Li;
    }

    public String getFragmentTag(int pos){
        return "android:switcher:"+R.id.viewpager+":"+pos;
    }

    private void LeaveChatroom(String RoomId) {

        final String RoomID= RoomId;

            String endPoint = EndPoints.LEAVE_CHAT;
            Log.e(TAG, "endPoint" + endPoint);

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    endPoint, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "response: " + response);

                    try {
                        JSONObject obj = new JSONObject(response);

                        // check for error
                        if (obj.getBoolean("error") == false) {

                            FirebaseMessaging.getInstance().unsubscribeFromTopic("topic_" + RoomID);

                            mPopupWindow.dismiss();
                            Fragment frg = null;
                            frg = getFragmentManager().findFragmentByTag(getFragmentTag(1));
                            final FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.detach(frg);
                            ft.attach(frg);
                            ft.commit();


                        } else {
                            Toast.makeText(Context_, "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "json parsing error: " + e.getMessage());
                        Toast.makeText(Context_, "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                    Toast.makeText(Context_, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("UserID", UserID);
                    params.put("RoomID", RoomID);

                    Log.e(TAG, "Params: " + params.toString());

                    return params;
                }


            };
            //Adding request to request queue
            MyApplication.getInstance().addToRequestQueue(strReq);


    };

    private void LeavePrivateChat(String UserId) {

        final String UserId_= UserId;

        String endPoint = EndPoints.LEAVE_PRIVATE_CHAT;
        Log.e(TAG, "endPoint" + endPoint);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {

                        Fragment frg = null;
                        frg = getFragmentManager().findFragmentByTag(getFragmentTag(1));
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(frg);
                        ft.attach(frg);
                        ft.commit();
                        mPopupWindow.dismiss();


                    } else {
                        Toast.makeText(Context_, "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(Context_, "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(Context_, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("UserID", UserID); //  RECEIVER ID
                params.put("senderID", UserId_); // SENDER ID

                Log.e(TAG, "Params: " + params.toString());

                return params;
            }


        };
        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);


    };

    protected void ExitChatHandler(ChatRoom chatRoom){
        LayoutInflater inflater_ = (LayoutInflater) Context_.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View customView = inflater_.inflate(R.layout.longclick_chatroom,null);



        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );


        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }

        // Get a reference for the custom view close button
        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);

        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow.setFocusable(true);
        mPopupWindow.update();

        mPopupWindow.showAtLocation(recyclerView, Gravity.CENTER,0,0);


        View contentView = mPopupWindow.getContentView();

        Roomname = (TextView) contentView.findViewById(R.id.RoomName);
        chat_exit = (TextView) contentView.findViewById(R.id.exit_chat);

        Roomname.setText(chatRoom.getName()); // 개인톡인 경우 유저 이름으로 대체됨
        chat_exit.setText("나가기");
        if(chatRoom.getIsRoom()) {
            final String Room_Id_ = chatRoom.getId();

            chat_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LeaveChatroom(Room_Id_);
                    int last_msg_id = MyApplication.getInstance().getPrefManager().getLastGroupMessage(Room_Id_);
                    int init_msg_id=MyApplication.getInstance().getPrefManager().getInitGroupMessage(Room_Id_);
                    MyApplication.getInstance().getPrefManager().removeInitGroupMessage(Room_Id_); // 방을 떠났다가 다시 돌아올때를 대비해서 init_msg_id를 삭제
                    for (int idx = init_msg_id; idx <= last_msg_id; idx++) {
                        try {
                            MyApplication.getInstance().getPrefManager().removeGroupMessage(Room_Id_, String.valueOf(idx));
                        } catch (Exception e) {

                        }
                    }
                }
            });
        }else{
            final String User_Id_ = chatRoom.getId();

            chat_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LeavePrivateChat(User_Id_);
                    int last_msg_id = MyApplication.getInstance().getPrefManager().getLastPrivateMessage(User_Id_);
                    int init_msg_id=MyApplication.getInstance().getPrefManager().getInitPrivateMessage(User_Id_);
                    MyApplication.getInstance().getPrefManager().removeInitPrivateMessage(User_Id_); // 방을 떠났다가 다시 돌아올때를 대비해서 init_msg_id를 삭제
                    for (int idx = init_msg_id; idx <= last_msg_id; idx++) {
                        try {
                            MyApplication.getInstance().getPrefManager().removeGroupMessage(User_Id_, String.valueOf(idx));
                        } catch (Exception e) {

                        }
                    }
                }
            });
        }
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if(toggle) {
            if (visible) {
                Fragment frg = null;
                frg = getFragmentManager().findFragmentByTag(getFragmentTag(1));
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.attach(frg);
                ft.commit();
            }
        }
    }

}