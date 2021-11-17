package com.shahenat.Driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.shahenat.ChosseLoginActivity;
import com.shahenat.Driver.dialog.NewRequestDialog;
import com.shahenat.Driver.services.MyService;
import com.shahenat.GPSTracker;
import com.shahenat.R;
import com.shahenat.User.HomeActiivity;
import com.shahenat.User.model.LoginModel;
import com.shahenat.databinding.ActivityHomeDriverBinding;
import com.shahenat.retrofit.ApiClient;
import com.shahenat.retrofit.Constant;
import com.shahenat.retrofit.ShahenatInterface;
import com.shahenat.utils.DataManager;
import com.shahenat.utils.NetworkAvailablity;
import com.shahenat.utils.SessionManager;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivityDriver extends AppCompatActivity implements OnMapReadyCallback {
    public String TAG = "HomeActivityDriver";
    ActivityHomeDriverBinding binding;
    GPSTracker gpsTracker;
    double latitude = 0;
    double longitude = 0;
    private GoogleMap mMap;

    private View promptsView;
    private AlertDialog alertDialog;
    private AlertDialog alertDialog1;
    public Activity activity;
    ShahenatInterface apiInterface;
    LoginModel loginModel;

    BroadcastReceiver JobStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Dialog Open ====","=====");
            try {
                JSONObject object = new JSONObject(intent.getStringExtra("object"));
                if(intent.getStringExtra("object")!= null) {
                    Log.e("Dialog Open11111 ====","=====");
                    if(object.get("status").equals("Cancel_by_user")){
                        NewRequestDialog.getInstance().stopCountDownTimer();
                    } else {
                        NewRequestDialog.getInstance().Request(HomeActivityDriver.this, intent.getStringExtra("object"));
                    } }
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(ShahenatInterface.class);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_home_driver);

        activity=HomeActivityDriver.this;
        setUp();
    }

    private void setUp() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Gps Lat Long
        gpsTracker = new GPSTracker(HomeActivityDriver.this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingsAlert();
        }


        binding.dashboard1.imgDrawer.setOnClickListener(v -> {
            navmenu();
        });

        binding.childNavDrawer.RRHome.setOnClickListener(v -> {

            navmenu();
        });

        binding.childNavDrawer.RRSignOut.setOnClickListener(v -> {
            navmenu();
            SessionManager.clear(HomeActivityDriver.this,DataManager.getInstance().getUserData(HomeActivityDriver.this).result.id);
        });

        binding.childNavDrawer.RRWallet.setOnClickListener(v -> {
            navmenu();
            startActivity(new Intent(HomeActivityDriver.this, WalletScreen.class));

        });

        binding.childNavDrawer.RREARNINGS.setOnClickListener(v -> {
            navmenu();
          startActivity(new Intent(HomeActivityDriver.this,EarningScreenActivity.class));
        });

        binding.childNavDrawer.RRPurchesPlan.setOnClickListener(v -> {
            navmenu();
           startActivity(new Intent(HomeActivityDriver.this,PurchesHistorySceen.class));
        });

        binding.childNavDrawer.RlProfile.setOnClickListener(v -> {
            navmenu();
            startActivity(new Intent(HomeActivityDriver.this,DriverProfileAct.class));
        });



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               // navmenu();
                AlertDaliogNotification();

            }
        },5000);

        binding.dashboard1.switchOnOff.setOnClickListener(v -> {
            if(binding.dashboard1.switchOnOff.isChecked()){
                if(NetworkAvailablity.checkNetworkStatus(HomeActivityDriver.this))
                    changeStatus(DataManager.getInstance().getUserData(HomeActivityDriver.this).result.id,"ONLINE");
                else Toast.makeText(HomeActivityDriver.this, getString(R.string.checkInternet), Toast.LENGTH_SHORT).show();
            }
            else {
                if(NetworkAvailablity.checkNetworkStatus(HomeActivityDriver.this))
                    changeStatus(DataManager.getInstance().getUserData(HomeActivityDriver.this).result.id,"OFFLINE");
                else Toast.makeText(HomeActivityDriver.this, getString(R.string.checkInternet), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        mMap.clear();
        LatLng sydney = new LatLng(latitude, longitude);

        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Sydney")
                .snippet("Population: 4,627,300")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon)));

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()))));


    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(14).build();
    }

    public void navmenu() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);
        } else {
            binding.drawer.openDrawer(GravityCompat.START);
        }
    }

    private void AlertDaliogCall() {

        LayoutInflater li;
        RelativeLayout RRICall;
        RelativeLayout RRCall;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(HomeActivityDriver.this);
        promptsView = li.inflate(R.layout.alert_driver_accept, null);
        RRICall = (RelativeLayout) promptsView.findViewById(R.id.RRICall);
        RRCall = (RelativeLayout) promptsView.findViewById(R.id.RRCall);
        alertDialogBuilder = new AlertDialog.Builder(HomeActivityDriver.this);   //second argument
        alertDialogBuilder.setView(promptsView);

        RRICall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

        RRCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    private void AlertDaliogNotification() {

        LayoutInflater li;
        RelativeLayout RRAccept;
        ImageView imgCross;
        RippleBackground ripple;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(HomeActivityDriver.this);
        promptsView = li.inflate(R.layout.alert_driver_accept, null);
        RRAccept = (RelativeLayout) promptsView.findViewById(R.id.RRAccept);
        imgCross = (ImageView) promptsView.findViewById(R.id.imgCross);
        ripple = (RippleBackground) promptsView.findViewById(R.id.ripple);
        alertDialogBuilder = new AlertDialog.Builder(HomeActivityDriver.this, R.style.myFullscreenAlertDialogStyle);   //second argument
        ripple.startRippleAnimation();
        //alertDialogBuilder = new AlertDialog.Builder(RideActivity.this);
        alertDialogBuilder.setView(promptsView);

        RRAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDaliogPayment();

                alertDialog.dismiss();
            }
        });
        imgCross.setOnClickListener(v -> {
            alertDialog.dismiss();
        });


        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void AlertDaliogPayment() {

        LayoutInflater li;
        CardView cardPayment;
        ImageView imgCross;
        RippleBackground ripple;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(HomeActivityDriver.this);
        promptsView = li.inflate(R.layout.alert_payment, null);
        imgCross = (ImageView) promptsView.findViewById(R.id.imgCross);
        cardPayment = (CardView) promptsView.findViewById(R.id.cardPayment);
        alertDialogBuilder = new AlertDialog.Builder(HomeActivityDriver.this, R.style.myFullscreenAlertDialogStyle);   //second argument
        //alertDialogBuilder = new AlertDialog.Builder(RideActivity.this);
        alertDialogBuilder.setView(promptsView);

        cardPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(activity,AcceptingActivity.class));

                alertDialog1.dismiss();
            }
        });

        imgCross.setOnClickListener(v -> {
            alertDialog1.dismiss();
        });
        alertDialog1 = alertDialogBuilder.create();
        alertDialog1.show();
        alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(JobStatusReceiver, new IntentFilter("Job_Status_Action1"));
        ContextCompat.startForegroundService(getApplicationContext(),new Intent(HomeActivityDriver.this, MyService.class));
        setUserInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(JobStatusReceiver);
        stopService(new Intent(this, MyService.class));
    }



    private void changeStatus(String id, String status) {
        Map<String,String> map = new HashMap<>();
        map.put("user_id",id);
        map.put("status",status);
        //  map.put("type",type);
        Log.e(TAG,"Upldate Driver Status Request "+map);
        Call<Map<String,String>> loginCall = apiInterface.updateStatus(map);
        loginCall.enqueue(new Callback<Map<String,String>>() {
            @Override
            public void onResponse(Call<Map<String,String>> call, Response<Map<String,String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String,String> data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    SessionManager.writeString(HomeActivityDriver.this, Constant.driver_status,status);
                    binding.dashboard1.switchOnOff.setText(status);
                    Log.e(TAG,"Upldate Driver Status Response :"+responseString);
                    Toast.makeText(HomeActivityDriver.this, status, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Map<String,String>> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }


    public void setUserInfo(){
        loginModel = new LoginModel();
        loginModel = DataManager.getInstance().getUserData(HomeActivityDriver.this);
        binding.childNavDrawer.txtUserName.setText(loginModel.result.firstName);
        binding.childNavDrawer.txtUserEmail.setText(loginModel.result.email);
        binding.dashboard1.tvName.setText(loginModel.result.firstName + " " +loginModel.result.lastName);
      //  binding.dashboard1.tvNumber.setText(loginModel.result.);

        SessionManager.writeString(HomeActivityDriver.this,Constant.driver_status,loginModel.result.onlineStatus);
        if(SessionManager.readString(HomeActivityDriver.this,Constant.driver_status,"").equals("ONLINE")) binding.dashboard1.switchOnOff.setChecked(true);
        else  binding.dashboard1.switchOnOff.setChecked(false);
        Glide.with(this).load(loginModel.result.image).error(R.drawable.user_default).into(binding.childNavDrawer.imgUser);
        Glide.with(this).load(loginModel.result.image).error(R.drawable.user_default).into(binding.dashboard1.userImgg);
        binding.dashboard1.switchOnOff.setText(SessionManager.readString(HomeActivityDriver.this,Constant.driver_status,""));




    }

}