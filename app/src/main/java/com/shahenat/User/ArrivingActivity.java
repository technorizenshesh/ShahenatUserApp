package com.shahenat.User;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.shahenat.GPSTracker;
import com.shahenat.MapRelated.DrawPollyLine;
import com.shahenat.R;
import com.shahenat.User.fragment.BottomBookingCancelFragment;
import com.shahenat.User.model.BookingDetailModelOne;
import com.shahenat.User.service.UpdateLocationService;
import com.shahenat.databinding.ActivityArrivingBinding;
import com.shahenat.retrofit.ApiClient;
import com.shahenat.retrofit.Constant;
import com.shahenat.retrofit.ShahenatInterface;
import com.shahenat.utils.DataManager;
import com.shahenat.utils.NetworkAvailablity;
import com.shahenat.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArrivingActivity extends AppCompatActivity implements OnMapReadyCallback {
    public String TAG = "ArrivingActivity";
    ActivityArrivingBinding binding;
    GoogleMap mMap;
    int PERMISSION_ID = 44;
    double dLatitude = 0.0, dLongitude = 0.0;
    ShahenatInterface apiInterface;
    String request_id = "", DriverId = "", DriverName = "", image = "", mobile = "",status="";
    private PolylineOptions lineOptions;
    private LatLng PickUpLatLng, DropOffLatLng, carLatLng,prelatLng;
    private MarkerOptions PicUpMarker, DropOffMarker, carMarker1;
    Marker carMarker;
    BookingDetailModelOne data1;
    double tolerance = 10; // meters
    ArrayList<LatLng> polineLanLongLine =new ArrayList<>();
    boolean isMarkerRotating = false;
    private float start_rotation;

    private View promptsView;
    private AlertDialog alertDialog;



    BroadcastReceiver LocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("latitude") != null) {
                if (NetworkAvailablity.checkNetworkStatus(ArrivingActivity.this)) getdriverLocation();
                else Toast.makeText(ArrivingActivity.this, getString(R.string.checkInternet), Toast.LENGTH_SHORT).show();
            }
        }
    };


    BroadcastReceiver TripStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("status") != null) {
                if (intent.getStringExtra("status").equals("chat")) {
                    if (NetworkAvailablity.checkNetworkStatus(ArrivingActivity.this)) {
                        //  request_id = intent.getStringExtra("request_id");
                       // getChatCount();
                    } else Toast.makeText(ArrivingActivity.this, getString(R.string.checkInternet), Toast.LENGTH_SHORT).show();
                } else if (intent.getStringExtra("status").equals("Cancel_by_driver")) {
                    startActivity(new Intent(ArrivingActivity.this, HomeActiivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                } else {
                    if (NetworkAvailablity.checkNetworkStatus(ArrivingActivity.this))
                        getBookingDetail(intent.getStringExtra("request_id"));
                    else Toast.makeText(ArrivingActivity.this, getString(R.string.checkInternet), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    private void getdriverLocation() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", SessionManager.readString(getApplicationContext(), Constant.driver_id, ""));
        Log.e("MapMap", "GET PROVIDER LATLONG REQUEST" + map);
        Call<Map<String, String>> subCategoryCall = apiInterface.getDriverLocation(map);
        subCategoryCall.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                try {
                    Map<String, String> data = response.body();
                    if (data.get("status").equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "GET PROVIDER LATLONG RESPONSE" + dataResponse);
                        dLatitude = Double.parseDouble(data.get("lat"));
                        dLongitude = Double.parseDouble(data.get("lon"));
                        carLatLng = new LatLng(dLatitude, dLongitude);
                        AddCarMarker(carLatLng);


                        Log.e("Current Locatoion===", dLatitude + "," + dLongitude);
                    } else if (data.get("status").equals("0")) {

                    }
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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(ShahenatInterface.class);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_arriving);
        initViews();
        request_id = SessionManager.readString(ArrivingActivity.this, Constant.request_id, "");
        initViews();
        getdriverLocation();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

       /* //Gps Lat Long
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
        });*/


    }

    private void initViews() {
        PicUpMarker = new MarkerOptions().title("Pick Up Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon));
        DropOffMarker = new MarkerOptions().title("Drop Off Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon));

        carMarker1 = new MarkerOptions().title("Vehicle")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.buldozer11));


        binding.cardCancel.setOnClickListener(v -> {

            BottomBookingCancelFragment bottomSheetFragment = new BottomBookingCancelFragment(ArrivingActivity.this);
            bottomSheetFragment.show(getSupportFragmentManager(), "ModalBottomSheet");

        });

        binding.cardCall.setOnClickListener(v -> {
            //AlertDaliogCall();
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile));
            startActivity(intent);
        });

        binding.Imgback.setOnClickListener(v -> {
            startActivity(new Intent(ArrivingActivity.this, HomeActiivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (!request_id.equals(""))
            getBookingDetail(request_id);

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





    public void getBookingDetail(String request_id) {
        Map<String, String> map = new HashMap<>();
        map.put("request_id", request_id);
        Log.e(TAG, "Get Booking Detail Request :" + map);
        Call<BookingDetailModelOne> call = apiInterface.bookingDetails(map);
        call.enqueue(new Callback<BookingDetailModelOne>() {
            @Override
            public void onResponse(Call<BookingDetailModelOne> call, Response<BookingDetailModelOne> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    data1 = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Get Booking Detail Response :" + responseString);
                    if (data1.status.equals("1")) {
                        DriverName = data1.result.driverDetails.firstName + " " + data1.result.driverDetails.lastLame;
                        DriverId = data1.result.driverDetails.id;
                        status = data1.result.status;
                        image = data1.result.driverDetails.image;
                        mobile = "+"+data1.result.driverDetails.phoneCode + data1.result.driverDetails.mobile;
                        binding.txtDriverName.setText(DriverName);
                        Glide.with(ArrivingActivity.this)
                                .load(image)
                                .apply(new RequestOptions().placeholder(R.drawable.user_default))
                                .override(300,300)
                                .into(binding.ivDriverPropic);
                        prelatLng = null;
                        PickUpLatLng = new LatLng(Double.parseDouble(data1.result.pLat), Double.parseDouble(data1.result.pLon));
                        DropOffLatLng = new LatLng(Double.parseDouble(data1.result.dLat), Double.parseDouble(data1.result.dLon));
                        if (data1.result.status.equals("Accepted")) {
                            Log.e(TAG,"Request Accept or Cancel Response :"+"Contionnxkxkxkxkxkkx");
                            DrawPolyLine1();
                        }
                        else if (data1.result.status.equals("Arrive")) {
                            DriverArriveDialog();
                        } else if (data1.result.status.equals("Start")) {
                            TripStartDialog();
                        } else if (data1.result.status.equals("Completed")) {
                            TripFinishDialog();
                        }

                    } else if (data1.status.equals("0")) {
                        Toast.makeText(ArrivingActivity.this, data1.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }



            @Override
            public void onFailure(Call<BookingDetailModelOne> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }


    private void DrawPolyLine1() {
        Log.e("CarLatLong=====",carLatLng+"");
        Log.e("PickUpLong=====",PickUpLatLng+"");
        DrawPollyLine.get(this).setOrigin(carLatLng)
                .setDestination(PickUpLatLng).execute(new DrawPollyLine.onPolyLineResponse() {
            @Override
            public void Success(ArrayList<LatLng> latLngs) {
                mMap.clear();
                polineLanLongLine.clear();
                polineLanLongLine =latLngs;
                lineOptions = new PolylineOptions();
                lineOptions.addAll(latLngs);
                lineOptions.width(10);
                lineOptions.geodesic(true);
                lineOptions.color(R.color.btn_green_color);
                AddDefaultMarker1();
                prelatLng =null;
                AddCarMarker(carLatLng);
            }
        });
    }

    public void AddDefaultMarker1() {
        if (mMap != null) {
            mMap.clear();
            if (lineOptions != null)
                mMap.addPolyline(lineOptions);
            if (carLatLng != null) {
                PicUpMarker.position(carLatLng);
                mMap.addMarker(PicUpMarker);
                // mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(PickUpLatLng)));
            }
            if (PickUpLatLng != null) {
                DropOffMarker.position(PickUpLatLng);
                mMap.addMarker(DropOffMarker);
            }
        }
    }



    public void AddCarMarker(LatLng latLng) {
        // if (carMarker != null) carMarker.remove();
        try {
            if (prelatLng == null) {
                carMarker1.position(latLng);
                carMarker = mMap.addMarker(carMarker1);
                prelatLng = latLng;
            } else {
                if (prelatLng != latLng) {
                    Log.e("locationChange====",latLng+"");
                    Location temp = new Location(LocationManager.GPS_PROVIDER);
                    temp.setLatitude(latLng.latitude);
                    temp.setLongitude(latLng.longitude);
                    //  MarkerAnimation.animateMarkerToGB(carMarker, carLatLng, new LatLngInterpolator.Spherical());
                    //  MarkerAnimation.move(mMap,carMarker,temp);
                    // MarkerAnimation.animateMarker(mMap,carMarker,polineLanLongLine,isMarkerRotating);
                    // float bearing = (float) bearingBetweenLocations(prelatLng, carLatLng);
                    // rotateMarker(carMarker, bearing);

                    moveVechile(carMarker, temp);
                    rotateMarker(carMarker, temp.getBearing(), start_rotation);
                    prelatLng = latLng;
                    if (status.equals("Start")) {
                        if (PolyUtil.isLocationOnPath(carLatLng, polineLanLongLine, true, tolerance)) {
                            Log.e("chala on road===", "==true===");

                        } else {
                            DrawPolyLine2();
                            Log.e("chala on road nahi hai=", "==false===");
                        }
                    }
                }
            }
            animateCamera(carLatLng);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void DrawPolyLine() {
        DrawPollyLine.get(this).setOrigin(PickUpLatLng)
                .setDestination(DropOffLatLng).execute(new DrawPollyLine.onPolyLineResponse() {
            @Override
            public void Success(ArrayList<LatLng> latLngs) {
                mMap.clear();
                polineLanLongLine.clear();
                polineLanLongLine =latLngs;
                lineOptions = new PolylineOptions();
                lineOptions.addAll(latLngs);
                lineOptions.width(10);
                lineOptions.geodesic(true);
                lineOptions.color(R.color.btn_green_color);
                AddDefaultMarker();
                prelatLng =null;
                AddCarMarker(carLatLng);
            }
        });
    }

    public void AddDefaultMarker() {
        if (mMap != null) {
            mMap.clear();
            if (lineOptions != null)
                mMap.addPolyline(lineOptions);
            if (PickUpLatLng != null) {
                PicUpMarker.position(PickUpLatLng);
                mMap.addMarker(PicUpMarker);
                // mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(PickUpLatLng)));
            }
            if (DropOffLatLng != null) {
                DropOffMarker.position(DropOffLatLng);
                mMap.addMarker(DropOffMarker);
            }
        }
    }

    private void DrawPolyLine2() {
        Log.e("CarLatLong=====",carLatLng+"");
        Log.e("PickUpLong=====",PickUpLatLng+"");
        DrawPollyLine.get(this).setOrigin(carLatLng)
                .setDestination(DropOffLatLng).execute(new DrawPollyLine.onPolyLineResponse() {
            @Override
            public void Success(ArrayList<LatLng> latLngs) {
                mMap.clear();
                lineOptions = new PolylineOptions();
                lineOptions.addAll(latLngs);
                lineOptions.width(10);
                lineOptions.geodesic(true);
                lineOptions.color(R.color.btn_green_color);
                AddDefaultMarker2();
                prelatLng =null;
                // AddCarMarker(carLatLng);
            }
        });
    }

    public void AddDefaultMarker2() {
        if (mMap != null) {
            mMap.clear();
            if (lineOptions != null)
                mMap.addPolyline(lineOptions);
            if (carLatLng != null) {
                PicUpMarker.position(carLatLng);
                mMap.addMarker(PicUpMarker);
                // mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(PickUpLatLng)));
            }
            if (DropOffLatLng != null) {
                DropOffMarker.position(DropOffLatLng);
                mMap.addMarker(DropOffMarker);
            }

            if (carMarker != null) carMarker.remove();
            carMarker1.position(carLatLng);
            carMarker = mMap.addMarker(carMarker1);
            prelatLng = carLatLng;
        }
    }

    public void moveVechile(final Marker myMarker, final Location finalPosition) {

        final LatLng startPosition = myMarker.getPosition();

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + (finalPosition.getLatitude()) * t,
                        startPosition.longitude * (1 - t) + (finalPosition.getLongitude()) * t);
                myMarker.setPosition(currentPosition);
                // myMarker.setRotation(finalPosition.getBearing());


                // Repeat till progress is completeelse
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                    // handler.postDelayed(this, 100);
                } else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }
            }
        });


    }


    public void rotateMarker(final Marker marker, final float toRotation, final float st) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1555;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;


                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                start_rotation = -rot > 180 ? rot / 2 : rot;
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }


    private void animateCamera(@NonNull LatLng location) {
        //  LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(location)));
    }


    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  getChatCount();
        callService();
        registerReceiver(LocationReceiver, new IntentFilter("data_update_location1"));
        registerReceiver(TripStatusReceiver, new IntentFilter("Job_Status_Action"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(LocationReceiver);
        unregisterReceiver(TripStatusReceiver);
        stopService(new Intent(ArrivingActivity.this, UpdateLocationService.class));
    }


    private void callService() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                startService(new Intent(ArrivingActivity.this, UpdateLocationService.class));
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
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
                setCurrentLoc();
            }
        }
    }

    private void setCurrentLoc() {
        DrawPolyLine();
    }


    public void DriverArriveDialog() {
        final Dialog dialog = new Dialog(ArrivingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_driver_arrived);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        binding.titler.setText(getString(R.string.driver_arrived));
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(getString(R.string.driver_arrived_pickup_location));

        dialog.setCanceledOnTouchOutside(true);
        TextView btnOk = dialog.findViewById(R.id.btnOk);
        TextView tvMsg = dialog.findViewById(R.id.tvMsg);
        tvMsg.setVisibility(View.VISIBLE);

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                setCurrentLoc();
            } else {
                Toast.makeText(ArrivingActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    public void TripStartDialog() {
        final Dialog dialog = new Dialog(ArrivingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_driver_arrived);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);

        tvTitle.setText(getString(R.string.your_trip_has_begin));
        binding.titler.setText(getString(R.string.trip_start));
        dialog.setCanceledOnTouchOutside(true);
        TextView btnOk = dialog.findViewById(R.id.btnOk);
        TextView tvMsg = dialog.findViewById(R.id.tvMsg);
        tvMsg.setVisibility(View.GONE);

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }


    public void TripFinishDialog() {

        final Dialog dialog = new Dialog(ArrivingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_driver_arrived);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);

        tvTitle.setText(getString(R.string.your_trip_is_finished));
        binding.titler.setText(getString(R.string.trip_end));
        dialog.setCanceledOnTouchOutside(true);
        TextView btnOk = dialog.findViewById(R.id.btnOk);
        TextView tvMsg = dialog.findViewById(R.id.tvMsg);
        tvMsg.setVisibility(View.GONE);

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            //   binding.titler.setText("Send Feedback");
           // binding.Imgback.setVisibility(View.GONE);
         //   binding.rlDriver.setVisibility(View.GONE);
            //  binding.rlFeedback.setVisibility(View.VISIBLE);
        //    Intent i = new Intent(TrackAct.this, TripEndAct.class).putExtra("request_id", request_id)
        //            .putExtra("driver_id", DriverId);
      //      startActivityForResult(i, 1);
            startActivity(new Intent(ArrivingActivity.this,HomeActiivity.class));
            finish();
        });

        dialog.show();

    }

}