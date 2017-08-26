package com.hamggae.snschat.fragment;

/**
 * Created by seungjun on 2017-01-13.
 */


import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.ToggleButton;


import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.hamggae.snschat.R;
import com.hamggae.snschat.activity.MainActivity;
import com.hamggae.snschat.app.Config;
import com.hamggae.snschat.app.EndPoints;
import com.hamggae.snschat.app.MyApplication;
import com.hamggae.snschat.model.User;
import com.hamggae.snschat.other.AndroidMultiPartEntity;
import com.hamggae.snschat.util.NotificationUtils;

import org.apache.http.entity.mime.content.ByteArrayBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.hamggae.snschat.other.AndroidMultiPartEntity.ProgressListener;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class FourFragment extends Fragment{

    private FragmentActivity Activity_;
    private Context Context_;
    private View Li;
    private String UserID = null;
    private EditText Profile_Nickname;
    private ImageView Profile_photo, facebook;
    private RecyclerView friend_list;
    private ImageButton profile_edit_end, profile_complete;

    private String newNickname;
    private InputMethodManager imm;
    private static final int PICK_FROM_ALBUM=1;
    private Uri ImageUri;
    long totalSize = 0;
    private Bitmap bm;
    private String Profile_Path_Server;
    private Boolean gotUri;
    private ToggleButton toggleButton;
    private Boolean _isOpen_, isViewed_;

    private static final String TAG = MainActivity.class.getSimpleName();

    public FourFragment() {
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Trip talktalk");
        // Inflate the layout for this fragment

        UserID = MyApplication.getInstance().getPrefManager().getUser().getId();
        Profile_Path_Server = MyApplication.getInstance().getPrefManager().getUser().getProfile_path();
        Activity_=getActivity();
        Context_=Activity_.getApplicationContext();
        Li= inflater.inflate(R.layout.fragment_four, container, false);
        gotUri=false;


        // View settings...
        Profile_Nickname = (EditText) Li.findViewById(R.id.input_nickname);
        Profile_photo = (ImageView) Li.findViewById(R.id.profile_photo);
        facebook = (ImageView) Li.findViewById(R.id.facebook);
        friend_list = (RecyclerView) Li.findViewById(R.id.recycle_view_friends);
        profile_edit_end = (ImageButton) Li.findViewById(R.id.profile_edit_end);
        profile_complete=(ImageButton) Li.findViewById(R.id.profile_complete);
        toggleButton=(ToggleButton) Li.findViewById(R.id.toggle_profile);

        // View handler

        imm=(InputMethodManager) Activity_.getSystemService(Context.INPUT_METHOD_SERVICE);

        Profile_Nickname.setText(MyApplication.getInstance().getPrefManager().getUser().getName());
        if(!(Profile_Path_Server.equals("default"))){
            Glide.with(Context_).load(EndPoints.BASE_URL+Profile_Path_Server)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new CropCircleTransformation(Context_))
                    .into(Profile_photo);
        }

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(MyApplication.getInstance().getPrefManager().getUser().getLinkUri());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        profile_edit_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(profile_edit_end.getWindowToken(), 0);
                Profile_Nickname.clearFocus();
            }
        });

        Profile_photo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                doTakeAlbumAction();
            }
        });

        profile_complete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(profile_edit_end.getWindowToken(), 0);
                updateisOpen(String.valueOf(_isOpen_));
                Profile_Nickname.clearFocus();
                new UploadFileToServer().execute();


            }
        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                _isOpen_=isChecked;
                isViewed_=true;
                if(isViewed_) {
                    if (isChecked) {
                        Toast.makeText(Context_, "프로필 공개로 설정 되었습니다.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Context_, "프로필 비공개로 설정 되었습니다.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        _isOpen_=MyApplication.getInstance().getPrefManager().getUser().getisOpen();
        toggleButton.setChecked(_isOpen_);


        return Li;
    }

    public void updateProfile(String nickname_){
        final String nickName=nickname_;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.PROFILE_UPDATE, new Response.Listener<String>() {

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
                        MyApplication.getInstance().getPrefManager().clearUser();
                        MyApplication.getInstance().getPrefManager().storeUser(user);



                    } else {
                        // login error - simply toast the message
                        Toast.makeText(Context_, "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(Context_, "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Toast.makeText(Context_, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nickName", nickName);
                params.put("UserID",UserID);
                params.put("profile_photo_path",Profile_Path_Server);

                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    @Override
    public void onResume() {
        super.onResume();


    }


    @Override
    public void onPause() {
        super.onPause();

    }

    public void doTakeAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        try {
            ImageUri = data.getData();

            bm=MyApplication.getInstance().getBitmapReduced(Activity_.getContentResolver(), ImageUri);
            Glide.with(Context_).load(ImageUri)  // loading with ImageUri works fine, too.
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .bitmapTransform(new CropCircleTransformation(Context_))
                    .into(Profile_photo);
            gotUri=true;

        }catch(Exception e){

        }
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            Log.e(TAG, "Starting uploadFile ");
            if(!gotUri){
                return "Image not attached.";
            }

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(EndPoints.PROFILE_UPLOAD_URL);
            Log.e(TAG, "Right before going to try statement");
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                Log.e(TAG, "entity declared successfully");


                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 50, bao);
                bm.recycle();
                bm=null;
                byte [] ba = bao.toByteArray();
                String photoName = ImageUri.getPath().toString();
                entity.addPart("image", new ByteArrayBody(ba, photoName+".png"));


                // Adding file data to http body

                /*
                Log.e(TAG, "get real path from URI succeeded");
                File sourceFile = new File(ImageUri.getPath().toString());
                Log.e(TAG, "sourceFile create succeeded");
                entity.addPart("image", new FileBody(sourceFile));
                */

                Log.e(TAG, "attaching image to entity succeeded");

                // Extra parameters if you want to pass to server
                entity.addPart("userID",
                        new StringBody(UserID));

                totalSize = entity.getContentLength();

                Log.e(TAG, "Right before setting entity of httppost");

                httppost.setEntity(entity);

                // Making server call
                Log.e(TAG, "Right before sending request to the server");
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                Log.e(TAG, "Response from server received successfully");

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

            super.onPostExecute(result);
            if(gotUri) {
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getBoolean("error") == false) {
                        Profile_Path_Server = obj.getString("file_path");
                        newNickname = Profile_Nickname.getText().toString();
                        updateProfile(newNickname);
                        Toast.makeText(Context_, "프로필이 업데이트 되었습니다.", Toast.LENGTH_LONG).show();
                        gotUri=false;
                    } else {
                        Toast.makeText(Context_, obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Context_, "프로필 업데이트 실패.", Toast.LENGTH_LONG).show();
                }
            }
            else{ // 이미지는 바뀌지 않고 닉네임만 바뀌는 경우
                newNickname = Profile_Nickname.getText().toString();
                updateProfile(newNickname);
                Toast.makeText(Context_, "프로필이 업데이트 되었습니다.", Toast.LENGTH_LONG).show();
            }
            Activity_.finish();
            startActivity(Activity_.getIntent());

        }

    }
    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        isViewed_=visible;
    }
    public String getFragmentTag(int pos){
        return "android:switcher:"+R.id.viewpager+":"+pos;
    }

    public void updateisOpen(String isOpen){
        final String isOpen_=isOpen;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.UPDATE_ISOPEN_PROFILE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        // user successfully logged in


                    } else {
                        // login error - simply toast the message
                        Toast.makeText(Context_, "서버 오류입니다.", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(Context_, "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Toast.makeText(Context_, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userID",UserID);
                params.put("isOpen",isOpen_);

                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }
}