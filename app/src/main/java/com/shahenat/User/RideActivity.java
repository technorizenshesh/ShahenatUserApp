package com.shahenat.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.shahenat.GPSTracker;
import com.shahenat.MapRelated.DrawPollyLine;
import com.shahenat.R;
import com.shahenat.RideClickListener;
import com.shahenat.User.adapter.NearByAvaiableAdapter;
import com.shahenat.User.model.BookingDetailModel;
import com.shahenat.User.model.GetPriceModel;
import com.shahenat.User.model.GetPriceModelData;
import com.shahenat.User.model.SameDayBookingModel;
import com.shahenat.databinding.ActivityRideBinding;
import com.shahenat.retrofit.ApiClient;
import com.shahenat.retrofit.ShahenatInterface;
import com.shahenat.utils.DataManager;
import com.shahenat.utils.NetworkAvailablity;
import com.shahenat.utils.SessionManager;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;

public class RideActivity extends AppCompatActivity implements OnMapReadyCallback, RideClickListener {
    public static String TAG = "RideActivity";
    ActivityRideBinding binding;
    public static Context context;
    public static RippleBackground rippleBackground;
    GPSTracker gpsTracker;
    private GoogleMap mMap;
    private ArrayList<GetPriceModelData> modelList = new ArrayList<>();
    NearByAvaiableAdapter mAdapter;

    private View promptsView;
    private AlertDialog alertDialog;
    LatLng PickUpLatLng, DropOffLatLng;
    MarkerOptions PicUpMarker, DropOffMarker, carMarker1;
    public static boolean run = true;
    private PolylineOptions lineOptions;
    public static double latitude = 0.0,longitude = 0.0, PicUp_latitude = 0.0,PicUp_longitude = 0.0,Droplatitude = 0.0,Droplongitude = 0.0;
   public static String VechleId = "",DriverId = "",EstemateTime = "",EstematePrice = "",EstemateDistance = "",PickupAddress = "",DropAddress = "",PaymentType = "";
    int PERMISSION_ID = 44;
    ArrayList<Marker> markers = new ArrayList<Marker>();
   public static ShahenatInterface shahenatInterfaceInterface;
    public static CountDownTimer countDownTimer;
    public static String countChk111 = "false" ,request_id = "", requestStatus = "";
    public static int count = 1;


