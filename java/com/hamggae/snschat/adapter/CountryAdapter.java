package com.hamggae.snschat.adapter;

/**
 * Created by seungjun on 2017-01-11.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.hamggae.snschat.R;
import com.hamggae.snschat.model.Country;


public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Country> CountryArrayList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView id, KOR_name, Eng_name;

        public ViewHolder(View view) {
            super(view);
            KOR_name = (TextView) view.findViewById(R.id._KOR_name);
            Eng_name = (TextView) view.findViewById(R.id._Eng_name);
        }
    }


    public CountryAdapter(Context mContext, ArrayList<Country> CountryArrayList) {
        this.mContext = mContext;
        this.CountryArrayList = CountryArrayList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.country_list_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Country country = CountryArrayList.get(position);

        holder.KOR_name.setText(country.getKorName());
        holder.Eng_name.setText(country.getEngName());

    }

    @Override
    public int getItemCount() {
        return CountryArrayList.size();
    }



    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private CountryAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final CountryAdapter.ClickListener clickListener) {
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
}
