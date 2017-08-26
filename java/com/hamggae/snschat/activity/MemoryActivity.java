package com.hamggae.snschat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hamggae.snschat.R;
import com.hamggae.snschat.app.EndPoints;
import com.hamggae.snschat.app.MyApplication;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by seungjun on 2017-02-10.
 */

public class MemoryActivity extends AppCompatActivity {
    private ImageView marker_photo;
    private TextView user_name, marker_info, marker_created_at;
    private String userName, markerInfo, markerCreated_at, markerPhoto, markerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_memory);

        marker_photo = (ImageView) findViewById(R.id.memory_pic);
        user_name=(TextView) findViewById(R.id.memory_user);
        marker_info=(TextView) findViewById(R.id.memory_info);
        marker_created_at=(TextView) findViewById(R.id.memory_created_at);

        Intent intent = getIntent();
        userName=intent.getStringExtra("user_name");
        markerInfo=intent.getStringExtra("marker_info");
        markerCreated_at=intent.getStringExtra("created_at");
        markerPhoto = intent.getStringExtra("marker_photo_path");
        markerID=intent.getStringExtra("marker_id");

        Glide.with(this).load(EndPoints.BASE_URL + markerPhoto)
                .asBitmap()
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(marker_photo);

        user_name.setText(userName);
        marker_info.setText(markerInfo);
        marker_created_at.setText(markerCreated_at);
    }

}
