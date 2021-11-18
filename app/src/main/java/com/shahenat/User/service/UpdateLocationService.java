package com.shahenat.User.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Timer;
import java.util.TimerTask;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UpdateLocationService extends Service {
    String TAG = "UpdateLastLocationService";

    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    public static final int notify = 5000;  //interval between two services(Here Service run every 1 Minute)
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling







    public UpdateLocationService() {
    }

    @Override
    public void onCreate() {
        requestNewLocationData();

     /*   if (mTimer != null) // Cancel if already existed
            mTimer.cancel();
        else
            mTimer = new Timer();   //recreate new

        mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify); */  //Schedule task
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void loadObjects() {


    }

    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("service is ", "running");
                    getLastLocation();
                }
            });
        }
    }

  /*  private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult.getLastLocation() == null)
                return;
            currentLocation = locationResult.getLastLocation();
            if (firstTimeFlag && googleMap != null) {
                animateCamera(currentLocation);
                firstTimeFlag = false;
            }
            showMarker(currentLocation);
        }
    };*/

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        Log.e("user Latitude", location.getLatitude() + "");
                        Log.e("user Longitude", location.getLongitude() + "");
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            Intent intent1 = new Intent("data_update_location1");
                            intent1.putExtra("latitude", String.valueOf(location.getLatitude()));
                            intent1.putExtra("longitude", String.valueOf(location.getLongitude()));
                            sendBroadcast(intent1);

                            // Toast.makeText(LocationService.this, "Update Location"+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
                            // latTextView.setText(location.getLatitude()+"");
                            //  lonTextView.setText(location.getLongitude()+"");
                        }
                    }
                }
        );


    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
      /*  LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );*/
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(7000);
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            if (mLastLocation == null) {
                requestNewLocationData();
            } else {
                Log.e("user Latitude", "" + mLastLocation.getLatitude() + "");
                Log.e("user Longitude", "" + mLastLocation.getLongitude() + "");
                Intent intent1 = new Intent("data_update_location1");
                intent1.putExtra("latitude", String.valueOf(mLastLocation.getLatitude()));
                intent1.putExtra("longitude", String.valueOf(mLastLocation.getLongitude()));
                sendBroadcast(intent1);
//                SessionManager.writeString(getApplicationContext(), SessionManager.USER_LATITUDE, "");
//                SessionManager.writeString(getApplicationContext(), SessionManager.USER_LONGITUDE, "");
                //  latTextView.setText(mLastLocation.getLatitude()+"");
                //  lonTextView.setText(mLastLocation.getLongitude()+"");
                //  Toast.makeText(LocationService.this, ""+mLastLocation.getLatitude(), Toast.LENGTH_SHORT).show();
            }
        }
    };































}
