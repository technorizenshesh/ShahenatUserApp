package com.shahenatuserapp.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.braintreepayments.cardform.view.CardForm;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shahenatuserapp.Driver.AcceptingActivity;
import com.shahenatuserapp.GPSTracker;
import com.shahenatuserapp.R;
import com.shahenatuserapp.databinding.ActivityPaymentOptionBinding;

public class PaymentOptionActivity extends AppCompatActivity implements OnMapReadyCallback{

    ActivityPaymentOptionBinding binding;
    GPSTracker gpsTracker;
    double latitude = 0;
    double longitude = 0;
    private GoogleMap mMap;

    private View promptsView;
    private AlertDialog alertDialog;
    public Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding= DataBindingUtil.setContentView(this,R.layout.activity_payment_option);

       activity=PaymentOptionActivity.this;

       binding.Imgback.setOnClickListener(v -> {
           onBackPressed();
       });

        //Gps Lat Long
        gpsTracker = new GPSTracker(PaymentOptionActivity.this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingsAlert();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapAriaval);
        mapFragment.getMapAsync(this);


        binding.RRCard.setOnClickListener(v -> {

            AlertDaliogRecharge();

        });

        binding.RRWallet.setOnClickListener(v -> {
            startActivity(new Intent(activity, ArrivingActivity.class));

        });

        binding.RRCash.setOnClickListener(v -> {

            startActivity(new Intent(activity, ArrivingActivity.class));

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



    private void AlertDaliogRecharge() {
        LayoutInflater li;
        ImageView ivBack;
        CardForm cardForm;
        RelativeLayout RRDone;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(PaymentOptionActivity.this);
        promptsView = li.inflate(R.layout.activity_stripe_payment, null);
        ivBack = (ImageView) promptsView.findViewById(R.id.ivBack);
        cardForm = (CardForm) promptsView.findViewById(R.id.card_form);
        RRDone = (RelativeLayout) promptsView.findViewById(R.id.RRDone);

        alertDialogBuilder = new AlertDialog.Builder(PaymentOptionActivity.this, R.style.myFullscreenAlertDialogStyle);   //second argument

        alertDialogBuilder.setView(promptsView);

        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                //.mobileNumberExplanation("SMS is required on this number")
                .setup(PaymentOptionActivity .this);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

        RRDone.setOnClickListener(v -> {

            startActivity(new Intent(activity, ArrivingActivity.class));

            alertDialog.dismiss();

        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}