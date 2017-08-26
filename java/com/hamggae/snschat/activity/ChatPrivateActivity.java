package com.hamggae.snschat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.games.video.Video;
import com.hamggae.snschat.R;
import com.hamggae.snschat.adapter.ChatRoomThreadAdapter;
import com.hamggae.snschat.app.Config;
import com.hamggae.snschat.app.EndPoints;
import com.hamggae.snschat.app.MyApplication;
import com.hamggae.snschat.model.Message;
import com.hamggae.snschat.model.User;
import com.hamggae.snschat.other.AndroidMultiPartEntity;
import com.hamggae.snschat.util.NotificationUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatPrivateActivity extends AppCompatActivity {

    private String TAG = ChatPrivateActivity.class.getSimpleName();
    private boolean toggle1,toggle2;


    private String UserID;
    private RecyclerView recyclerView;
    private ChatRoomThreadAdapter mAdapter;
    private ArrayList<Message> messageArrayList;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private EditText inputMessage;
    private Button btnSend;

    private View contentView, chat_options_layout;
    private ImageView chat_pics, chat_loc;
    private ToggleButton chat_options;
    private static final int PICK_PICTURE=1, PICK_VIDEO=2;
    private Uri ImageUri, VideoUri;
    private Boolean gotUri, isImage;
    private Bitmap bm;
    private Video vm;
    private long totalSize = 0;
    private String File_Path_Server;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        toggle1=false;
        toggle2=true;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        contentView=this.findViewById(android.R.id.content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputMessage = (EditText) findViewById(R.id.message);
        btnSend = (Button) findViewById(R.id.btn_send);

        chat_options = (ToggleButton) findViewById(R.id.chat_options);
        chat_pics = (ImageView) findViewById(R.id.chat_pics);
        chat_loc = (ImageView) findViewById(R.id.chat_loc);
        chat_options_layout = (View) findViewById(R.id.chat_options_layout);

        chat_options_layout.setVisibility(View.INVISIBLE);

        final Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_button);
        final Animation re_rotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.re_rotate_button);

        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.setFillAfter(true);
                //chat_options.setRotation(135);
                chat_options_layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        re_rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.setFillAfter(true);
                //chat_options.setRotation(135);
                chat_options_layout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        Intent intent = getIntent();
        UserID=intent.getStringExtra("user_id"); // 상대방 ID
        User user = MyApplication.getInstance().getPrefManager().getTmpUser(UserID);


        // fetching last_msg_id and init_msg_id from shared preferences
        final int SharedLastMsgID=MyApplication.getInstance().getPrefManager().getLastPrivateMessage(UserID);
        int SharedInitMsgID=MyApplication.getInstance().getPrefManager().getInitPrivateMessage(UserID);
        // 지금 여기서 SharedInitMsgID가 0일 경우는 개인 톡을 처음 시작할 경우(상대방이 처음 보낸 경우는 MainActivity에서 자동 업데이트)
        if(SharedInitMsgID==0){
            if(SharedLastMsgID==0){
                SharedInitMsgID=1;
            }else{
                Toast.makeText(this, "심각한 오류 (SharedInit 변수 오류)", Toast.LENGTH_SHORT).show();
            }
            MyApplication.getInstance().getPrefManager().updateInitPrivateMessage(UserID,SharedInitMsgID);
        }



        getSupportActionBar().setTitle(user.getName()); // 상대방 닉네임이 채팅방 이름으로
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (UserID == null) {
            Toast.makeText(getApplicationContext(), "사용자 정보 오류(1)", Toast.LENGTH_SHORT).show();
            finish();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        messageArrayList = new ArrayList<>();

        // self user id is to identify the message owner
        String selfUserId = MyApplication.getInstance().getPrefManager().getUser().getId();

        mAdapter = new ChatRoomThreadAdapter(this, messageArrayList, selfUserId, this.getApplicationContext(), this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION_PRIVATE)) {
                    // new push message is received
                    handlePushNotification(intent); // 채팅방 안에서 개인 채팅 핸들러
                }
                else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)){
                    Message message = (Message) intent.getSerializableExtra("message");
                    String chatRoomId = intent.getStringExtra("chat_room_id");
                    String chatRoomName = intent.getStringExtra("chat_room_name");
                    Toast.makeText(getApplicationContext(), chatRoomName+"\n"+message.getUser().getName() + ": " + message.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        };


        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                contentView.getWindowVisibleDisplayFrame(r);
                int screenHeight = contentView.getRootView().getHeight();



                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                Log.d(TAG, "keypadHeight = " + keypadHeight);

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    Log.d(TAG, "keypad opened");
                    if(toggle1) {
                        if (mAdapter.getItemCount() > 1) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }
                        toggle2 = true;
                        toggle1=false;
                    }
                }
                else {
                    // keyboard is closed
                    Log.d(TAG, "keypad closed");
                    if(toggle2) {
                        if (mAdapter.getItemCount() > 1) {
                             recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }
                        toggle1=true;
                        toggle2=false;
                    }
                }
            }
        });



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPrivateMessage("text", inputMessage.getText().toString().trim());
            }
        });

        chat_options.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    chat_options.startAnimation(rotate);
                }
                else{
                    chat_options.startAnimation(re_rotate);
                }
            }
        });

        chat_pics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTakePICTURE();
            }
        });

        fetchPrivateChatThread(UserID,SharedLastMsgID,SharedInitMsgID); // fetching from sharedpreference
    }

    @Override
    protected void onResume() {
        super.onResume();

        // registering the receiver for new notification
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION_PRIVATE));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Handling new push message, will add the message to
     * recycler view and scroll it to bottom
     * */
    private void handlePushNotification(Intent intent) {
        Message message = (Message) intent.getSerializableExtra("message");
        String user_id = intent.getStringExtra("user_id");


        if (message != null && user_id != null) {
            MyApplication.getInstance().getPrefManager().storePrivateMessage(user_id,message);
            MyApplication.getInstance().getPrefManager().updateLastPrivateMessage(user_id,Integer.parseInt(message.getId()));
            messageArrayList.add(MyApplication.getInstance().getPrefManager().getPrivateMessage(user_id,message.getId())); // on new message received
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() > 1) {
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
            }
        }
    }

    /**
     * Posting a new message in chat room
     * will make an http call to our server. Our server again sends the message
     * to all the devices as push notification
     * */
    private void sendPrivateMessage(String msgType, final String message) {

        final String msgType_=msgType;

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        String endPoint = EndPoints.PRIVATE_MESSAGE.replace("_ID_", UserID);

        Log.e(TAG, "endpoint: " + endPoint);

        this.inputMessage.setText("");

        StringRequest strReq = new StringRequest(Request.Method.POST,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        JSONObject commentObj = obj.getJSONObject("message");

                        String commentId = commentObj.getString("message_id");
                        String commentText = commentObj.getString("message");
                        String createdAt = commentObj.getString("created_at");
                        String msgtype=commentObj.getString("messagetype");

                        JSONObject userObj = obj.getJSONObject("user");
                        String userId = userObj.getString("user_id");
                        //상관 없는 정보 서버에서 보내지는거 다 삭제(user id제외한 부분 sedmessage에서 삭제하기
                        boolean isOpen = false; // 자기 자신의 프로필이니 false든 true든 상관 없음.(어차피 채팅방 안에서 자기자신 프로필 확인은 불가능)
                        User user = new User();
                        user=MyApplication.getInstance().getPrefManager().getUser(); // 자기자신 프로필 정보는 sharedpref에서 직접 fetching
                        user.setisOpen(isOpen);

                        Message message = new Message();
                        message.setId(commentId);
                        message.setMessage(commentText);
                        message.setCreatedAt(createdAt);
                        message.setUser(user);
                        message.setType(msgtype);
                        message.setIsRead(true);

                        if(MyApplication.getInstance().getPrefManager().getInitPrivateMessage(UserID)==0){
                            MyApplication.getInstance().getPrefManager().updateInitPrivateMessage(UserID,Integer.parseInt(commentId));
                        }

                        MyApplication.getInstance().getPrefManager().storePrivateMessage(UserID,message); //서버에 등록 마친 메시지 sharedpref에 저장
                        MyApplication.getInstance().getPrefManager().updateLastPrivateMessage(UserID,Integer.parseInt(commentId)); // 서버에 등록 마치고 sharedpref에 저장까지 마친 메시지 last_id 업데이트
                        MyApplication.getInstance().getPrefManager().updateSharedPrivateLast(UserID);
                        if(MyApplication.getInstance().getPrefManager().getInitPrivateMessage(UserID)==0){
                            MyApplication.getInstance().getPrefManager().updateInitPrivateMessage(UserID,Integer.parseInt(commentId));//처음 보는 메시지라면 내가 보내는 첫 메시지가 init_msg가 되는게 맞음
                        }

                        messageArrayList.add(message); // the message that I sent

                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            // scrolling to bottom of the recycler view
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "" + obj.getString("message"), Toast.LENGTH_LONG).show();
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
                inputMessage.setText(message);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", MyApplication.getInstance().getPrefManager().getUser().getId());
                params.put("message", message);
                params.put("msgType",msgType_);
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



    private void fetchPrivateChatThread(String User_id, int last_msg_id, int init_msg_id){
        for(int i=Math.max(init_msg_id,last_msg_id-200); i<=last_msg_id; i++){

                Message msg = new Message();
                msg = MyApplication.getInstance().getPrefManager().getPrivateMessage(User_id, String.valueOf(i));
                messageArrayList.add(msg);



        }
        mAdapter.notifyDataSetChanged();
        if(mAdapter.getItemCount()>1){
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
        }
    }

    public void doTakePICTURE(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,PICK_PICTURE);
    }

    public void doTakeVIDEO(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Video.Media.CONTENT_TYPE);
        startActivityForResult(intent,PICK_VIDEO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        try {
            if(requestCode==PICK_PICTURE){
                ImageUri = data.getData();

                bm=MyApplication.getInstance().getBitmapReduced(this.getContentResolver(), ImageUri);
                gotUri=true;
                isImage=true;
            }
            else{
                VideoUri = data.getData();
                gotUri=true;
                isImage=false;
            }
            new UploadFileToServer().execute();


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
            HttpPost httppost = new HttpPost(EndPoints.ROOM_UPLOAD_URL);
            Log.e(TAG, "Right before going to try statement");
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                Log.e(TAG, "entity declared successfully");

                if(isImage) {

                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 80, bao);
                    bm.recycle();
                    bm=null;
                    byte[] ba = bao.toByteArray();
                    String photoName = ImageUri.getPath().toString();
                    entity.addPart("file", new ByteArrayBody(ba, photoName + ".png"));

                }
                else{


                    // Adding file data to http body


                    Log.e(TAG, "get real path from URI succeeded");
                    File sourceFile = new File(VideoUri.getPath().toString());
                    Log.e(TAG, "sourceFile create succeeded");
                    entity.addPart("file", new FileBody(sourceFile));
                }


                Log.e(TAG, "attaching image to entity succeeded");

                // Extra parameters if you want to pass to server
                entity.addPart("userID",
                        new StringBody(UserID)); // UserID = 상대방 id

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
                        File_Path_Server = obj.getString("file_path");
                        sendPrivateMessage("image",File_Path_Server);
                        Toast.makeText(getApplicationContext(), "파일이 전송 되었습니다.", Toast.LENGTH_LONG).show();
                        gotUri=false;
                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "파일 업로드 실패.", Toast.LENGTH_LONG).show();
                }
            }
            else{ // 이미지는 바뀌지 않고 닉네임만 바뀌는 경우
                Toast.makeText(getApplicationContext(), "다시 시도해 주세요.", Toast.LENGTH_LONG).show();
            }

        }

    }


}
