package com.shahenatuserapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationSettingsResult;
import com.shahenatuserapp.databinding.ActivityMainBinding;

public class SplashActivity extends AppCompatActivity implements
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    public static final int RequestPermissionCode = 1;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main);

        Preference.save(SplashActivity.this,Preference.KEY_Login_type,"User");


        if (permissioncheck()) {
            finds();
        } else {
            requestPermission();
        }

    }

    private boolean permissioncheck() {
        int FourthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int FifthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        return FourthPermissionResult ==
                PackageManager.PERMISSION_GRANTED && FifthPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(SplashActivity.this, new String[]
                {
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION

                }, RequestPermissionCode);

        if (permissioncheck()) {
            finds();
        } else {
            requestPermission();
        }
    }

    private void finds() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent=new Intent(SplashActivity.this, ChosseLoginActivity.class);
                startActivity(intent);
                finish();

            }

        }, 3000);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

    }

}