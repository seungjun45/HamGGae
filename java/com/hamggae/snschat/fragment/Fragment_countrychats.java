package com.hamggae.snschat.fragment;

/**
 * Created by seungjun on 2017-01-13.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Gravity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import android.os.Build;

import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


import com.hamggae.snschat.R;
import com.hamggae.snschat.activity.ChatRoomActivity;
import com.hamggae.snschat.activity.MainActivity;
import com.hamggae.snschat.app.Config;
import com.hamggae.snschat.util.NotificationUtils;
import com.hamggae.snschat.adapter.CountryChatRoomsAdapter;
import com.hamggae.snschat.app.EndPoints;
import com.hamggae.snschat.app.MyApplication;
import com.hamggae.snschat.helper.SimpleDividerItemDecoration;
import com.hamggae.snschat.model.ChatRoom;
import com.hamggae.snschat.model.Message;
import com.hamggae.snschat.model.User;
import com.hamggae.snschat.fragment.OneFragment;


public class Fragment_countrychats extends Fragment{

    private FragmentActivity Activity_;
    private View Li;



    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private ArrayList<ChatRoom> chatRoomArrayList;
    private CountryChatRoomsAdapter mAdapter;
    private RecyclerView recyclerView;

    private static final String TAG = MainActivity.class.getSimpleName();


    private Boolean register_ = false;

    private String CountryID;
    private String KOR_name;
    private String Eng_name;
    private String tmpId, tmpName;
    private Context mContext;
    private PopupWindow mPopupWindow, mPopupWindow_info;
    private Toolbar toolbar;

    private EditText inputName;
    private TextView _info, _title;
    private ImageButton btnCreate;
    private Bundle extras;

    private boolean togg3=true;

    public Fragment_countrychats() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Activity_=getActivity();
        mContext=Activity_.getApplicationContext();
        Li= inflater.inflate(R.layout.fragment_two_chatroomlist, container, false);

        recyclerView = (RecyclerView) Li.findViewById(R.id.recycler_view);

        extras=getArguments();
        CountryID=extras.getString("CountryID");
        KOR_name=extras.getString("KOR_name");
        Eng_name=extras.getString("Eng_name");


        // this.setTitle(KOR_name+" ("+Eng_name+")");

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(KOR_name);


        chatRoomArrayList = new ArrayList<>();
        mAdapter = new CountryChatRoomsAdapter(Activity_, chatRoomArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Activity_);
        Log.e(TAG, "before displaying recyclerView");
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                mContext
        ));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new CountryChatRoomsAdapter.RecyclerTouchListener(mContext, recyclerView, new CountryChatRoomsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
                ChatRoom chatRoom = chatRoomArrayList.get(position);

                tmpId=chatRoom.getId();
                tmpName=chatRoom.getName();
                JoinChatRoom(tmpId);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        ImageButton createroom = (ImageButton) Li.findViewById(R.id.ib_createroom);

        // Set a click listener for the Logout button
        createroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(togg3) {
                    togg3=false;
                    actionCreateChat();

                }
            }
        });

        /*
        ImageButton back = (ImageButton) Li.findViewById(R.id.ib_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Trip talktalk");
            }
        });
        */

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                toolbar.setNavigationIcon(null);
                fragmentManager.popBackStack();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Trip talktalk");
            }
        });

        if(checkPlayServices()){
            fetchCountryChatRooms();
        }

        // Inflate the layout for this fragment
        return Li;
    }

    private void JoinChatRoom(String RoomId) {

        final String RoomID= RoomId;

        if (!(CountryID == null)) {
            String endPoint = EndPoints.CHAT_JOIN;
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

                            String RoomName = obj.getString("name");
                            String last_msg_id=obj.getString("last_msg_id");
                            String RoomID=obj.getString("RoomID");
                            MyApplication.getInstance().getPrefManager().updateLastGroupMessage(RoomID,Integer.parseInt(last_msg_id)); // 방이 생성된 후 대화가 오가던 중간에 접속한 경우
                            MyApplication.getInstance().getPrefManager().updateInitGroupMessage(RoomID,Integer.parseInt(last_msg_id)+1);
                            // careful about +1 at init_msg_id
                            FirebaseMessaging.getInstance().subscribeToTopic("topic_" + RoomID);
                            Toast.makeText(mContext, "Succesfully joined Chatting Room : " + RoomName, Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(Activity_, ChatRoomActivity.class);
                            intent.putExtra("chat_room_id", tmpId);
                            intent.putExtra("name", tmpName);
                            startActivity(intent);

                        } else {
                            Toast.makeText(mContext, "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "json parsing error: " + e.getMessage());
                        Toast.makeText(mContext, "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                    Toast.makeText(mContext, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("UserID", MyApplication.getInstance().getPrefManager().getUser().getId());
                    params.put("RoomID", RoomID);

                    Log.e(TAG, "Params: " + params.toString());

                    return params;
                }


            };
            //Adding request to request queue
            MyApplication.getInstance().addToRequestQueue(strReq);
        }

    };



    private void fetchCountryChatRooms() {

        if(!(CountryID==null)) {
            String endPoint = EndPoints.Country_ROOMS.replace("_ID_", CountryID);
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

                                chatRoomArrayList.add(cr);
                            }

                        } else {
                            // error in fetching chat rooms
                            Toast.makeText(mContext, "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "json parsing error: " + e.getMessage());
                        Toast.makeText(mContext, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    mAdapter.notifyDataSetChanged();


                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                    Toast.makeText(mContext, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            //Adding request to request queue
            MyApplication.getInstance().addToRequestQueue(strReq);
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
                Toast.makeText(mContext, "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                Activity_.finish();
            }
            return false;
        }
        return true;
    }

    // Fetches reg id from shared preferences
    // and displays on the screen


    // 채팅방 메시지들 테이블 (간략 뷰) 업데이트



    @Override
    public void onResume() {
        super.onResume();

        Activity_.invalidateOptionsMenu();
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void actionCreateChat(){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View customView = inflater.inflate(R.layout.popup_layout,null);



        mPopupWindow = new PopupWindow(
                customView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
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
                togg3=true;
            }
        });

        mPopupWindow.setFocusable(true);
        mPopupWindow.update();

        mPopupWindow.showAtLocation(recyclerView, Gravity.CENTER,0,0);


        View contentView = mPopupWindow.getContentView();

        inputName = (EditText) contentView.findViewById(R.id.input_name);
        btnCreate = (ImageButton) contentView.findViewById(R.id.btn_create);

        inputName.addTextChangedListener(new MyTextWatcher());



        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(inputName.getText().toString().equals(""))){
                    CreateChatRoom(inputName.getText().toString(), "default");


                }
                else{
                    Toast.makeText(mContext, "채팅방 이름을 확인해 주세요." , Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

        }
        return super.onOptionsItemSelected(menuItem);
    }


    private class MyTextWatcher implements TextWatcher {


        private MyTextWatcher( ) {

        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {

        }
    }

    private void CreateChatRoom(String RoomName_, final String RoomInfo) {

        final String Roomname= RoomName_;
        final String info=RoomInfo;

        if (!(CountryID == null)) {
            String endPoint = EndPoints.CHAT_CREATE;
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

                            String RoomId_ = obj.getString("chat_room_id");

                            ChatRoom cr = new ChatRoom();
                            cr.setId(RoomId_);
                            cr.setName(obj.getString("RoomName"));
                            cr.setLastMessage("");
                            cr.setUnreadCount(0);
                            cr.setTimestamp("");
                            cr.setInfo(RoomInfo);
                            cr.setMemberCount("1");

                            MyApplication.getInstance().getPrefManager().updateInitGroupMessage(RoomId_,1); // 방 처음 생성시 메시지 없음. 라스트 메시지 없음, 초기 메시지 번호 1로 세팅 필요 (1부터 읽을 것)

                            chatRoomArrayList.add(cr);


                            Log.e(TAG, "ChatRoom ID : " + RoomId_);
                            Toast.makeText(mContext, "Succesfully Created Chatting Room." , Toast.LENGTH_LONG).show();


                            FirebaseMessaging.getInstance().subscribeToTopic("topic_" + RoomId_);





                        } else {
                            Toast.makeText(mContext, "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "json parsing error: " + e.getMessage());
                        Toast.makeText(mContext, "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    mAdapter.notifyDataSetChanged();

                    mPopupWindow.dismiss();
                    togg3=true;

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                    Toast.makeText(mContext, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("UserName", MyApplication.getInstance().getPrefManager().getUser().getName());
                    params.put("RoomName", Roomname);
                    params.put("CountryID", CountryID);
                    params.put("info", info);

                    Log.e(TAG, "Params: " + params.toString());

                    return params;
                }


            };

            //Adding request to request queue
            MyApplication.getInstance().addToRequestQueue(strReq);
        }

    };




    public String getFragmentTag(int pos){
        return "android:switcher:"+R.id.viewpager+":"+pos;
    }

}