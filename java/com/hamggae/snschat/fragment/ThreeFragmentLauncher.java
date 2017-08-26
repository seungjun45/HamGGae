package com.hamggae.snschat.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hamggae.snschat.R;
import com.hamggae.snschat.activity.LoginActivity;
import com.hamggae.snschat.activity.SplashActivity;

/**
 * Created by seungjun on 2017-01-22.
 */

public class ThreeFragmentLauncher extends Fragment {

    private FragmentActivity Activity_;
    private Context Context_;
    private View Li;

    private static int SPLASH_TIME_OUT = 1300;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Trip talktalk");
        Activity_=getActivity();
        Context_=Activity_.getApplicationContext();
        Li= inflater.inflate(R.layout.fragment_three_launcher, container, false);
        /*
        Li.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment threeFragment = new ThreeFragment();
                FragmentTransaction transaction = Activity_.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment3_launcher, threeFragment, "GoogleMap");
                transaction.commit();
            }
        });
        */

        return Li;
    }

    /*
    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {


                    Fragment threeFragment = new ThreeFragment();
                    FragmentTransaction transaction = Activity_.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment3_launcher, threeFragment, "GoogleMap");
                    transaction.commit();

                }
            }, SPLASH_TIME_OUT);
        }
    }
    */
}
