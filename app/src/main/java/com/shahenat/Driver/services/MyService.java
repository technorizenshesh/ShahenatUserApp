package com.shahenat.Driver.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.shahenat.R;
import com.shahenat.retrofit.ApiClient;
import com.shahenat.retrofit.ShahenatInterface;
import com.shahenat.utils.DataManager;


import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyService extends Service {
    public static String TAG ="MyService";
    FusedLocationProviderClient mFusedLocationClient;
    public static final int notify = 5000;  // interval between two services(Here Service run every 1 Minute)
    private Handler mHandler = new Handler();   // run on another Thread to avoid crash
    private Timer mTimer = null; // timer handling
    ShahenatInterface apiInterface;

    public MyService() {}

    @Override
    public void onCreate() {
        apiInterface = ApiClient.getClient().create(ShahenatInterface.class);
        requestNewLocationData();


        String channelId = "channel-01";

//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId);
//        startForeground((int) System.currentTimeMillis(), mBuilder.build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        } else {
            startForeground(1, new Notification());
        }

        if (mTimer != null) // Cancel if already existed
            mTimer.cancel();
        else
            mTimer = new Timer();   // recreate new

        mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.shahenat";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle("")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
    }; */


    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
//                        Log.e("userLatitude", location.getLatitude() + "");
//                        Log.e("userLongitude", location.getLongitude() + "");
                        // Toast.makeText(MyService.this, "Latitude = " + location.getLatitude() +
                        //  "Longitude = " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
//                            Intent intent1 = new Intent("data_update_location1");
//                            intent1.putExtra("latitude", String.valueOf(location.getLatitude()));
//                            intent1.putExtra("longitude", String.valueOf(location.getLongitude()));
//                            sendBroadcast(intent1);
                             Log.e("Location====", String.valueOf(location.getLatitude()));


                            if (DataManager.getInstance().getUserData(getApplicationContext()) != null) {
                                updateProviderLatLon(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), DataManager.getInstance().getUserData(getApplicationContext()).result.id);
                            }

                            // Toast.makeText(LocationService.this, "Update Location"+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
                            // latTextView.setText(location.getLatitude() + "");
                            //  lonTextView.setText(location.getLongitude() + "");

                        }
                    }
                }
        );

    }

    public void updateProviderLatLon(String lat, String lon, String userId) {

        Log.e("kjsghfjdgsf","userId = " + userId);
        Log.e("kjsghfjdgsf","lat = " + lat);
        Log.e("kjsghfjdgsf","lon = " + lon);
        Map<String, String> map = new HashMap<>();
        map.put("user_id",userId);
        map.put("lat",lat);
        map.put("lon",lon);
        Log.e(TAG,"Upldate Driver Location Request "+map);
        Call<Map<String, String>> loginCall = apiInterface.updateLocation(map);
        loginCall.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String, String> data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG,"Upldate Driver Location Response :"+responseString);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });


    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
      /*LocationRequest mLocationRequest = new LocationRequest();
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
        locationRequest.setInterval(5000);
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
//              SessionManager.writeString(getApplicationContext(), SessionManager.USER_LATITUDE, "");
//              SessionManager.writeString(getApplicationContext(), SessionManager.USER_LONGITUDE, "");
                //  latTextView.setText(mLastLocation.getLatitude()+"");
                //  lonTextView.setText(mLastLocation.getLongitude()+"");
                //  Toast.makeText(LocationService.this, ""+mLastLocation.getLatitude(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        System.out.println("service in onTaskRemoved");
        long ct = System.currentTimeMillis(); // get current time
        Intent restartService = new Intent(getApplicationContext(),MyService.class);
        PendingIntent restartServicePI = PendingIntent.getService (
                getApplicationContext(), 0, restartService,
                0);

        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.setRepeating(AlarmManager.RTC_WAKEUP, ct, 1 * 1000, restartServicePI);


    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
            this.stopSelf();
        }
    }



}