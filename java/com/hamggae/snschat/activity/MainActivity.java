package com.hamggae.snschat.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


import com.google.android.gms.maps.MapView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hamggae.snschat.R;
import com.hamggae.snschat.app.Config;
import com.hamggae.snschat.fragment.FourFragment;
import com.hamggae.snschat.adapter.ChatRoomsAdapter;
import com.hamggae.snschat.app.EndPoints;
import com.hamggae.snschat.app.MyApplication;
import com.hamggae.snschat.fragment.FourFragment_init;
import com.hamggae.snschat.fragment.ThreeFragmentLauncher;
import com.hamggae.snschat.model.ChatRoom;
import com.hamggae.snschat.model.CountryList;
import com.hamggae.snschat.model.Message;
import com.hamggae.snschat.model.User;
import com.hamggae.snschat.fragment.OneFragment;
import com.hamggae.snschat.fragment.TwoFragment;
import com.hamggae.snschat.fragment.ThreeFragment;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {


    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private ArrayList<ChatRoom> chatRoomArrayList;
    private ChatRoomsAdapter mAdapter;
    private RecyclerView recyclerView;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private Toolbar toolbar;
    private FragmentDrawer drawerFragment;

    private static final String TAG = MainActivity.class.getSimpleName();


    private Boolean register_ = false;
    private String UserID = null;

    private int[] tabIcons = {
            R.drawable.explore,
            R.drawable.chats,
            R.drawable.mate,
            R.drawable.friend
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(MyApplication.getInstance().getPrefManager().getUser()==null){
            launchLoginActivity();

        }



        UserID = MyApplication.getInstance().getPrefManager().getUser().getId();

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();

        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(3);

        this.getSupportActionBar().setTitle("Trip talktalk");

        if(!(FirebaseInstanceId.getInstance().getToken()==null))
        {
            String token_=FirebaseInstanceId.getInstance().getToken();
            Log.e(TAG, "Token name is : " + token_);
            register_=true;
            sendRegistrationToServer(token_);
        }

        CheckVersion();

        fetchAllGroupMsg();
        fetchAllPrivateMsg();

        MyApplication.getInstance().getPrefManager().removeTmpUsrTmpList(); // TmpUserList reset
        MyApplication.getInstance().getPrefManager().updateTmpUsers();


        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MapView mv = new MapView(getApplicationContext());
                    mv.onCreate(null);
                    mv.onPause();
                    mv.onDestroy();
                }catch (Exception ignored){

                }
            }
        }).start();
        */

        /*
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.addDrawerListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        */
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TwoFragment(), "TWO"); //TwoFragment is about explore
        adapter.addFragment(new OneFragment(), "ONE"); //OneFragment is about chat
        adapter.addFragment(new ThreeFragment(), "THREE"); //ThreeFragment is about mate
        adapter.addFragment(new FourFragment_init(), "Four"); //FourFragment is about friend
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // return null to display only the icon
            return null;
        }
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(MainActivity.this, FacebookLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onPause() {

        super.onPause();
    }



/*
    case R.id.action_logout:
    sendRegistrationToServer("null");
    MyApplication.getInstance().logout();
*/





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
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(registrationComplete);
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to send gcm registration id to our sever. " + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onDrawerItemSelected(View view, int position) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_logout:
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void CheckVersion() {

        String endPoint = EndPoints.CHECK_VERSION;
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
                        String Version = obj.getString("Version");
                        if(MyApplication.getInstance().getPrefManager().getVersion()==null){ // Very beginning of service
                            MyApplication.getInstance().getPrefManager().recordVersion(Version);
                        }
                        else{
                            if(!MyApplication.getInstance().getPrefManager().getVersion().equals(Version)){ // version updated in server
                                MyApplication.getInstance().getPrefManager().clearCountryInfo();
                                MyApplication.getInstance().getPrefManager().updateVersion(Version);
                                Toast.makeText(getApplicationContext(), "업데이트가 진행됩니다.", Toast.LENGTH_LONG).show();
                            }
                             // current version
                        }

                    } else {
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                }


                // subscribing to all chat room topics
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
            }
        });
        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);

    }


    private void fetchAllGroupMsg() {

        String endPoint = EndPoints.CHAT_MYROOMS_IDS.replace("_ID_", MyApplication.getInstance().getPrefManager().getUser().getId());
        Log.e(TAG, "endPoint: " + endPoint);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        JSONArray commentsObj = obj.getJSONArray("chat_rooms");

                        for (int i = 0; i < commentsObj.length(); i++) {

                            JSONObject commentObj = (JSONObject) commentsObj.get(i);

                            String CR_ROOM_id = commentObj.getString("chat_room_id");
                            fetchAllGroupMsg2(CR_ROOM_id,String.valueOf(MyApplication.getInstance().getPrefManager().getLastGroupMessage(CR_ROOM_id)));

                        }


                    } else {

                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "일시적 서버 오류 (1) 입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }



    private void fetchAllGroupMsg2(String CR_ROOM_ID, String last_msg_id) {

        final String CR_ROOM_ID_=CR_ROOM_ID;

        String endPoint = EndPoints.FETCH_CHATROOM_MSG.replace("_ID_", CR_ROOM_ID).replace("_UID_", UserID).replace("_MID_",last_msg_id)
                .replace("_IID_",String.valueOf(MyApplication.getInstance().getPrefManager().getInitGroupMessage(CR_ROOM_ID_)));
        Log.e(TAG, "endPoint: " + endPoint);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        JSONArray commentsObj = obj.getJSONArray("messages");

                        for (int i = 0; i < commentsObj.length(); i++) {

                            JSONObject commentObj = (JSONObject) commentsObj.get(i);

                            String chatRoomId = commentObj.getString("chat_room_id");

                            String commentId = commentObj.getString("message_id");
                            String commentText = commentObj.getString("message");
                            String createdAt = commentObj.getString("created_at");
                            String msgType=commentObj.getString("messagetype");

                            JSONObject userObj = commentObj.getJSONObject("user");
                            String userId = userObj.getString("user_id");
                            String userName = userObj.getString("name");
                            String LinkUri=userObj.getString("LinkUri");
                            String profile_photo_path = userObj.getString("profile_photo_path");
                            boolean isOpen = Boolean.parseBoolean(userObj.getString("isOpen"));
                            User user = new User(userId, userName, LinkUri, profile_photo_path);
                            user.setisOpen(isOpen);

                            Message message = new Message();
                            message.setId(commentId);
                            message.setMessage(commentText);
                            message.setCreatedAt(createdAt);
                            message.setUser(user);
                            message.setType(msgType);
                            if(userId != MyApplication.getInstance().getPrefManager().getUser().getId())
                            message.setIsRead(false);
                            else
                            message.setIsRead(true);

                            if(!MyApplication.getInstance().getPrefManager().checkTmpUser(userId)){
                                MyApplication.getInstance().getPrefManager().storeTmpUser(user);
                            }

                            MyApplication.getInstance().getPrefManager().storeGroupMessage(message,chatRoomId); //서버에 등록 마친 메시지 sharedpref에 저장




                        }
                        MyApplication.getInstance().getPrefManager().updateLastGroupMessage(CR_ROOM_ID_,Integer.parseInt(obj.getString("last_msg_id"))); // 서버에 등록 마치고 sharedpref에 저장까지 마친 메시지 last_id 업데이트
                        if(MyApplication.getInstance().getPrefManager().getInitGroupMessage(CR_ROOM_ID_)==0){ // InitGroupMsgID가 존재하지 않는 경우는 프로그램을 삭제했다가 다시 깐 경우, 혹은 채팅방을 나갔다가 다시 들어온 경우
                            MyApplication.getInstance().getPrefManager().updateInitGroupMessage(CR_ROOM_ID_,Integer.parseInt(obj.getString("fetched_last_msg"))+1);
                        }
                        MyApplication.getInstance().getPrefManager().updateSharedRoomLast(CR_ROOM_ID_);


                    } else {

                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "일시적 서버 오류 (2) 입니다. " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void fetchAllPrivateMsg() {

        String endPoint = EndPoints.CHAT_MYPRIVATE_IDS.replace("_ID_", MyApplication.getInstance().getPrefManager().getUser().getId());
        Log.e(TAG, "endPoint: " + endPoint);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        JSONArray commentsObj = obj.getJSONArray("senders");

                        for (int i = 0; i < commentsObj.length(); i++) {

                            JSONObject commentObj = (JSONObject) commentsObj.get(i);

                            String senderID = commentObj.getString("senderID");
                            fetchAllPrivateMsg2(senderID,String.valueOf(MyApplication.getInstance().getPrefManager().getLastPrivateMessage(senderID)));

                        }


                    } else {

                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "일시적 서버 오류 (3) 입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void fetchAllPrivateMsg2(String senderID_, String last_msg_id) {

        final String senderID=senderID_;

        String endPoint = EndPoints.FETCH_CHATPRIVATE_MSG.replace("_RID_",UserID).replace("_SID_", senderID).replace("_MID_",last_msg_id)
                .replace("_IID_",String.valueOf(MyApplication.getInstance().getPrefManager().getInitPrivateMessage(senderID)));
        Log.e(TAG, "endPoint: " + endPoint);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        JSONArray commentsObj = obj.getJSONArray("messages");

                        for (int i = 0; i < commentsObj.length(); i++) {

                            JSONObject commentObj = (JSONObject) commentsObj.get(i);



                            String commentId = commentObj.getString("message_id");
                            String commentText = commentObj.getString("message");
                            String createdAt = commentObj.getString("created_at");
                            String msgType=commentObj.getString("messagetype");

                            JSONObject userObj = commentObj.getJSONObject("user");
                            String userId = userObj.getString("user_id");
                            String senderID = userId;
                            String userName = userObj.getString("name");
                            String LinkUri=userObj.getString("LinkUri");
                            String profile_photo_path = userObj.getString("profile_photo_path");
                            boolean isOpen = Boolean.parseBoolean(userObj.getString("isOpen"));
                            User user = new User(userId, userName, LinkUri, profile_photo_path);
                            user.setisOpen(isOpen);
                            if(!MyApplication.getInstance().getPrefManager().checkTmpUser(senderID)){ // 기존 유저 정보가 존재하지 안으면
                                MyApplication.getInstance().getPrefManager().storeTmpUser(user); // 새로이 저장
                            }

                            Message message = new Message();
                            message.setId(commentId);
                            message.setMessage(commentText);
                            message.setCreatedAt(createdAt);
                            message.setUser(user);
                            message.setType(msgType);
                            if(userId != MyApplication.getInstance().getPrefManager().getUser().getId())
                            message.setIsRead(false);
                            else
                            message.setIsRead(true);

                            if(i==commentObj.length()-1) {
                                if (MyApplication.getInstance().getPrefManager().checkTmpUser(senderID)) {
                                    MyApplication.getInstance().getPrefManager().removeTmpUser(senderID);
                                }
                                MyApplication.getInstance().getPrefManager().storeTmpUser(user); // main에서 개인 톡방에 존재하는 유저에 대한 정보 업데이트
                            }
                            MyApplication.getInstance().getPrefManager().storePrivateMessage(senderID,message); //서버에 등록 마친 메시지 sharedpref에 저장




                        }
                        MyApplication.getInstance().getPrefManager().updateLastPrivateMessage(senderID,Integer.parseInt(obj.getString("last_msg_id"))); // 서버에 등록 마치고 sharedpref에 저장까지 마친 메시지 last_id 업데이트
                        if(MyApplication.getInstance().getPrefManager().getInitPrivateMessage(senderID)==0){ // InitPrivateMsgID가 존재하지 않는 경우는 프로그램을 삭제했다가 다시 깐 경우, 혹은 채팅방을 나갔다가 다시 들어온 경우
                            MyApplication.getInstance().getPrefManager().updateInitPrivateMessage(senderID,Integer.parseInt(obj.getString("fetched_last_msg"))+1);
                        }
                        MyApplication.getInstance().getPrefManager().updateSharedPrivateLast(senderID);

                    } else {

                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "일시적 서버 오류 (4) 입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }


}
