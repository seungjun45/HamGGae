package com.hamggae.snschat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.hamggae.snschat.R;
import com.hamggae.snschat.activity.MemoryActivity;
import com.hamggae.snschat.activity.ProfileActivity;
import com.hamggae.snschat.app.EndPoints;

import java.util.Arrays;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by seungjun on 2017-01-27.
 */

public class MapItemAdapter implements GoogleMap.InfoWindowAdapter {
    ImageView iv;
    TextView tv;
    private View InfoWin;
    private LayoutInflater inflater;
    private Context Context_;
    private Boolean toggle=false;
    private String previous_addr;

    public MapItemAdapter(Context context) {
        Context_=context;
        inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        InfoWin=inflater.inflate(R.layout.map_info_window, null);
        tv=(TextView) InfoWin.findViewById(R.id.infoWin_txt);
        iv=(ImageView) InfoWin.findViewById(R.id.infoWin_Img);
        previous_addr="default";
        /*
        tv = new TextView(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tv.setLayoutParams(lp);
        tv.setGravity(Gravity.CENTER);
        */
    }


    @Override
    public View getInfoWindow(Marker marker) {

        tv.setText(marker.getTitle());
        final Marker marker_=marker;
        if(!previous_addr.equals(marker.getSnippet())){
            toggle=false;
            if(marker.getSnippet()==null){
                previous_addr="default";
            }
            else {
                previous_addr = marker.getSnippet();
            }
        }
        if(marker.getSnippet()!=null && (!marker.getSnippet().equals("default")) ) {

            Glide.with(Context_).load(EndPoints.BASE_URL + marker.getSnippet())
                    .asBitmap()
                    .thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                            iv.setImageBitmap(bitmap);

                            if (marker_.isInfoWindowShown()) {
                                if(!toggle) {
                                    marker_.hideInfoWindow();
                                    toggle=true;
                                    marker_.showInfoWindow();

                                }
                            }

                        }
                    });
        }
        else{
            iv.setImageResource(R.mipmap.ic_picture);
        }


        return InfoWin;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}