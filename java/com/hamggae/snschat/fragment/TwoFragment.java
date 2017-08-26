package com.hamggae.snschat.fragment;

/**
 * Created by seungjun on 2017-01-13.
 */
import android.graphics.Rect;
import 	android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import com.google.firebase.messaging.FirebaseMessaging;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import com.hamggae.snschat.R;
import com.hamggae.snschat.activity.MainActivity;
import com.hamggae.snschat.app.Config;
import com.hamggae.snschat.helper.MyPreferenceManager;
import com.hamggae.snschat.model.CountryList;
import com.hamggae.snschat.util.NotificationUtils;
import com.hamggae.snschat.app.EndPoints;
import com.hamggae.snschat.app.MyApplication;
import com.hamggae.snschat.model.Country;

public class TwoFragment extends Fragment{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;



    private ArrayList<Country> CountryArrayList, CountryArrayList_Thumb;
    private ArrayList<String> CountryList_;
    private ArrayAdapter<String> Cadapter;
    private ListView listView;
    private View listViewLayout;
    private ImageView country_thumb[] = new ImageView[6];
    private TextView country_thumb_name[] = new TextView[6];
    private String Thumb_id[] = new String [6];
    private String Thumb_KOR_name[] = new String [6];
    private String Thumb_Eng_name[] = new String [6];
    private Map<String, List<String>> Dic_Con= new HashMap<String, List<String>>();
    private Map<String, List<String>> Dic_Thumb= new HashMap<String, List<String>>();
    // params.put("gcm_registration_id", token);

    private int loader,Country_Numbs;

    private FragmentActivity Activity_;
    private Context Context_;
    private View Li;
    private EditText CountrySearch;
    private CountryList countryList;
    private boolean tog1=false,tog2=false;


    private InputMethodManager imm;

    private static final String TAG = MainActivity.class.getSimpleName();

