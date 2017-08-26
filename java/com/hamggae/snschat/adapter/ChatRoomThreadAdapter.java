package com.hamggae.snschat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hamggae.snschat.R;
import com.hamggae.snschat.activity.ChatRoomActivity;
import com.hamggae.snschat.activity.ProfileActivity;
import com.hamggae.snschat.app.EndPoints;
import com.hamggae.snschat.model.Message;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ChatRoomThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String TAG = ChatRoomThreadAdapter.class.getSimpleName();

    private String userId;
    private int SELF = 100, SELF_IMG= 200, SELF_LOC = 300, OTHER= 150, OTHER_IMG=250, OTHER_LOC=350;
    private static String today;

    private Context mContext;
    private ArrayList<Message> messageArrayList;
    private Context Context_;
    private Activity Activity_;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, timestamp;
        ImageView profile_other, msg_pic;

        public ViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            profile_other = (ImageView) itemView.findViewById(R.id.profile_image_other);
            msg_pic = (ImageView) itemView.findViewById(R.id.msg_img);

        }
    }


    public ChatRoomThreadAdapter(Context mContext, ArrayList<Message> messageArrayList, String userId, Context Context_, Activity Activity_) {
        this.mContext = mContext;
        this.messageArrayList = messageArrayList;
        this.userId = userId;
        this.Context_=Context_;
        this.Activity_=Activity_;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        // view type is to identify where to render the chat message
        // left or right
        if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self, parent, false);
        } else if(viewType == OTHER) {
            // others message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_other, parent, false);
        } else if(viewType == SELF_IMG) {
            // others message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self_img, parent, false);
        } else{
        //} else if(viewType == OTHER_IMG) {
            // others message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_other_img, parent, false);
        }


        return new ViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        if (message.getUser().getId().equals(userId)) {
            if(message.getType().equals("text"))
                return SELF;
            else if(message.getType().equals("image"))
                return SELF_IMG;
            else if(message.getType().equals("location"))
                return SELF_LOC;
        }
        else{
            if(message.getType().equals("text"))
                return OTHER;
            else if(message.getType().equals("image"))
                return OTHER_IMG;
            else if(message.getType().equals("location"))
                return OTHER_LOC;
        }


        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);
        if(message.getType().equals("text")) {
            ((ViewHolder) holder).message.setText(message.getMessage());
        }
        else if(message.getType().equals("image")){
            Glide.with(Context_).load(EndPoints.BASE_URL + message.getMessage())
                    .thumbnail(0.4f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(((ViewHolder) holder).msg_pic);
        }
        else if(message.getType().equals("location")){

        }

        String timestamp = getTimeStamp(message.getCreatedAt());

        if (message.getUser().getName() != null)
            timestamp = message.getUser().getName() + ", " + timestamp;

        ((ViewHolder) holder).timestamp.setText(timestamp);
        if (!(message.getUser().getId().equals(userId))) {
            if(!(message.getUser().getProfile_path().equals("default"))) {
                Glide.with(Context_).load(EndPoints.BASE_URL + message.getUser().getProfile_path())
                        .thumbnail(0.2f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .bitmapTransform(new CropCircleTransformation(Context_))
                        .into(((ViewHolder) holder).profile_other);
            }
            final String userID, profile_path, userName;
            userID=message.getUser().getId();
            profile_path=message.getUser().getProfile_path();
            userName=message.getUser().getName();
            final boolean isOpen = message.getUser().getisOpen();
            ((ViewHolder) holder).profile_other.setOnClickListener(new View.OnClickListener(){
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
        }


    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }
}

