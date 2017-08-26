package com.hamggae.snschat.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import com.hamggae.snschat.activity.ChatPrivateActivity;
import com.hamggae.snschat.model.Message;
import com.hamggae.snschat.model.User;
import com.hamggae.snschat.activity.ChatRoomActivity;
import com.hamggae.snschat.activity.MainActivity;
import com.hamggae.snschat.app.Config;
import com.hamggae.snschat.app.MyApplication;
import com.hamggae.snschat.util.NotificationUtils;


/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        if(MyApplication.getInstance().getPrefManager().getUser()==null){
            // user is not logged in, skipping push notification
            Log.e(TAG, "user is not logged in, skipping push notification");
            return;
        }

        if(remoteMessage.getFrom().startsWith("/topics/")){
            // message received from some topic.
        } else{
            // normal downstream message.
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().get("data").toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().get("data").toString());
                Boolean is_background = Boolean.parseBoolean(remoteMessage.getData().get("is_background").toString());
                String title = remoteMessage.getData().get("title").toString();
                String flag = remoteMessage.getData().get("flag").toString();

                switch (Integer.parseInt(flag)){
                    case Config.PUSH_TYPE_CHATROOM:
                        processChatRoomPush(json, is_background, title);
                        break;
                    case Config.PUSH_TYPE_USER:
                        processUserMessage(json, is_background, title);
                        break;
                    case Config.PUSH_TYPE_PROFILE:
                        break;
                    case Config.PUSH_TYPE_MEMBERCOUNT:
                        break;
                    case Config.PUSH_TYPE_COUNTRYCHAT:
                        break;
                }

                // handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void processChatRoomPush(JSONObject datObj, boolean isBackground, String title) {
        Log.e(TAG, "push json: " + datObj.toString());

            if (!isBackground) {

                try {
                    //JSONObject datObj = new JSONObject(json);

                    String chatRoomId = datObj.getString("chat_room_id");
                    String chatRoomName = datObj.getString("chat_room_name");

                    JSONObject mObj = datObj.getJSONObject("message");
                    Message message = new Message();
                    message.setMessage(mObj.getString("message"));
                    message.setId(mObj.getString("message_id"));
                    message.setCreatedAt(mObj.getString("created_at"));
                    message.setType(mObj.getString("messagetype"));
                    message.setIsRead(false);
                    message.setSelf(false);

                    JSONObject uObj = datObj.getJSONObject("user");

                    // skip the message if the message belongs to same user as
                    // the user would be having the same message when he was sending
                    // but it might differs in your scenario
                    if (uObj.getString("user_id").equals(MyApplication.getInstance().getPrefManager().getUser().getId())) {
                        Log.e(TAG, "Skipping the push message as it belongs to same user");
                        message.setSelf(true);
                        message.setIsRead(true);
                        return;
                    }

                    User user = new User();
                    user.setId(uObj.getString("user_id"));
                    user.setLinkUri(uObj.getString("LinkUri"));
                    user.setName(uObj.getString("name"));
                    user.setProfile_path(uObj.getString("profile_photo_path"));
                    user.setisOpen(Boolean.parseBoolean(uObj.getString("isOpen")));
                    if(!MyApplication.getInstance().getPrefManager().checkTmpUser(user.getId())){ // firebase notification 도착시 해당 user 정보가 UID_LIST에 없으면 최초 tmp_user 작성
                        MyApplication.getInstance().getPrefManager().storeTmpUser(user);
                    }
                    message.setUser(user);
                    MyApplication.getInstance().getPrefManager().storeGroupMessage(message,chatRoomId);
                    MyApplication.getInstance().getPrefManager().updateLastGroupMessage(chatRoomId,Integer.parseInt(message.getId()));
                    MyApplication.getInstance().getPrefManager().updateSharedRoomLast(chatRoomId);

                    // verifying whether the app is in background or foreground
                    if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                        // app is in foreground, broadcast the push message
                        Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                        pushNotification.putExtra("type", Config.PUSH_TYPE_CHATROOM);
                        pushNotification.putExtra("message", message);
                        pushNotification.putExtra("chat_room_id", chatRoomId);
                        pushNotification.putExtra("chat_room_name",chatRoomName);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                        // play notification sound
                        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                        notificationUtils.playNotificationSound();
                    } else {

                        // app is in background. show the message in notification try
                        Intent resultIntent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                        resultIntent.putExtra("chat_room_id", chatRoomId);
                        showNotificationMessage(getApplicationContext(), title, user.getName() + " : " + message.getMessage(), message.getCreatedAt(), resultIntent);
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            } else {
                // the push notification is silent, may be other operations needed
                // like inserting it in to SQLite
            }




    }

    /**
     * Processing user specific push message
     * It will be displayed with / without image in push notification tray
     * */
    private void processUserMessage(JSONObject datObj, boolean isBackground, String title) {
        Log.e(TAG, "push json: " + datObj.toString());

            if (!isBackground) {

                try {


                    String imageUrl = datObj.getString("image");

                    JSONObject mObj = datObj.getJSONObject("message");
                    Message message = new Message();
                    message.setMessage(mObj.getString("message"));
                    message.setId(mObj.getString("message_id"));
                    message.setCreatedAt(mObj.getString("created_at"));
                    message.setType(mObj.getString("messagetype"));
                    message.setIsRead(false);
                    message.setSelf(false);

                    JSONObject uObj = datObj.getJSONObject("user");
                    String user_id = uObj.getString("user_id");
                    String user_name = uObj.getString("name");
                    User user = new User();
                    user.setId(uObj.getString("user_id"));
                    user.setLinkUri(uObj.getString("LinkUri"));
                    user.setName(uObj.getString("name"));
                    user.setProfile_path(uObj.getString("profile_photo_path"));
                    user.setisOpen(Boolean.parseBoolean(uObj.getString("isOpen")));
                    if(!MyApplication.getInstance().getPrefManager().checkTmpUser(user.getId())){ // firebase notification 도착시 해당 user 정보가 UID_LIST에 없으면 최초 tmp_user 작성
                        MyApplication.getInstance().getPrefManager().storeTmpUser(user);
                    }
                    message.setUser(user);
                    MyApplication.getInstance().getPrefManager().storePrivateMessage(user.getId(),message);
                    MyApplication.getInstance().getPrefManager().updateLastPrivateMessage(user.getId(),Integer.parseInt(message.getId()));
                    MyApplication.getInstance().getPrefManager().updateSharedPrivateLast(user.getId());

                    // verifying whether the app is in background or foreground
                    if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                        // app is in foreground, broadcast the push message
                        Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION_PRIVATE);
                        pushNotification.putExtra("type", Config.PUSH_TYPE_USER);
                        pushNotification.putExtra("message", message);
                        pushNotification.putExtra("user_id", user_id);
                        pushNotification.putExtra("user_name", user_name);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                        // play notification sound
                        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                        notificationUtils.playNotificationSound();
                    } else {

                        // app is in background. show the message in notification try
                        //Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                        Intent resultIntent = new Intent(getApplicationContext(), ChatPrivateActivity.class);
                        resultIntent.putExtra("user_id", user_id);

                        // check for push notification image attachment
                        if (TextUtils.isEmpty(imageUrl)) {
                            showNotificationMessage(getApplicationContext(), title, message.getMessage(), message.getCreatedAt(), resultIntent);
                        } else {
                            // push notification contains image
                            // show it with the image
                            showNotificationMessageWithBigImage(getApplicationContext(), title, message.getMessage(), message.getCreatedAt(), resultIntent, imageUrl);
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            } else {
                // the push notification is silent, may be other operations needed
                // like inserting it in to SQLite
            }



    }


    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION_FIREBASE);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    //we don't use handleDataMessage

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
