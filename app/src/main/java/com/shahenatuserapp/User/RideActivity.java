package com.shahenatuserapp.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

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
import com.shahenatuserapp.R;
import com.shahenatuserapp.SplashActivity;
import com.shahenatuserapp.User.adapter.AvalibilityAdapter;
import com.shahenatuserapp.User.adapter.NearByAvaiableAdapter;
import com.shahenatuserapp.User.model.CategoryModel;
import com.shahenatuserapp.databinding.ActivityRideBinding;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;

public class RideActivity extends AppCompatActivity implements OnMapReadyCallback {

    ActivityRideBinding binding;

    GPSTracker gpsTracker;
    double latitude = 0;
    double longitude = 0;
    private GoogleMap mMap;

    private ArrayList<CategoryModel> modelList = new ArrayList<>();
    NearByAvaiableAdapter mAdapter;
    String ProductName="";

    private View promptsView;
    private AlertDialog alertDialog;

    String PaymentType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_ride);

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.RRBook.setOnClickListener(v -> {

            AlertDaliogArea();

        });

        //Gps Lat Long
        gpsTracker = new GPSTracker(RideActivity.this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingsAlert();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        setAdapter();

        binding.RadioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.RadioCash:
                        PaymentType = "Cash";
                        // do operations specific to this selection
                        break;
                    case R.id.RadioCard:
                        PaymentType = "Card";
                        // do operations specific to this selection
                        break;
                    case R.id.RadioWallet:
                        PaymentType = "Wallet";
                        // do operations specific to this selection
                        break;
                }
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

    private void setAdapter() {

        this.modelList.add(new CategoryModel("Bulldozer",R.drawable.buldozer));
        this.modelList.add(new CategoryModel("Bulldozer",R.drawable.box_truck));
        this.modelList.add(new CategoryModel("Bulldozer",R.drawable.box_truck));

        mAdapter = new NearByAvaiableAdapter(RideActivity.this,modelList);
        binding.recyclerNearBy.setHasFixedSize(true);
        // use a linear layout manager
        binding.recyclerNearBy.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true));
        binding.recyclerNearBy.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener(new NearByAvaiableAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, CategoryModel model) {

                ProductName=model.getName().toString();
                Toast.makeText(RideActivity.this, ""+ProductName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AlertDaliogArea() {

        LayoutInflater li;
        ImageView imgCross;
        RippleBackground ripple;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(RideActivity.this);
        promptsView = li.inflate(R.layout.alert_search_for_driver, null);
        imgCross = (ImageView) promptsView.findViewById(R.id.imgCross);
        ripple = (RippleBackground) promptsView.findViewById(R.id.ripple);
        alertDialogBuilder = new AlertDialog.Builder(RideActivity.this, R.style.myFullscreenAlertDialogStyle);   //second argument
        ripple.startRippleAnimation();
        //alertDialogBuilder = new AlertDialog.Builder(RideActivity.this);
        alertDialogBuilder.setView(promptsView);

        imgCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(RideActivity.this,ArrivingActivity.class));

            }
        }, 4000);

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }
}