    public TwoFragment() {
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

        Activity_=getActivity();
        Context_=Activity_.getApplicationContext();

        // View setting here
        Li= inflater.inflate(R.layout.fragment_two, container, false);

        imm=(InputMethodManager) Activity_.getSystemService(Context.INPUT_METHOD_SERVICE);

        listView = (ListView) Li.findViewById(R.id.list_view);

        CountrySearch=(EditText) Li.findViewById(R.id.CountrySearch);

        country_thumb[0]= (ImageView) Li.findViewById(R.id.country_thumb1);
        country_thumb[1]= (ImageView) Li.findViewById(R.id.country_thumb2);
        country_thumb[2]= (ImageView) Li.findViewById(R.id.country_thumb3);
        country_thumb[3]= (ImageView) Li.findViewById(R.id.country_thumb4);
        country_thumb[4]= (ImageView) Li.findViewById(R.id.country_thumb5);
        country_thumb[5]= (ImageView) Li.findViewById(R.id.country_thumb6);

        country_thumb_name[0] = (TextView) Li.findViewById(R.id.country_thumb1_name);
        country_thumb_name[1] = (TextView) Li.findViewById(R.id.country_thumb2_name);
        country_thumb_name[2] = (TextView) Li.findViewById(R.id.country_thumb3_name);
        country_thumb_name[3] = (TextView) Li.findViewById(R.id.country_thumb4_name);
        country_thumb_name[4] = (TextView) Li.findViewById(R.id.country_thumb5_name);
        country_thumb_name[5] = (TextView) Li.findViewById(R.id.country_thumb6_name);


        // View handling from here

        loader=R.drawable.loader;

        listViewLayout= (View) Li.findViewById(R.id.list_view_layout);
        //ViewCompat.setElevation(listViewLayout, 4 * Context_.getResources().getDisplayMetrics().density);
        //ViewCompat.setAlpha(listViewLayout, 0.9f);
        listViewLayout.setVisibility(View.INVISIBLE);



        // ImageButton to close search list

        ImageButton closeButton = (ImageButton) Li.findViewById(R.id.close_search);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                listViewLayout.setVisibility(View.INVISIBLE);
                CountrySearch.clearFocus();
                imm.hideSoftInputFromWindow(CountrySearch.getWindowToken(), 0);

            }
        });

        // Edittext event handler

        CountrySearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    listViewLayout.setVisibility(View.VISIBLE);

                }
                else{
                    listViewLayout.setVisibility(View.INVISIBLE);
                    imm.hideSoftInputFromWindow(CountrySearch.getWindowToken(), 0);
                }
            }
        });


        //CountryArrayList = new ArrayList<>();
        if(MyApplication.getInstance().getPrefManager().getCountryList()==null) {
            if (savedInstanceState != null) {
                ArrayList<Map<String, List<String>>> list_receive = (ArrayList<Map<String, List<String>>>) savedInstanceState.getSerializable("Dic_Con");
                Dic_Con = list_receive.get(0);
                CountryList_ = savedInstanceState.getStringArrayList("CountryList_");
                Thumb_id = savedInstanceState.getStringArray("Thumb_id");
                Thumb_KOR_name = savedInstanceState.getStringArray("Thumb_KOR_name");
                Thumb_Eng_name = savedInstanceState.getStringArray("Thumb_Eng_name");
                Cadapter = new ArrayAdapter<String>(Activity_, R.layout.list_item, R.id.country_name, CountryList_);
                listView.setAdapter(Cadapter); // Cadapter is for listView, not for recyclerView

                Cadapter.notifyDataSetChanged();
                // subscribing to all chat room topics
                //subscribeToAllCountries();

                for (int i = 0; i < 6; i++) {
                    Glide.with(Context_).load(EndPoints.COUNTRY_THUMB_URL.replace("_COUNTRY_", Thumb_Eng_name[i] + "-min"))
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            //.bitmapTransform(new CropCircleTransformation(Context_))
                            .into(country_thumb[i]);
                    country_thumb_name[i].setText(Thumb_KOR_name[i]);
                }


            } else {
                CountryList_ = new ArrayList<>();
                Cadapter = new ArrayAdapter<String>(Activity_, R.layout.list_item, R.id.country_name, CountryList_);
                listView.setAdapter(Cadapter); // Cadapter is for listView, not for recyclerView
                if (checkPlayServices()) {
                    fetchCountries();
                    fetchSixCountries();
                }



            }
        }
        else{
            countryList= MyApplication.getInstance().getPrefManager().getCountryList();

            CountryList_=countryList.getCoutryList_KOR_StringList();
            Dic_Con=countryList.getDic_con();

            Cadapter = new ArrayAdapter<String>(Activity_, R.layout.list_item, R.id.country_name, CountryList_);
            listView.setAdapter(Cadapter); // Cadapter is for listView, not for recyclerView

            Cadapter.notifyDataSetChanged();

            if (checkPlayServices()) {

                fetchSixCountries();
            }

        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // when chat is clicked, launch full chat thread activity
                    String Country_INPUT= (String) parent.getItemAtPosition(position);
                    Fragment Fragment_countrychats = new Fragment_countrychats();
                    Bundle data = new Bundle();
                    List<String> Values_=Dic_Con.get(Country_INPUT);
                    updateCountryhits(Values_.get(0));
                    data.putString("CountryID", Values_.get(0));

                    data.putString("Eng_name", Values_.get(1));
                    data.putString("KOR_name", Country_INPUT); // 한글 검색, 영어 검색 변동시 이부분 변환

                    Fragment_countrychats.setArguments(data);
                    FragmentTransaction transaction = Activity_.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment2_layout, Fragment_countrychats, "Country_Frag");
                    transaction.addToBackStack(null);
                    imm.hideSoftInputFromWindow(CountrySearch.getWindowToken(), 0);
                    transaction.commit();

            }
        });

        CountrySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                TwoFragment.this.Cadapter.getFilter().filter(cs);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 6 country handler

        for(int i=0; i<6; i++){
            final int idx=i;

            country_thumb[i].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Fragment Fragment_countrychats = new Fragment_countrychats();
                    Bundle data = new Bundle();
                    updateCountryhits(Thumb_id[idx]);
                    data.putString("CountryID", Thumb_id[idx]);
                    data.putString("KOR_name", Thumb_KOR_name[idx]);
                    data.putString("Eng_name", Thumb_Eng_name[idx]);
                    Fragment_countrychats.setArguments(data);
                    FragmentTransaction transaction = Activity_.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment2_layout, Fragment_countrychats, "Country_Frag2");
                    transaction.addToBackStack(null);
                    imm.hideSoftInputFromWindow(CountrySearch.getWindowToken(), 0);
                    transaction.commit();
                }
            });
        }


        return Li;
    }

    private void fetchCountries() {

        String endPoint = EndPoints.Country_Names;
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
                        JSONArray CountryArray = obj.getJSONArray("countries");
                        for (int i = 0; i < CountryArray.length(); i++) {
                            JSONObject CountryObj = (JSONObject) CountryArray.get(i);
                            List<String> tmp_=new ArrayList<String>();


                            tmp_.add(CountryObj.getString("country_id"));
                            tmp_.add(CountryObj.getString("Eng_name"));
                            Dic_Con.put(CountryObj.getString("KOR_name"), tmp_);
                            CountryList_.add(CountryObj.getString("KOR_name")); // change this to getKORName for 한글로 검색
                        }
                        tog1=true;
                        if(tog1&&tog2) {
                            countryList = new CountryList(Thumb_id, Thumb_KOR_name, Thumb_Eng_name, CountryList_, Dic_Con);
                            MyApplication.getInstance().getPrefManager().storeCountryInfo(countryList);
                            tog2=false;
                            tog1=false;
                        }

                    } else {
                        // error in fetching chat rooms
                        Toast.makeText(Context_, "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(Context_, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                Cadapter.notifyDataSetChanged();

                // subscribing to all chat room topics
                unsubscribeToAllCountries();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(Context_, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);

    }


    private void fetchSixCountries() {

        String endPoint = EndPoints.Country_Six;
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
                        JSONArray CountryArray = obj.getJSONArray("countries");
                        for (int i = 0; i < 6; i++) {
                            JSONObject CountryObj = (JSONObject) CountryArray.get(i);

                            Thumb_id[i]=CountryObj.getString("country_id");
                            Thumb_Eng_name[i]=CountryObj.getString("Eng_name");
                            Thumb_KOR_name[i]=CountryObj.getString("KOR_name");

                            Glide.with(Context_).load(EndPoints.COUNTRY_THUMB_URL.replace("_COUNTRY_", Thumb_Eng_name[i]+"-min"))
                                    .thumbnail(0.5f)
                                    .crossFade()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    //.bitmapTransform(new CropCircleTransformation(Context_))
                                    .into(country_thumb[i]);
                            country_thumb_name[i].setText(Thumb_KOR_name[i]);

                        }
                        tog2=true;
                        if(tog1&&tog2) {
                            countryList = new CountryList(Thumb_id, Thumb_KOR_name, Thumb_Eng_name, CountryList_, Dic_Con);
                            MyApplication.getInstance().getPrefManager().storeCountryInfo(countryList);
                            tog2=false;
                            tog1=false;
                        }

                    } else {
                        // error in fetching chat rooms
                        Toast.makeText(Context_, "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(Context_, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                Cadapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(Context_, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);

    }



    private void unsubscribeToAllCountries() {
        for (String cr : CountryList_) {

            FirebaseMessaging.getInstance().unsubscribeFromTopic("Country_" + Dic_Con.get(cr).get(0));

        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(Activity_);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(Activity_, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(Context_, "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                Activity_.finish();
            }
            return false;
        }
        return true;
    }

    // Fetches reg id from shared preferences
    // and displays on the screen




    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void updateCountryhits(String countryID) {

        final String country_id= countryID;

        if (!(country_id == null)) {
            String endPoint = EndPoints.COUNTRY_HIT.replace("_ID_", country_id);

            Log.e(TAG, "endPoint" + endPoint);

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    endPoint, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "response: " + response);

                    try {
                        JSONObject obj = new JSONObject(response);

                        // check for error
                        if (obj.getBoolean("error") == false) {

                            String Messages_ = obj.getString("message");
                            //Toast.makeText(Context_, Messages_, Toast.LENGTH_LONG).show();


                        } else {
                            Toast.makeText(Context_, obj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "json parsing error: " + e.getMessage());
                        Toast.makeText(Context_, "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                    Toast.makeText(Context_, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    Log.e(TAG, "Params: " + params.toString());

                    return params;
                }


            };
            //Adding request to request queue
            MyApplication.getInstance().addToRequestQueue(strReq);
        }

    };

    @Override
    public void onSaveInstanceState(Bundle savedState) {

        super.onSaveInstanceState(savedState);

        // Note: getValues() is a method in your ArrayAdapter subclass

        savedState.putStringArrayList("CountryList_", CountryList_);
        ArrayList<Map<String,List<String>>> list_=new ArrayList<>();
        list_.add(Dic_Con);
        savedState.putSerializable("Dic_Con",list_);
        savedState.putStringArray("Thumb_id",Thumb_id);
        savedState.putStringArray("Thumb_KOR_name",Thumb_KOR_name);
        savedState.putStringArray("Thumb_Eng_name",Thumb_Eng_name);
    }
}