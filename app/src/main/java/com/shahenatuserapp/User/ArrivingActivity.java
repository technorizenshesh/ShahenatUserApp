package com.shahenatuserapp.User;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shahenatuserapp.GPSTracker;
import com.shahenatuserapp.R;
import com.shahenatuserapp.User.fragment.BottomBookingCancelFragment;
import com.shahenatuserapp.databinding.ActivityArrivingBinding;
import com.skyfishjy.library.RippleBackground;

public class ArrivingActivity extends AppCompatActivity implements OnMapReadyCallback {

    ActivityArrivingBinding binding;
    GPSTracker gpsTracker;
    double latitude = 0;
    double longitude = 0;
    private GoogleMap mMap;

    private View promptsView;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_arriving);


        //Gps Lat Long
        gpsTracker = new GPSTracker(ArrivingActivity.this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingsAlert();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapAriaval);
        mapFragment.getMapAsync(this);

        binding.cardCancel.setOnClickListener(v -> {

            BottomBookingCancelFragment bottomSheetFragment = new BottomBookingCancelFragment(ArrivingActivity.this);
            bottomSheetFragment.show(getSupportFragmentManager(), "ModalBottomSheet");

        });

        binding.cardCall.setOnClickListener(v -> {

            AlertDaliogCall();

        });

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
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

    private void AlertDaliogCall() {

        LayoutInflater li;
        RelativeLayout RRICall;
        RelativeLayout RRCall;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(ArrivingActivity.this);
        promptsView = li.inflate(R.layout.alert_call, null);
        RRICall = (RelativeLayout) promptsView.findViewById(R.id.RRICall);
        RRCall = (RelativeLayout) promptsView.findViewById(R.id.RRCall);
        alertDialogBuilder = new AlertDialog.Builder(ArrivingActivity.this);   //second argument

        //alertDialogBuilder = new AlertDialog.Builder(RideActivity.this);
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
}