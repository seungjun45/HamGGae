package com.hamggae.snschat.activity;

import android.content.Intent;
import android.graphics.Path;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hamggae.snschat.R;
import com.hamggae.snschat.app.EndPoints;
import com.hamggae.snschat.app.MyApplication;
import com.hamggae.snschat.model.Message;
import com.hamggae.snschat.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by seungjun on 2017-01-20.
 */

public class ProfileActivity extends AppCompatActivity {

    private ImageView profile_photo, facebook, kakao, instagram;
    private ImageButton profile_request, chat_request;
    private TextView nickName, profile_request_text;
    private String TAG = ProfileActivity.class.getSimpleName();
    private String OpponentID, userName, profile_path, ThisID;
    private boolean isOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        profile_request = (ImageButton) findViewById(R.id.act_profile_request);
        chat_request = (ImageButton) findViewById(R.id.act_profile_chat);

        profile_photo = (ImageView) findViewById(R.id.profile_activity_photo);
        facebook = (ImageView) findViewById(R.id.act_profile_facebook);
        kakao = (ImageView) findViewById(R.id.act_profile_kakao);
        instagram = (ImageView) findViewById(R.id.act_profile_insta);

        nickName = (TextView) findViewById(R.id.acitivity_profile_name);
        profile_request_text = (TextView) findViewById(R.id.act_profile_request_text);


        Intent intent = getIntent();
        OpponentID=intent.getStringExtra("userID");
        ThisID= MyApplication.getInstance().getPrefManager().getUser().getId();
        userName=intent.getStringExtra("userName");
        profile_path=intent.getStringExtra("profile_path");
        isOpen = intent.getBooleanExtra("isOpen",false);

        nickName.setText(userName);
        facebook.setVisibility(View.VISIBLE);
        kakao.setVisibility(View.VISIBLE);
        instagram.setVisibility(View.VISIBLE);
        profile_request.setVisibility(View.VISIBLE);
        nickName.setVisibility(View.VISIBLE);

        if(isOpen)
        {
            profile_request.setVisibility(View.INVISIBLE);
            profile_request_text.setVisibility(View.INVISIBLE);

            if(!(profile_path.equals("default"))){
                Glide.with(getApplicationContext()).load(EndPoints.BASE_URL+profile_path)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                        .into(profile_photo);
            }

            facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(MyApplication.getInstance().getPrefManager().getTmpUser(OpponentID).getLinkUri());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });



        }
        else{
            facebook.setVisibility(View.INVISIBLE);
            kakao.setVisibility(View.INVISIBLE);
            instagram.setVisibility(View.INVISIBLE);
        }
        chat_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ChatPrivateActivity.class);
                intent.putExtra("user_id", OpponentID);
                // ProfileActivity에서 ChatPrivateActivity로 넘어가거나, OneFragment에서 ChatPrivateActivity로 넘어갈때는 UserID만 넘겨줌.

                startActivity(intent);
            }
        });
        profile_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestProfile(OpponentID);
            }
        });



    }

    private void requestProfile(String Opponentid_) {
        final String Opponentid = Opponentid_;
        final String UserID_=ThisID;


        String endPoint = EndPoints.REQUEST_PROFILE;

        Log.e(TAG, "endpoint: " + endPoint);


        StringRequest strReq = new StringRequest(Request.Method.POST,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        Toast.makeText(getApplicationContext(), "프로필 요청이 전송되었습니다.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "서버 오류 : 잠시 후 다시 시도해주세요.", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("senderID", UserID_);
                params.put("receiverID", Opponentid);
                Log.e(TAG, "Params: " + params.toString());

                return params;
            };
        };


        // disabling retry policy so that it won't make
        // multiple http calls
        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        strReq.setRetryPolicy(policy);

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }
}
