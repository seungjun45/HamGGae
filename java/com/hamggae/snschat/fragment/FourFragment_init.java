package com.hamggae.snschat.fragment;

/**
 * Created by seungjun on 2017-01-13.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hamggae.snschat.R;
import com.hamggae.snschat.activity.ChatRoomActivity;
import com.hamggae.snschat.activity.MainActivity;
import com.hamggae.snschat.adapter.CountryChatRoomsAdapter;
import com.hamggae.snschat.adapter.ProfileUserAdapter;
import com.hamggae.snschat.app.EndPoints;
import com.hamggae.snschat.app.MyApplication;
import com.hamggae.snschat.helper.SimpleDividerItemDecoration;
import com.hamggae.snschat.model.ChatRoom;
import com.hamggae.snschat.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class FourFragment_init extends Fragment{

    private FragmentActivity Activity_;
    private View Li;


    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private ArrayList<User> ProfileUserList;
    private ProfileUserAdapter mAdapter;
    private RecyclerView recyclerView;
    private TextView nickname;
    private ImageView profile_img;

    private Context mContext;
    private Toolbar toolbar;


    private static final String TAG = MainActivity.class.getSimpleName();




    private View profile_modify;

    private boolean togg3=true;

    public FourFragment_init() {
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
        Li= inflater.inflate(R.layout.fragmentfour_init, container, false);
        profile_modify= (View) Li.findViewById(R.id.profile_modify_layout);
        nickname = (TextView) Li.findViewById(R.id.input_layout_nickname);
        profile_img = (ImageView) Li.findViewById(R.id.profile_photo);

        recyclerView = (RecyclerView) Li.findViewById(R.id.recycler_profile);


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("나의 정보");


        ProfileUserList = new ArrayList<>();
        mAdapter = new ProfileUserAdapter(mContext, ProfileUserList, Activity_);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Activity_);
        Log.e(TAG, "before displaying recyclerView");
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                mContext
        ));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        profile_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment profile_modify = new FourFragment();
                FragmentTransaction transaction = Activity_.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.profile_modify_layout, profile_modify, "profile_modify");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        nickname.setText(MyApplication.getInstance().getPrefManager().getUser().getName());
        String Profile_Path_Server = MyApplication.getInstance().getPrefManager().getUser().getProfile_path();
        if(!(Profile_Path_Server.equals("default"))){
            Glide.with(mContext).load(EndPoints.BASE_URL+Profile_Path_Server)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .into(profile_img);
        }

        fetchProfileRequest();

        // Inflate the layout for this fragment
        return Li;
    }



    @Override
    public void onResume() {
        super.onResume();

        Activity_.invalidateOptionsMenu();
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    private void fetchProfileRequest() {

        String endPoint = EndPoints.FETCH_PROFILE.replace("_RID_", MyApplication.getInstance().getPrefManager().getUser().getId());
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
                        JSONArray request_sender = obj.getJSONArray("sender");
                        for (int i = 0; i < request_sender.length(); i++) {
                            JSONObject sender = (JSONObject) request_sender.get(i);
                            User ur = new User();
                            ur.setId(sender.getString("senderID"));
                            ur.setName(sender.getString("name"));
                            ur.setProfile_path(sender.getString("profile_photo_path"));
                            ur.setisOpen(sender.getBoolean("isOpen"));
                            ur.setLinkUri(sender.getString("LinkUri"));

                            ProfileUserList.add(ur);
                        }

                    } else {
                        // 프로필 요청이 존재하지 않는 경우
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

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            Fragment frg = null;
            frg = getFragmentManager().findFragmentByTag(getFragmentTag(3));
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(frg);
            ft.attach(frg);
            ft.commit();

        }
    }


    public String getFragmentTag(int pos){
        return "android:switcher:"+R.id.viewpager+":"+pos;
    }

}