    BroadcastReceiver JobStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("request_id") != null) {
                request_id = intent.getStringExtra("request_id");
                requestStatus = intent.getStringExtra("status");
                if (intent.getStringExtra("status").equals("Accept")) {

                    rippleBackground.stopRippleAnimation();
                    // run = false;
                    countDownTimer.onFinish();
                    context.startActivity(new Intent(context, ArrivingActivity.class));
                    ((Activity) context).finish();
                }
                /*else if(intent.getStringExtra("status").equals("Cancel")){
                    rippleBackground.stopRippleAnimation();
                    countDownTimer.cancel();
                    context.startActivity(new Intent(context, HomeAct.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    ((Activity) context).finish();
                }*/
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = RideActivity.this;
        shahenatInterfaceInterface = ApiClient.getClient().create(ShahenatInterface.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride);
        Intent intent1 = getIntent();
        if (intent1 != null) {
            PicUp_latitude = Double.parseDouble(intent1.getStringExtra("PickUpLat").toString());
            PicUp_longitude = Double.parseDouble(intent1.getStringExtra("PickUpLon").toString());
            Droplatitude = Double.parseDouble(intent1.getStringExtra("DropLat").toString());
            Droplongitude = Double.parseDouble(intent1.getStringExtra("DropLon").toString());

            PickupAddress = intent1.getStringExtra("PickupAddress").toString();
            DropAddress = intent1.getStringExtra("DropAddress").toString();

            Log.e("PicKUpLat-----", PicUp_latitude + "");
            Log.e("PicKUpLong--------", PicUp_longitude + "");

            Log.e("DropLat-----", Droplatitude + "");
            Log.e("DropLong--------", Droplongitude + "");

            if (NetworkAvailablity.checkNetworkStatus(RideActivity.this)) getNearestDriversMethod();
            else Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();


        }

        binding.Imgback.setOnClickListener(v -> {

            onBackPressed();

        });

        binding.RRBook.setOnClickListener(v -> {

            if (PaymentType.equalsIgnoreCase("")) {
                Toast.makeText(RideActivity.this, "Please Check Payment Type", Toast.LENGTH_SHORT).show();

            } else {

                if (NetworkAvailablity.checkNetworkStatus(RideActivity.this)) BookingRideMethod();
                else Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();


            }
            //
        });


        binding.RadioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
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

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                Setup();
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }

    }

    private void Setup() {

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

        PickUpLatLng = new LatLng(PicUp_latitude, PicUp_longitude);

        DropOffLatLng = new LatLng(Droplatitude, Droplongitude);
        // DropOffLatLng = new LatLng(23.2599, 77.4126);

        PicUpMarker = new MarkerOptions().title("Pick Up Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon));


        DropOffMarker = new MarkerOptions().title("Drop Off Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon));

        DrawPolyLine();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        LatLng sydney = new LatLng(latitude, longitude);

    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(11).build();
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
                Setup();
            }
        }
    }


    private void setAdapter(ArrayList<GetPriceModelData> modelList) {

        mAdapter = new NearByAvaiableAdapter(RideActivity.this, modelList, RideActivity.this);
        binding.recyclerNearBy.setHasFixedSize(true);
        // use a linear layout manager
        binding.recyclerNearBy.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        binding.recyclerNearBy.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener(new NearByAvaiableAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, GetPriceModelData model) {
              DriverId = modelList.get(position).getEquipmentId();
                VechleId = modelList.get(position).getVehicleId();
            }
        });
    }




    private void DrawPolyLine() {
        DrawPollyLine.get(this).setOrigin(PickUpLatLng)
                .setDestination(DropOffLatLng).execute(new DrawPollyLine.onPolyLineResponse() {
            @Override
            public void Success(ArrayList<LatLng> latLngs) {
                mMap.clear();
                lineOptions = new PolylineOptions();
                lineOptions.addAll(latLngs);
                lineOptions.width(10);
                lineOptions.color(R.color.purple_200);
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
                markers.add(mMap.addMarker(PicUpMarker));
                //allINGoogleMap(markers);
                // animateCamera(PickUpLatLng);

            }
            if (DropOffLatLng != null) {
                DropOffMarker.position(DropOffLatLng);
                // mMap.addMarker(DropOffMarker);
                markers.add(mMap.addMarker(DropOffMarker));
            }
            allINGoogleMap(markers);
        }
    }


    private void animateCamera(@NonNull LatLng location) {

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(location)));
    }


    private void allINGoogleMap(ArrayList<Marker> markers) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 300; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()))));
        mMap.animateCamera(cu);
    }

    @Override
    public void onItemClick(int position) {

        EstemateTime = modelList.get(position).getEstimateTime().toString();
        EstematePrice = modelList.get(position).getPrice().toString();
        EstemateDistance = modelList.get(position).getDistance().toString();

        binding.txtTotalTime.setText(EstemateTime);
        binding.txtTotalAmt.setText(EstematePrice);
        binding.txtTotalDistance.setText(EstemateDistance);

    }

    private void getNearestDriversMethod() {
        DataManager.getInstance().showProgressMessage(RideActivity.this, getString(R.string.please_wait));
        String User_id = DataManager.getInstance().getUserData(RideActivity.this).result.id;
        String PicUp_lat = String.valueOf(PicUp_latitude);
        String PicUp_long = String.valueOf(PicUp_longitude);
        String Drop_lat = String.valueOf(Droplatitude);
        String Drop_long = String.valueOf(Droplongitude);

        Map<String, String> map = new HashMap<>();
        map.put("user_id", User_id);
        map.put("pickup_add", "");
        map.put("p_lat", PicUp_lat);
        map.put("p_lon", PicUp_long);
        map.put("drop_add", "");
        map.put("d_lat", Drop_lat);
        map.put("d_lon", Drop_long);
        Log.e(TAG, "GetNearBY Deriver Request :" + map);
        Call<GetPriceModel> call = shahenatInterfaceInterface.get_price_km(map);
        call.enqueue(new Callback<GetPriceModel>() {
            @Override
            public void onResponse(Call<GetPriceModel> call, Response<GetPriceModel> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    GetPriceModel finallyPr = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "GetNearBY Deriver Response :" + responseString);

                    if (finallyPr.getStatus().equalsIgnoreCase("1")) {
                        modelList.clear();
                        modelList.addAll(finallyPr.getResult());

                        // String EstTime = String.valueOf(finallyPr.getResult().get(0).getEstimateTime());

                        //  binding.txtTime.setText(EstTime+" min");


                      //  modelList = (ArrayList<GetPriceModelData>) finallyPr.getResult();

                        if (!modelList.isEmpty()) {
                            DriverId = modelList.get(0).getEquipmentId().toString();
                            VechleId = modelList.get(0).getVehicleId().toString();

                            EstemateTime = modelList.get(0).getEstimateTime().toString();
                            EstematePrice = modelList.get(0).getPrice().toString();
                            EstemateDistance = modelList.get(0).getDistance().toString();

                            binding.txtTotalTime.setText(EstemateTime);
                            binding.txtTotalAmt.setText(EstematePrice);
                            binding.txtTotalDistance.setText(EstemateDistance);
                        }

                        setAdapter(modelList);

                    } else {
                        DataManager.getInstance().hideProgressMessage();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetPriceModel> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    public static void BookingRideMethod() {
        DataManager.getInstance().showProgressMessage((Activity) context, context.getString(R.string.please_wait));
        String User_id = DataManager.getInstance().getUserData(context).result.id;
        String PicUp_lat = String.valueOf(PicUp_latitude);
        String PicUp_long = String.valueOf(PicUp_longitude);
        String Drop_lat = String.valueOf(Droplatitude);
        String Drop_long = String.valueOf(Droplongitude);

        Call<Map<String,String>> call = shahenatInterfaceInterface.same_day_booking(User_id, DriverId, VechleId,
                PickupAddress, PicUp_lat, PicUp_long,
                DropAddress, Drop_lat, Drop_long,
                EstematePrice, EstemateDistance, "Cash");
        call.enqueue(new Callback<Map<String,String>>() {
            @Override
            public void onResponse(Call<Map<String,String>> call, Response<Map<String,String>> response) {

                DataManager.getInstance().hideProgressMessage();

                try {
                    Map<String,String> finallyPr = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e(TAG, "Send New Booking Response after 40 second :" + dataResponse);
                    if (finallyPr.get("status").equalsIgnoreCase("1")) {
                       // AlertDaliogArea();
                        request_id = finallyPr.get("request_id");
                        SearchingDriverDialog();
                        //

                    } else {
                        Toast.makeText(context, finallyPr.get("message"), Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Map<String,String>> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
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

      /*  new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(RideActivity.this,ArrivingActivity.class));

            }
        }, 4000);*/

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }




    public static void SearchingDriverDialog() {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_search_for_driver);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        rippleBackground = dialog.findViewById(R.id.rippleDriver);
        ImageView imgCross = dialog.findViewById(R.id.imgCross);
        rippleBackground.startRippleAnimation();

        countDownTimer = new CountDownTimer(240000, 40000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("MilliseconTime===", millisUntilFinished + "");
               /* if (millisUntilFinished % 40000 != 0) {
                    if (countChk111.equals("false")) {
                        if (NetworkAvailablity.checkNetworkStatus(context)) {
                            Log.e("MilliseconTimePayment", countChk111 + "");
                            BookingRideMethod();
                            Log.e("MilliseconTime===", count + "");
                        } else Toast.makeText(context, context.getString(R.string.checkInternet), Toast.LENGTH_SHORT).show();

                    } else {
                        countChk111 = "false";
                        Log.e("MilliseconTimePayment", countChk111 + "");

                    }
                }*/

            }

            @Override
            public void onFinish() {
                rippleBackground.stopRippleAnimation();
                if (!request_id.equals("")) {
                    if (requestStatus.equals("Accept")) {
                        context.startActivity(new Intent(context, ArrivingActivity.class));
                        ((Activity) context).finish();
                    } else {
                        Toast.makeText(context, "No Driver Found", Toast.LENGTH_SHORT).show();
                        context.startActivity(new Intent(context, HomeActiivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        ((Activity) context).finish();
                    }
                } else {
                    Toast.makeText(context, "Driver cancel your booking request", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, HomeActiivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    ((Activity) context).finish();
                }
                dialog.dismiss();

            }
        }.start();


        imgCross.setOnClickListener(v -> {
            if (NetworkAvailablity.checkNetworkStatus(context)) userCancelBooking();
            else Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        dialog.show();
    }



    public static void userCancelBooking() {
        Map<String, String> map = new HashMap<>();
        map.put("request_id", request_id);
        map.put("status", "Cancel_by_user");
        map.put("reason", "");
        Log.e(TAG, "Cancel Request user Request :" + map);
        Call<BookingDetailModel> callNearCar = shahenatInterfaceInterface.rideUserCancel(map);
        callNearCar.enqueue(new Callback<BookingDetailModel>() {
            @Override
            public void onResponse(Call<BookingDetailModel> call, Response<BookingDetailModel> response) {

                try {
                    BookingDetailModel data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e(TAG, "Cancel Request user Request :" + dataResponse);
                    if (data.status.equals("1")) {
                        rippleBackground.stopRippleAnimation();
                        countDownTimer.cancel();
                        context.startActivity(new Intent(context, HomeActiivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        ((Activity) context).finish();

                    } else if (data.status.equals("0")) {
                        // dialog.dismiss();

                        //  App.showToast(context, data.get("message"), Toast.LENGTH_LONG);
                        context.startActivity(new Intent(context, HomeActiivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        ((Activity) context).finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BookingDetailModel> call, Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                call.cancel();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(JobStatusReceiver, new IntentFilter("Job_Status_Action"));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(JobStatusReceiver);
        if (countDownTimer != null)
            countDownTimer.cancel();

    }

}