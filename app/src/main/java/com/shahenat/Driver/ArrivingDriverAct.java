package com.shahenat.Driver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
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
import com.shahenat.Driver.Model.BookingDetailModelTwo;
import com.shahenat.GPSTracker;
import com.shahenat.MapRelated.DrawPollyLine;
import com.shahenat.R;
import com.shahenat.User.ArrivingActivity;
import com.shahenat.User.fragment.BottomBookingCancelFragment;
import com.shahenat.User.model.BookingDetailModel;
import com.shahenat.User.service.UpdateLocationService;
import com.shahenat.databinding.ActivityArrivingBinding;
import com.shahenat.databinding.ActivityArrivingDriverBinding;
import com.shahenat.retrofit.ApiClient;
import com.shahenat.retrofit.ShahenatInterface;
import com.shahenat.utils.DataManager;
import com.shahenat.utils.NetworkAvailablity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ng.max.slideview.SlideView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArrivingDriverAct extends AppCompatActivity implements OnMapReadyCallback {

    ActivityArrivingDriverBinding binding;
    public static String TAG = "TrackAct";
    GoogleMap mMap;
    GPSTracker gpsTracker;
    int PERMISSION_ID = 44;
    Vibrator vibrator;
    private PolylineOptions lineOptions;
    private LatLng PickUpLatLng, DropOffLatLng, carLatLng, prelatLng, mapCenterLatLng;
    private MarkerOptions PicUpMarker;
    private MarkerOptions DropOffMarker;
    private MarkerOptions carMarker1;
    Marker carMarker;
    AlertDialog.Builder builder1;
    ShahenatInterface apiInterface;
    ArrayList<LatLng> polineLanLongLine = new ArrayList<>();
    String request_id = "", driverStatus = "", UserId = "", UserName = "", UserImage = "", mobile = "";
    double tolerance = 10; // meters
    boolean isMarkerRotating = false;
    private float start_rotation;


    BroadcastReceiver LocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("latitude") != null) {
                carLatLng = new LatLng(Double.parseDouble(intent.getStringExtra("latitude")), Double.parseDouble(intent.getStringExtra("longitude")));
                if (prelatLng == null) {
                    if (driverStatus.equals("Accept")) DrawPolyLine1();
                    //   else if(driverStatus.equals("Arrived"))  DrawPolyLine();
                    //   else if(driverStatus.equals("Start"))  DrawPolyLine1();
                    if (carMarker != null) carMarker.remove();
                    carMarker1.position(carLatLng);
                    carMarker = mMap.addMarker(carMarker1);
                    carMarker1.flat(true);
                    prelatLng = carLatLng;
                    if (PolyUtil.isLocationOnPath(carLatLng, polineLanLongLine, true, tolerance)) {
                        Log.e("chala on road===", "==true===");
                        //DrawPolyLine1();
                    } else Log.e("chala on road nahi hai=", "==false===");

                } else {
                    if (prelatLng != carLatLng) {
                        Log.e("locationChange====", carLatLng + "");
                        Location temp = new Location(LocationManager.GPS_PROVIDER);
                        temp.setLatitude(Double.parseDouble(intent.getStringExtra("latitude")));
                        temp.setLongitude(Double.parseDouble(intent.getStringExtra("longitude")));
                        //  MarkerAnimation.animateMarkerToGB(carMarker, carLatLng, new LatLngInterpolator.Spherical());
                        //  MarkerAnimation.move(mMap,carMarker,temp);
                        // MarkerAnimation.animateMarker(mMap,carMarker,polineLanLongLine,isMarkerRotating);
                        // float bearing = (float) bearingBetweenLocations(prelatLng, carLatLng);
                        // rotateMarker(carMarker, bearing);

                        moveVechile(carMarker, temp);
                        rotateMarker(carMarker, temp.getBearing(), start_rotation);

                        prelatLng = carLatLng;


                        if (driverStatus.equals("Start")) {
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
            }

            //  binding.tvAddress.setText(getAddress(TrackAct.this,Double.parseDouble(intent.getStringExtra("latitude")), Double.parseDouble(intent.getStringExtra("longitude"))));
        }
    };

    BroadcastReceiver TripStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("status") != null) {
                if (intent.getStringExtra("status").equals("chat")) {
                    if (NetworkAvailablity.checkNetworkStatus(ArrivingDriverAct.this)) {
                        //   request_id = intent.getStringExtra("request_id");
                       // getChatCount();
                    } else Toast.makeText(ArrivingDriverAct.this, getString(R.string.checkInternet), Toast.LENGTH_SHORT).show();

                } else if (intent.getStringExtra("status").equals("Cancel_by_user")) {
                    startActivity(new Intent(ArrivingDriverAct.this, HomeActivityDriver.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(ShahenatInterface.class);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_arriving_driver);


        /*//Gps Lat Long
        gpsTracker = new GPSTracker(ArrivingDriverAct.this);
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

            BottomBookingCancelFragment bottomSheetFragment = new BottomBookingCancelFragment(ArrivingDriverAct.this);
            bottomSheetFragment.show(getSupportFragmentManager(), "ModalBottomSheet");

        });

        binding.cardCall.setOnClickListener(v -> {

            AlertDaliogCall();

        });

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });*/


        gpsTracker = new GPSTracker(ArrivingDriverAct.this);
        initView();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (getIntent() != null)
            request_id = getIntent().getStringExtra("request_id");

    }



    private void initView() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        binding.header.tvTitle.setText(getString(R.string.pick_up_passenger));
        binding.header.Imgback.setOnClickListener(v -> {
            finish();
        });

        binding.ivNavigate.setOnClickListener(v -> {
            setCurrentLocNavi();
        });

        binding.btnArrived.setOnClickListener(v -> {
            DriverArrivedDialog();
        });

        binding.slideViewBegin.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                if (NetworkAvailablity.checkNetworkStatus(ArrivingDriverAct.this)) DriverChangeStatus(request_id, "Start");
                else Toast.makeText(ArrivingDriverAct.this, getString(R.string.checkInternet), Toast.LENGTH_SHORT).show();

            }
        });


        binding.slideViewEnd.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                if (NetworkAvailablity.checkNetworkStatus(ArrivingDriverAct.this)) DriverChangeStatus(request_id, "Completed");
                else Toast.makeText(ArrivingDriverAct.this, getString(R.string.checkInternet), Toast.LENGTH_SHORT).show();

            }
        });

     /*   binding.layoutforChat.setOnClickListener(v -> {
            startActivity(new Intent(TrackAct.this, MsgChatAct.class)
                    .putExtra("UserId", UserId)
                    .putExtra("UserName", UserName)
                    .putExtra("UserImage", UserImage)
                    .putExtra("request_id", request_id));
        });*/

        binding.layoutforCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile));
            startActivity(intent);
        });

        binding.ivCancel.setOnClickListener(v -> {
            startActivity(new Intent(ArrivingDriverAct.this, CancelActivityDriver.class).putExtra("request_id", request_id));
        });

        PicUpMarker = new MarkerOptions().title("Pick Up Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon));
        DropOffMarker = new MarkerOptions().title("Drop Off Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon));

        carMarker1 = new MarkerOptions().title("Vehicle")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.buldozer11));

    }


    public void DriverArrivedDialog() {
        builder1 = new AlertDialog.Builder(ArrivingDriverAct.this);
        builder1.setMessage(getResources().getString(R.string.are_you_sure_you_have_arrived_at_pickup_location_of_passenger));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (NetworkAvailablity.checkNetworkStatus(ArrivingDriverAct.this))
                            DriverChangeStatus(request_id, "Arrive");
                        else Toast.makeText(ArrivingDriverAct.this, getString(R.string.checkInternet), Toast.LENGTH_SHORT).show();
                    }
                });

        builder1.setNegativeButton(
                getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();

    }


    private void setCurrentLocNavi() {

        mMap.addMarker(new MarkerOptions().position(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude())).title("My Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon)));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()))));
        binding.tvAddress.setText(getAddress(ArrivingDriverAct.this, gpsTracker.getLatitude(), gpsTracker.getLongitude()));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getBookingDetail(request_id);

    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(17).build();
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


    private void animateCamera(@NonNull LatLng location) {
        //  LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(location)));
    }

    public void getBookingDetail(String request_id) {
        Map<String, String> map = new HashMap<>();
        map.put("request_id", request_id);
        Log.e(TAG, "Request Accept or Cancel Request :" + map);
        Call<BookingDetailModelTwo> call = apiInterface.bookingDetailsDriver(map);
        call.enqueue(new Callback<BookingDetailModelTwo>() {
            @Override
            public void onResponse(Call<BookingDetailModelTwo> call, Response<BookingDetailModelTwo> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    BookingDetailModelTwo data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Request Accept or Cancel Response :" + responseString);
                    if (data.status.equals("1")) {
                        UserId = data.result.userDetails.id;
                        UserName = data.result.userDetails.firstName + " " + data.result.userDetails.lastLame;
                        UserImage = data.result.userDetails.userImage;
                        mobile = "+" + data.result.userDetails.phoneCode + data.result.userDetails.mobile;
                        binding.tvName.setText(UserName);
                        binding.tvAddress.setText(data.result.pickupAdd);
                        prelatLng = null;
                        driverStatus = data.result.status;
                        Glide.with(ArrivingDriverAct.this)
                                .load(UserImage)
                                .apply(new RequestOptions().placeholder(R.drawable.user_default))
                                .override(200, 200)
                                .into(binding.ivUserPropic);
                        PickUpLatLng = new LatLng(Double.parseDouble(data.result.pLat), Double.parseDouble(data.result.pLon));
                        DropOffLatLng = new LatLng(Double.parseDouble(data.result.dLat), Double.parseDouble(data.result.dLon));

                        if (driverStatus.equals("Arrived")) {
                            Log.e("fhgddvnvdvn",driverStatus);
                            binding.btnArrived.setVisibility(View.GONE);
                            binding.btnBegin.setVisibility(View.VISIBLE);
                            binding.tvAddress.setText(data.result.dropAdd);
                            binding.header.tvTitle.setText(getString(R.string.destination));
                            if (checkPermissions()) {
                                if (isLocationEnabled()) {
                                    setCurrentLoc();
                                } else {
                                    Toast.makeText(ArrivingDriverAct.this, "Turn on location", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);

                                }
                            } else {
                                requestPermissions();
                            }
                        } else if (driverStatus.equals("Start")) {
                            vibrator.vibrate(100);
                            binding.btnArrived.setVisibility(View.GONE);
                            binding.btnBegin.setVisibility(View.GONE);
                            binding.btnEnd.setVisibility(View.VISIBLE);
                            binding.header.tvTitle.setText(getString(R.string.destination));
                            binding.tvAddress.setText(data.result.dropAdd);
                        } else if (driverStatus.equals("Completed")) {
                            vibrator.vibrate(100);
                           /* startActivity(new Intent(ArrivingDriverAct.this, PaymentSummary.class).putExtra("request_id", request_id)
                                    .putExtra("user_id", UserId).putExtra("userName", UserName)
                                    .putExtra("userImage", UserImage)
                                    .putExtra("startTime", data.result.startTime)
                                    .putExtra("endTime", data.result.endTime));
                            finish();*/
                            startActivity(new Intent(ArrivingDriverAct.this, HomeActivityDriver.class));
                            finish();
                        }





                    } else if (data.status.equals("0")) {
                        Toast.makeText(ArrivingDriverAct.this, data.message, Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BookingDetailModelTwo> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //getChatCount();
        callService();
        registerReceiver(LocationReceiver, new IntentFilter("data_update_location1"));
        registerReceiver(TripStatusReceiver, new IntentFilter("Job_Status_Action1"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(LocationReceiver);
        unregisterReceiver(TripStatusReceiver);
        stopService(new Intent(ArrivingDriverAct.this, UpdateLocationService.class));
    }


    private void callService() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                startService(new Intent(ArrivingDriverAct.this, UpdateLocationService.class));
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void DrawPolyLine() {
        DrawPollyLine.get(this).setOrigin(PickUpLatLng)
                .setDestination(DropOffLatLng).execute(new DrawPollyLine.onPolyLineResponse() {
            @Override
            public void Success(ArrayList<LatLng> latLngs) {
                mMap.clear();
                polineLanLongLine.clear();
                polineLanLongLine = latLngs;
                lineOptions = new PolylineOptions();
                lineOptions.addAll(latLngs);
                lineOptions.width(10);
                lineOptions.geodesic(true);
                lineOptions.color(R.color.btn_green_color);
                AddDefaultMarker();
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

            if (carMarker != null) carMarker.remove();
            carMarker1.position(carLatLng);
            carMarker = mMap.addMarker(carMarker1);
            prelatLng = carLatLng;

        }
    }


    private void DrawPolyLine1() {
        DrawPollyLine.get(this).setOrigin(carLatLng)
                .setDestination(PickUpLatLng).execute(new DrawPollyLine.onPolyLineResponse() {
            @Override
            public void Success(ArrayList<LatLng> latLngs) {
                mMap.clear();
                polineLanLongLine.clear();
                polineLanLongLine = latLngs;
                lineOptions = new PolylineOptions();
                lineOptions.addAll(latLngs);
                lineOptions.width(10);
                lineOptions.geodesic(true);
                lineOptions.color(R.color.btn_green_color);
                AddDefaultMarker1();
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
        if (carMarker != null) carMarker.remove();
        carMarker1.position(carLatLng);
        carMarker = mMap.addMarker(carMarker1);
        carMarker1.flat(true);
        //chkCmeraPosi();

        prelatLng = carLatLng;


    }


    private void DrawPolyLine2() {
        DrawPollyLine.get(this).setOrigin(carLatLng)
                .setDestination(DropOffLatLng).execute(new DrawPollyLine.onPolyLineResponse() {
            @Override
            public void Success(ArrayList<LatLng> latLngs) {
                mMap.clear();
                polineLanLongLine.clear();
                polineLanLongLine = latLngs;
                lineOptions = new PolylineOptions();
                lineOptions.addAll(latLngs);
                lineOptions.width(10);
                lineOptions.geodesic(true);
                lineOptions.color(R.color.btn_green_color);
                AddDefaultMarker2();
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
        }
        if (carMarker != null) carMarker.remove();
        carMarker1.position(carLatLng);
        carMarker = mMap.addMarker(carMarker1);
        carMarker1.flat(true);
        //chkCmeraPosi();
        prelatLng = carLatLng;

        // float bearing = (float) bearingBetweenLocations(prelatLng, carLatLng);
        //  rotateMarker(carMarker, bearing);

    }


    public void DriverChangeStatus(String request_id, String status) {
        DataManager.getInstance().showProgressMessage(ArrivingDriverAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("driver_id", DataManager.getInstance().getUserData(ArrivingDriverAct.this).result.id);
        map.put("request_id", request_id);
        map.put("status", status);
        map.put("type", "User");
        Log.e(TAG, "Request Driver " + status + " :" + map);
        Call<BookingDetailModel> call = apiInterface.acceptCancelRequest(map);
        call.enqueue(new Callback<BookingDetailModel>() {
            @Override
            public void onResponse(Call<BookingDetailModel> call, Response<BookingDetailModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    BookingDetailModel data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Request Accept or Cancel Response :" + responseString);
                    if (data.status.equals("1")) {
                        driverStatus = data.result.status;
                        if (driverStatus.equals("Arrive")) {
                            binding.btnArrived.setVisibility(View.GONE);
                            binding.btnBegin.setVisibility(View.VISIBLE);
                            binding.tvAddress.setText(data.result.dropAdd);
                            binding.header.tvTitle.setText(getString(R.string.destination));
                            if (checkPermissions()) {
                                if (isLocationEnabled()) {
                                    setCurrentLoc();
                                } else {
                                    Toast.makeText(ArrivingDriverAct.this, "Turn on location", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);

                                }
                            } else {
                                requestPermissions();
                            }
                        } else if (driverStatus.equals("Start")) {
                            vibrator.vibrate(100);
                            binding.btnArrived.setVisibility(View.GONE);
                            binding.btnBegin.setVisibility(View.GONE);
                            binding.btnEnd.setVisibility(View.VISIBLE);
                            binding.header.tvTitle.setText(getString(R.string.destination));
                            binding.tvAddress.setText(data.result.dropAdd);
                        } else if (driverStatus.equals("Completed")) {
                            vibrator.vibrate(100);
                            /*startActivity(new Intent(ArrivingDriverAct.this, PaymentSummary.class).putExtra("request_id", request_id)
                                    .putExtra("user_id", UserId).putExtra("userName", UserName)
                                    .putExtra("userImage", UserImage)
                                    .putExtra("startTime", data.result.startTime)
                                    .putExtra("endTime", data.result.endTime));
                            finish();*/
                            startActivity(new Intent(ArrivingDriverAct.this, HomeActivityDriver.class));
                            finish();
                        }
                    } else if (data.status.equals("0")) {
                        Toast.makeText(ArrivingDriverAct.this, data.message, Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BookingDetailModel> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    public String getAddress(Context context, double latitude, double longitute) {
        List<Address> addresses;
        String addressStreet = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {

            addresses = geocoder.getFromLocation(latitude, longitute, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addressStreet = addresses.get(0).getAddressLine(0);
            //address2 = addresses.get(0).getAddressLine(1); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String region = addresses.get(0).getAdminArea();
            Log.e("addressStreet====", addressStreet);
            Log.e("city====", city);
            Log.e("region====", region);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressStreet;

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

}
