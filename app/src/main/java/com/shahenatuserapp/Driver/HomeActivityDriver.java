package com.shahenatuserapp.Driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shahenatuserapp.ChosseLoginActivity;
import com.shahenatuserapp.GPSTracker;
import com.shahenatuserapp.Preference;
import com.shahenatuserapp.R;
import com.shahenatuserapp.User.ArrivingActivity;
import com.shahenatuserapp.User.HomeActiivity;
import com.shahenatuserapp.User.LoginActivity;
import com.shahenatuserapp.User.RideActivity;
import com.shahenatuserapp.databinding.ActivityHomeDriverBinding;
import com.skyfishjy.library.RippleBackground;

public class HomeActivityDriver extends AppCompatActivity implements OnMapReadyCallback {

    ActivityHomeDriverBinding binding;
    GPSTracker gpsTracker;
    double latitude = 0;
    double longitude = 0;
    private GoogleMap mMap;

    private View promptsView;
    private AlertDialog alertDialog;
    private AlertDialog alertDialog1;
    public Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_home_driver);

        activity=HomeActivityDriver.this;

        String  UserName = Preference.get(HomeActivityDriver.this,Preference.KEY_User_name);
        String  Email =Preference.get(HomeActivityDriver.this,Preference.KEY_User_email);
        String  UserImg =Preference.get(HomeActivityDriver.this,Preference.KEY_USer_img);

        binding.childNavDrawer.txtUserName.setText(UserName);
        binding.childNavDrawer.txtUserEmail.setText(Email);

        if(!UserImg.equalsIgnoreCase(""))
        {
            Glide.with(this).load(UserImg).into(binding.childNavDrawer.imgUser);
        }

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


        binding.dashboard.imgDrawer.setOnClickListener(v -> {
            navmenu();
        });

        binding.childNavDrawer.RRHome.setOnClickListener(v -> {

            navmenu();
        });

        binding.childNavDrawer.RRSignOut.setOnClickListener(v -> {
            navmenu();
            Preference.clearPreference(this);
            startActivity(new Intent(HomeActivityDriver.this, ChosseLoginActivity.class));

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



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               // navmenu();
                AlertDaliogNotification();

            }
        },5000);
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


}