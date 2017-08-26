package com.hamggae.snschat.fragment;

/**
 * Created by seungjun on 2017-01-13.
 */
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.widget.ToggleButton;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.signature.StringSignature;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hamggae.snschat.R;
import com.hamggae.snschat.activity.MainActivity;
import com.hamggae.snschat.activity.MemoryActivity;
import com.hamggae.snschat.adapter.CloseUserAdapter;
import com.hamggae.snschat.adapter.MapItemAdapter;
import com.hamggae.snschat.adapter.ProfileUserAdapter;
import com.hamggae.snschat.app.EndPoints;
import com.hamggae.snschat.app.MyApplication;
import com.hamggae.snschat.helper.SimpleDividerItemDecoration;
import com.hamggae.snschat.model.CountryList;
import com.hamggae.snschat.model.User;
import com.hamggae.snschat.other.AndroidMultiPartEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.R.attr.bitmap;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class ThreeFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private GoogleMap mMap;
    private FragmentActivity Activity_;
    private Context Context_;
    private View Li, Ma_P, Close_Layout;
    private CoordinatorLayout CoordinateView_Layout;
    private RecyclerView recyclerView;
    //private RelativeLayout Ma_P;
    private ImageView Ma_P_photo, memory_pic;
    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private TextView lblLocation, searchViewTxt;
    private Button btnLeaveMemory;
    private ToggleButton toggleButton, btnLocalSearch, searchView;
    private static final int REQUEST_CODE_LOCATION = 2;
    private boolean toggle_loc_change=false;

    private double latitude;
    private double longitude;
    private String marker_notice;
    private ArrayList<Marker> MarkerArray;
    private boolean toggle=false;
    private int keep_height;
    private Boolean gotUri;

    private Uri ImageUri;
    private Bitmap bm;
    private BitmapDescriptor MemoryIcon, MemoryIcon_me;

    private Circle circle;

    private boolean setCircleOn= false;

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;

    private ArrayList<User> UserArrayList;
    private CloseUserAdapter mAdapter;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    private static final int PICK_FROM_ALBUM=1;

    private SupportMapFragment mapFragment;

    private PopupWindow mPopupWindow;
    private double final_lat, final_long;
    private String marker_info_;

    public ThreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mLocationRequest=LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        MarkerArray=new ArrayList<>();
        marker_notice="저 여기 있어요!";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // First, everything starts after View assigning!!

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Trip talktalk");
        Activity_=getActivity();
        Context_=Activity_.getApplicationContext();
        Li= inflater.inflate(R.layout.fragment_three, container, false);
        lblLocation = (TextView) Li.findViewById(R.id.lblLocation);
        searchViewTxt=(TextView) Li.findViewById(R.id.search_radius_txt);
        btnLeaveMemory = (Button) Li.findViewById(R.id.btnLeaveMemory);
        btnLocalSearch = (ToggleButton) Li.findViewById(R.id.LocalSearch);
        toggleButton = (ToggleButton) Li.findViewById(R.id.toggBtn);
        searchView = (ToggleButton) Li.findViewById(R.id.search_view_icon);
        Close_Layout = (View) Li.findViewById(R.id.close_layout);
        CoordinateView_Layout= (CoordinatorLayout) Li.findViewById(R.id.recycler_view_layout);
        MemoryIcon=BitmapDescriptorFactory.fromResource(R.drawable.ic_flag2);
        MemoryIcon_me=BitmapDescriptorFactory.fromResource(R.mipmap.red_flag);

        final Animation bottomUp = AnimationUtils.loadAnimation(Context_,R.anim.bottom_up);
        final Animation bottomDown = AnimationUtils.loadAnimation(Context_,R.anim.bottom_down);




        // Then we start operations!



        Close_Layout.setVisibility(View.INVISIBLE);

        UserArrayList = new ArrayList<User>();
        recyclerView = (RecyclerView) Li.findViewById(R.id.recycler_close);
        mAdapter = new CloseUserAdapter(Context_, UserArrayList, Activity_, mMap);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Activity_);
        Log.e(TAG, "before displaying recyclerView");
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                Context_
        ));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        // Building the GoogleApi client
        buildGoogleApiClient();

        // Toggle handler (Bottom code)
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if (mLastLocation != null) {
                        Toast.makeText(Context_, "GPS작동을 준비중입니다. 잠시 기다려 주십시오", Toast.LENGTH_LONG).show();
                        startLoc();
                        mRequestingLocationUpdates = true;
                        btnLocalSearch.setChecked(false);
                        circle.setVisible(false);


                    }
                    else{
                        displayLocation();
                    }
                }
                else{
                    mRequestingLocationUpdates=false;
                    toggle_loc_change=false;
                    Toast.makeText(Context_, "위치 추적을 종료합니다.", Toast.LENGTH_LONG).show();
                    stopLoc();
                }
            }
        });

        btnLocalSearch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(setCircleOn) {
                    if (isChecked) {
                        toggleButton.setChecked(false);
                        circle.setVisible(true);
                        displayLocation();
                        Close_Layout.startAnimation(bottomUp);
                        Close_Layout.setVisibility(View.VISIBLE);
                        MarkerUpdate(MyApplication.getInstance().getPrefManager().getUser().getId(),String.valueOf(latitude),String.valueOf(longitude));
                        getCloseUsers(String.valueOf(latitude),String.valueOf(longitude),"1");

                    }else{
                        circle.setVisible(false);
                        Close_Layout.startAnimation(bottomDown);
                        Close_Layout.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        searchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ViewGroup.LayoutParams params = CoordinateView_Layout.getLayoutParams();
                    keep_height=params.height;
                    params.height=0;
                    CoordinateView_Layout.setLayoutParams(params);
                }
                else{
                    ViewGroup.LayoutParams params = CoordinateView_Layout.getLayoutParams();
                    params.height=keep_height;
                    CoordinateView_Layout.setLayoutParams(params);
                }
            }
        });

        btnLeaveMemory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleButton.setChecked(false);
                btnLocalSearch.setChecked(false);
                displayLocation();
                actionLeaveMemory();

            }
        });

        map_initialize();

        mapFragment.getView().setVisibility(View.INVISIBLE);
        toggle=true;

        displayLocation();

        return Li;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new MapItemAdapter(Context_));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(marker.getTag()!=null) {
                    Object tmpObj = marker.getTag();
                    String marker_id = Array.get(tmpObj, 0).toString();
                    String created_at = Array.get(tmpObj, 1).toString();
                    String user_name = Array.get(tmpObj, 2).toString();

                    Intent intent = new Intent(Activity_, MemoryActivity.class);
                    intent.putExtra("marker_id", marker_id);
                    intent.putExtra("marker_photo_path", marker.getSnippet());
                    intent.putExtra("marker_info", marker.getTitle());
                    intent.putExtra("created_at", created_at);
                    intent.putExtra("user_name", user_name);
                    Activity_.startActivity(intent);
                }

            }
        });
        mAdapter.notifyDataSetChanged();
        mAdapter.setmMap(mMap);

        // Add a marker in Sydney and move the camera
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

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        checkPlayServices();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        else{
            stopLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    private void displayLocation() {

        if(ActivityCompat.checkSelfPermission(Activity_, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(Activity_, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Activity_, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
            ActivityCompat.requestPermissions(Activity_, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);
        } else {
            if(mGoogleApiClient.isConnected()){
                mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();

                    lblLocation.setText(latitude + ", " + longitude);
                    focusMyLocation(latitude, longitude);
                } else {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    lblLocation
                            .setText("위치를 확인할 수 없습니다. GPS상태를 확인해주세요.");
                }
            }else{
                mGoogleApiClient.connect();
                Toast.makeText(Context_, "지도 정보를 불러오는 중입니다. 잠시 기다려 주세요.", Toast.LENGTH_LONG).show();
            }
        }




    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(Activity_)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mRequestingLocationUpdates) {
            if (!toggle_loc_change) {
                Toast.makeText(Context_, "위치 추적을 시작합니다.", Toast.LENGTH_LONG).show();

                toggle_loc_change = true;
            } else {
                Toast.makeText(Context_, "위치 추적중...", Toast.LENGTH_LONG).show();

            }
        }
        mLastLocation = location;

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        latitude=location.getLatitude();
        longitude=location.getLongitude();
        lblLocation.setText(latitude + ", " + longitude);
        circle.setCenter(new LatLng(latitude,longitude));
        focusMyLocation(latitude, longitude);
        MarkerUpdate(MyApplication.getInstance().getPrefManager().getUser().getId(),String.valueOf(latitude),String.valueOf(longitude));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    protected void focusMyLocation(double latitude_, double longitude_){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude_, longitude_), 13));
        if(MarkerArray.size() > 0){
            MarkerArray.get(0).setPosition(new LatLng(latitude_,longitude_));
            MarkerArray.get(0).setTitle(marker_notice);
        }
        else{
            MarkerArray.add(mMap.addMarker(new MarkerOptions().position(new LatLng(latitude_, longitude_)).title(marker_notice))); // initial location
            loadMarkerIcon(MarkerArray.get(0));
            setCircleOn=true;
            circle=mMap.addCircle(new CircleOptions()
                    .center(new LatLng(latitude_,longitude_))
                    .radius(1000)
                    .strokeColor(Color.argb(160,181,220,236))
                    .fillColor(Color.argb(160,181,220,236))
            );
            circle.setVisible(false);
            getMemoryMarker(MyApplication.getInstance().getPrefManager().getUser().getId());

        }
    }

    // modify this to make leave memory with picture and share with others
    protected void LeaveMyMemory(double latitude_, double longitude_, String Memory){
        MarkerArray.add(mMap.addMarker(new MarkerOptions().position(new LatLng(latitude_, longitude_)).title(Memory))); // initial location
    }

    protected void map_initialize(){
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if(toggle) {
            if (visible) {

                mapFragment.getView().setVisibility(View.VISIBLE);
            } else {
                mapFragment.getView().setVisibility(View.INVISIBLE);
            }
        }
    }

    protected void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(Activity_, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Activity_,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    protected void startLoc(){ 
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLoc(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void loadMarkerIcon(final Marker marker) {

        //LayoutInflater inflater2 = (LayoutInflater) Context_.getSystemService(LAYOUT_INFLATER_SERVICE);
        //Ma_P = (RelativeLayout) inflater2.inflate(R.layout.profile_marker, null);
        Ma_P=LayoutInflater.from(Activity_).inflate(R.layout.profile_marker,null);
        Ma_P_photo = (ImageView) Ma_P.findViewById(R.id.marker_profile);

        if(!MyApplication.getInstance().getPrefManager().getUser().getProfile_path().equals("default")) {
            Glide.with(this).load(EndPoints.BASE_URL + MyApplication.getInstance().getPrefManager().getUser().getProfile_path())
                    .asBitmap()
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .transform(new CropCircleTransformation(Context_))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                            Ma_P_photo.setImageBitmap(bitmap);
                            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(loadBitmapFromView(Ma_P, Activity_));
                            marker.setIcon(icon);
                        }
                    });
        }
        else{
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(loadBitmapFromView(Ma_P, Activity_));
            marker.setIcon(icon);
        }

        //Activity_.setContentView(Ma_P);




        /*
        Glide.with(this).load(EndPoints.BASE_URL+ MyApplication.getInstance().getPrefManager().getUser().getProfile_path())
                .asBitmap()
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .transform(new CropCircleTransformation(Context_))
                .into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
                marker.setIcon(icon);
            }
        });
        */
    }

    public static Bitmap loadBitmapFromView(View view,Activity activity) {


        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;

    }

    private void getCloseUsers(final String latitude, final String longitude, final String multiplier) {

        String endPoint = EndPoints.GET_USERS_CLOSE.replace("_LATITUDE_",latitude).replace("_LONGITUDE_",longitude).replace("_MULTIPLIER",multiplier);
        Log.e(TAG, "endPoint" + endPoint);

        final Location loc_A = new Location("A_");
        loc_A.setLatitude(Double.parseDouble(latitude));
        loc_A.setLongitude(Double.parseDouble(longitude));

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        UserArrayList.clear();
                        JSONArray users = obj.getJSONArray("users");
                        for (int i = 0; i < users.length(); i++) {
                            JSONObject userObj = (JSONObject) users.get(i);
                            double latitude_=Double.parseDouble(userObj.getString("latitude"));
                            double longitude_=Double.parseDouble(userObj.getString("longitude"));

                            Location loc_B = new Location("B_");
                            loc_B.setLatitude(latitude_);
                            loc_B.setLongitude(longitude_);

                            if(loc_A.distanceTo(loc_B) < 1000.0 * Double.parseDouble(multiplier)) {
                                String user_id_=userObj.getString("user_id");

                                if(!user_id_.equals(MyApplication.getInstance().getPrefManager().getUser().getId())) {

                                    User user = new User(userObj.getString("user_id"),
                                            userObj.getString("name"),
                                            userObj.getString("LinkUri"), userObj.getString("profile_photo_path"));

                                    user.setisOpen(userObj.getBoolean("isOpen"));

                                    UserArrayList.add(user);
                                }
                            }
                        }

                    } else {
                        Log.e(TAG, "server respond error");
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                }

                mAdapter.notifyDataSetChanged();

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

    public void MarkerUpdate(final String user_id, final String latitude, final String longitude){

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.MARKER_UPDAE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        // user successfully marked
                        Log.e(TAG, "user successfully marked");
                    } else {
                        // login error - simply toast the message
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Parsing error");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley_error");
            }
        }) {

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",user_id);
                params.put("latitude",latitude);
                params.put("longitude",longitude);

                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    public void actionLeaveMemory(){
        LayoutInflater inflater = (LayoutInflater) Context_.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View customView = inflater.inflate(R.layout.popup_leavememory,null);



        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );


        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }

        // Get a reference for the custom view close button
        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);

        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow.setFocusable(true);
        mPopupWindow.update();

        mPopupWindow.showAtLocation(recyclerView, Gravity.CENTER,0,0);


        View contentView = mPopupWindow.getContentView();

        final EditText inputInfo = (EditText) contentView.findViewById(R.id.input_info);
        final ImageView btn_leave = (ImageView) contentView.findViewById(R.id.btn_leave);
        memory_pic = (ImageView) contentView.findViewById(R.id.leavememory_pics);

        inputInfo.addTextChangedListener(new ThreeFragment.MyTextWatcher());

        memory_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButton.setChecked(false);
                btnLocalSearch.setChecked(false);
                doTakeAlbumAction();
            }
        });



        btn_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(inputInfo.getText().toString().equals(""))){
                    //CreateChatRoom(inputInfo.getText().toString(), "default");
                    final_lat=latitude;
                    final_long=longitude;
                    marker_info_=inputInfo.getText().toString();
                    new ThreeFragment.UploadFileToServer().execute();

                }
                else{
                    Toast.makeText(Context_, "흔적 내용을 입력해주세요." , Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private class MyTextWatcher implements TextWatcher {


        private MyTextWatcher( ) {

        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {

        }
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
                    .thumbnail(0.3f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(memory_pic);
            gotUri=true;

        }catch(Exception e){

        }
    }

    public void markerAdd(final String user_id, final String latitude, final String longitude, final String marker_photo_path, final String marker_info){

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.MARKER_ADD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        // user successfully marked
                        Log.e(TAG, "marker add success");
                    } else {
                        // login error - simply toast the message
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Parsing error");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley_error");
            }
        }) {

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",user_id);
                params.put("latitude",latitude);
                params.put("longitude",longitude);
                params.put("marker_photo_path",marker_photo_path);
                params.put("marker_info",marker_info);

                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
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
            HttpPost httppost = new HttpPost(EndPoints.MARKER_UPLOAD_URL);
            Log.e(TAG, "Right before going to try statement");
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) 0) * 100));
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
                        new StringBody(MyApplication.getInstance().getPrefManager().getUser().getId()));

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
                        final String Marker_photo_path = obj.getString("file_path");
                        markerAdd(MyApplication.getInstance().getPrefManager().getUser().getId(),
                                String.valueOf(final_lat),
                                String.valueOf(final_long),
                                Marker_photo_path,
                                marker_info_);
                        Toast.makeText(Context_, "흔적이 저장 되었습니다.", Toast.LENGTH_LONG).show();
                        for(int idx=1; idx<MarkerArray.size(); idx++){
                            MarkerArray.get(idx).remove();
                        }
                        MarkerArray.clear();
                        getMemoryMarker(MyApplication.getInstance().getPrefManager().getUser().getId());
                        gotUri=false;
                    } else {
                        Toast.makeText(Context_, obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Context_, "흔적 저장 실패.", Toast.LENGTH_LONG).show();
                }
            }
            else{ // 이미지 없이 저장 되는 경우
                markerAdd(MyApplication.getInstance().getPrefManager().getUser().getId(),
                        String.valueOf(final_lat),
                        String.valueOf(final_long),
                        "default",
                        marker_info_);
                Toast.makeText(Context_, "흔적이 저장 되었습니다.", Toast.LENGTH_LONG).show();
            }
            mPopupWindow.dismiss();

        }

    }

    private void getMemoryMarker(final String user_id) {

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
                mAdapter.notifyDataSetChanged();
                mAdapter.setmMap(mMap);

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



}