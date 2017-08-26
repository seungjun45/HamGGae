package com.hamggae.snschat.adapter;

/**
 * Created by seungjun on 2016-12-23.
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hamggae.snschat.R;
import com.hamggae.snschat.activity.MainActivity;
import com.hamggae.snschat.activity.ProfileActivity;
import com.hamggae.snschat.app.EndPoints;
import com.hamggae.snschat.app.MyApplication;
import com.hamggae.snschat.model.ChatRoom;
import com.hamggae.snschat.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class ProfileUserAdapter extends RecyclerView.Adapter<ProfileUserAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<User> UserArrayList;
    private Activity Activity_;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView Profile_photo, accept_request, deny_request;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.Profile_nickname);
            Profile_photo = (ImageView) view.findViewById(R.id.profile_image);
            accept_request = (ImageView) view.findViewById(R.id.accept_request);
            deny_request = (ImageView) view.findViewById(R.id.deny_request);

        }
    }


    public ProfileUserAdapter(Context mContext, ArrayList<User> UserArrayList, Activity Activity_) {
        this.mContext = mContext;
        this.UserArrayList = UserArrayList;
        this.Activity_=Activity_;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_list_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        User request_sender = UserArrayList.get(position);
        holder.name.setText(request_sender.getName());

        if(!(request_sender.getProfile_path().equals("default"))) {
            Glide.with(mContext).load(EndPoints.BASE_URL + request_sender.getProfile_path())
                    .thumbnail(0.2f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .into(((ViewHolder) holder).Profile_photo);
        }

        // 프로필 이미지 사진 클릭시 이벤트

        final String userID, profile_path, userName;
        userID=request_sender.getId();
        profile_path=request_sender.getProfile_path();
        userName=request_sender.getName();
        final boolean isOpen = request_sender.getisOpen();
        ((ProfileUserAdapter.ViewHolder) holder).Profile_photo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_, ProfileActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("profile_path", profile_path);
                intent.putExtra("userName", userName);
                intent.putExtra("isOpen", isOpen);
                Activity_.startActivity(intent);
            }
        });

        final String receiverID = MyApplication.getInstance().getPrefManager().getUser().getId();
        final String senderID = userID;

        // 수락, 거절 버튼 클릭시 이벤트

        ((ViewHolder) holder).accept_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept_request(receiverID,senderID,position);
            }
        });

        ((ViewHolder) holder).deny_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deny_request(receiverID,senderID,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return UserArrayList.size();
    }



    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ProfileUserAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ProfileUserAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    private void accept_request(final String RID, final String SID, final int index_) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.ALLOW_PROFILE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        // user successfully logged in
                        Toast.makeText(mContext, "프로필 요청을 승낙하였습니다.", Toast.LENGTH_LONG).show();
                        UserArrayList.remove(index_);
                        ProfileUserAdapter.this.notifyDataSetChanged();

                    } else {
                        // login error - simply toast the message
                        Toast.makeText(mContext, "요청에 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(mContext, "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Toast.makeText(mContext, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("receiverID", RID);
                params.put("senderID", SID);

                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }


    private void deny_request(final String RID, final String SID, final int index_) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.DENY_PROFILE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        // user successfully logged in
                        Toast.makeText(mContext, "프로필 요청을 거절하였습니다.", Toast.LENGTH_LONG).show();
                        UserArrayList.remove(index_);
                        ProfileUserAdapter.this.notifyDataSetChanged();

                    } else {
                        // login error - simply toast the message
                        Toast.makeText(mContext, "요청에 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(mContext, "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Toast.makeText(mContext, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("receiverID", RID);
                params.put("senderID", SID);

                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }
}
