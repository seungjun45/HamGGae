package com.hamggae.snschat.adapter;

/**
 * Created by seungjun on 2016-12-23.
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hamggae.snschat.R;
import com.hamggae.snschat.activity.MainActivity;
import com.hamggae.snschat.activity.ProfileActivity;
import com.hamggae.snschat.app.EndPoints;
import com.hamggae.snschat.app.MyApplication;
import com.hamggae.snschat.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class CloseUserAdapter extends RecyclerView.Adapter<CloseUserAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<User> UserArrayList;
    private Activity Activity_;
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    private BitmapDescriptor MemoryIcon, MemoryIcon_me;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView Profile_photo;
        public ToggleButton getMarkers;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.close_user_name);
            Profile_photo = (ImageView) view.findViewById(R.id.close_user_image);
            getMarkers = (ToggleButton) view.findViewById(R.id.get_markers);

        }
    }


    public CloseUserAdapter(Context mContext, ArrayList<User> UserArrayList, Activity Activity_, GoogleMap mMap) {
        this.mContext = mContext;
        this.UserArrayList = UserArrayList;
        this.Activity_=Activity_;
        this.mMap=mMap;
        this.MemoryIcon= BitmapDescriptorFactory.fromResource(R.drawable.ic_flag2);
        this.MemoryIcon_me=BitmapDescriptorFactory.fromResource(R.mipmap.red_flag);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.close_user_list_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
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
        ((CloseUserAdapter.ViewHolder) holder).Profile_photo.setOnClickListener(new View.OnClickListener(){
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

        final ArrayList<Marker> MarkerArray=new ArrayList<Marker>();
        ((CloseUserAdapter.ViewHolder) holder).getMarkers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isOpen) {
                    if (isChecked) {
                        Toast.makeText(mContext, "해당 사용자의 흔적을 불러오는 중...", Toast.LENGTH_LONG).show();
                        //
                        // 서버 리퀘스트 추가하기
                        getMemoryMarker(userID,MarkerArray);
                        //

                    } else {
                        Toast.makeText(mContext, "불러오기 취소.", Toast.LENGTH_LONG).show();
                        //
                        // ArrayList 삭제하기(초기화)
                        //
                        for(int idx=0; idx< MarkerArray.size(); idx++){
                            MarkerArray.get(idx).remove();
                        }
                        MarkerArray.clear();

                    }
                }
                else{
                    if(isChecked){
                        Toast.makeText(mContext, "해당 사용자에게 프로필 공개를 요청해보세요", Toast.LENGTH_LONG).show();
                        ((CloseUserAdapter.ViewHolder) holder).getMarkers.setChecked(false);
                    }
                }

            }
        });
        //((CloseUserAdapter.ViewHolder) holder).getMarkers.setChecked(false);


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
        private CloseUserAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final CloseUserAdapter.ClickListener clickListener) {
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

    private void getMemoryMarker(final String user_id, final ArrayList<Marker> MarkerArray) {

        String endPoint = EndPoints.MARKER_GET_MEMORY.replace("_UID_",user_id);
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
                        JSONArray markers = obj.getJSONArray("markers");
                        for (int i = 0; i < markers.length(); i++) {
                            JSONObject marker = (JSONObject) markers.get(i);
                            double latitude_=Double.parseDouble(marker.getString("latitude"));
                            double longitude_=Double.parseDouble(marker.getString("longitude"));
                            String user_name=marker.getString("user_name");
                            String type=marker.getString("type");
                            String marker_photo_path=marker.getString("marker_photo_path");
                            String marker_info = marker.getString("marker_info");
                            String marker_id=marker.getString("marker_id");
                            String marker_created_at=marker.getString("created_at");
                            Marker marker_tmp=mMap.addMarker(new MarkerOptions().position(new LatLng(latitude_, longitude_)).title(marker_info));
                            String[] tmp_string=new String[3];
                            tmp_string[0]=marker_id;
                            tmp_string[1]=marker_created_at;
                            tmp_string[2]=user_name;
                            marker_tmp.setTag(tmp_string);
                            marker_tmp.setSnippet(marker_photo_path);
                            if(type.equals("memory")){
                                if(user_id.equals(MyApplication.getInstance().getPrefManager().getUser().getId())){
                                    marker_tmp.setIcon(MemoryIcon_me);
                                }
                                else {
                                    marker_tmp.setIcon(MemoryIcon);
                                }
                            }
                            MarkerArray.add(marker_tmp); // initial location

                        }

                    } else {
                        Log.e(TAG, "server respond error");
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                }


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

    public void setmMap(GoogleMap mMap){
        this.mMap=mMap;
    }
}
