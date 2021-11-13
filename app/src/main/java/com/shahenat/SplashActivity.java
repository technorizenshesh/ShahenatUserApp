package com.shahenat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationSettingsResult;
import com.shahenat.Driver.HomeActivityDriver;
import com.shahenat.User.HomeActiivity;
import com.shahenat.databinding.ActivityMainBinding;
import com.shahenat.retrofit.Constant;
import com.shahenat.utils.DataManager;
import com.shahenat.utils.SessionManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;


public class SplashActivity extends AppCompatActivity {
    public static int SPLASH_TIME_OUT = 3000;
    int PERMISSION_ID = 44;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                processNextActivity();
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }



    }

    private void processNextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (DataManager.getInstance().getUserData(getApplicationContext()) != null &&
                        DataManager.getInstance().getUserData(getApplicationContext()).result != null &&
                        !DataManager.getInstance().getUserData(getApplicationContext())
                                .result.id.equalsIgnoreCase("")) {
                       if(DataManager.getInstance().getUserData(SplashActivity.this).result.type.equals("User")){
                           startActivity(new Intent(SplashActivity.this, HomeActiivity.class));
                           finish();
                       }
                       else{
                           startActivity(new Intent(SplashActivity.this, HomeActivityDriver.class));
                           finish();
                       }



                } else {
                    SessionManager.writeString(SplashActivity.this, Constant.KEY_Login_type,"User");
                    startActivity(new Intent(SplashActivity.this, ChosseLoginActivity.class));
                    finish();
                }


            }
        }, SPLASH_TIME_OUT);
    }


    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED  &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
               ) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                processNextActivity();
            }
        }

    }

    public void KeyHash() {
        byte[] sha1 = {
                (byte) 0xDC, (byte) 0xFF, (byte) 0xDE, (byte) 0x48, (byte) 0x89, (byte) 0x0F, (byte) 0xE4, (byte) 0x6A, (byte) 0x05, (byte) 0x0C, (byte) 0xD1, (byte) 0x74, (byte) 0xDB, (byte) 0xBE, (byte) 0xED, (byte) 0x02, (byte) 0xEF, (byte) 0xF7, (byte) 0x76, (byte) 0x9E
        };
        Log.e("keyhashGoogleSignin", Base64.encodeToString(sha1, Base64.NO_WRAP));

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }




}