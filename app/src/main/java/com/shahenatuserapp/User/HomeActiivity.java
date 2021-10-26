package com.shahenatuserapp.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.shahenatuserapp.ChosseLoginActivity;
import com.shahenatuserapp.Driver.WalletScreen;
import com.shahenatuserapp.GPSTracker;
import com.shahenatuserapp.Preference;
import com.shahenatuserapp.R;
import com.shahenatuserapp.databinding.ActivityHomeActiivityBinding;
import com.shahenatuserapp.databinding.ActivityHomeNavBinding;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeActiivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    ActivityHomeNavBinding binding;

    GPSTracker gpsTracker;
    double PicUp_latitude = 0;
    double PicUp_longitude = 0;

    double Droplatitude = 0;
    double Droplongitude = 0;

    String addressStreet = "";

    int AUTOCOMPLETE_REQUEST_CODE_ADDRESS = 101;
    int AUTOCOMPLETE_REQUEST_CODE_ADDRESS1 = 102;
    int PERMISSION_ID = 44;
    String PickUp_address="";
    String DropAddress_address="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_home_nav);

        setUp();

        String  UserName =Preference.get(HomeActiivity.this,Preference.KEY_User_name);
        String  Email =Preference.get(HomeActiivity.this,Preference.KEY_User_email);
        String  UserImg =Preference.get(HomeActiivity.this,Preference.KEY_USer_img);

        binding.childNavDrawer.txtUserName.setText(UserName);
        binding.childNavDrawer.txtUserEmail.setText(Email);

        if(!UserImg.equalsIgnoreCase(""))
        {
            Glide.with(this).load(UserImg).into(binding.childNavDrawer.imgUser);
        }

        if (!Places.isInitialized()) {
            Places.initialize(HomeActiivity.this, getString(R.string.place_api_key));
        }

        binding.dashboard.tvPickUp.setOnClickListener(v -> {

            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(HomeActiivity.this);

            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_ADDRESS);

        });

        binding.dashboard.tvDropUp.setOnClickListener(v -> {

            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(HomeActiivity.this);

            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_ADDRESS1);

        });

        //Gps Lat Long
        gpsTracker = new GPSTracker(HomeActiivity.this);
        if (gpsTracker.canGetLocation()) {
            PicUp_latitude = gpsTracker.getLatitude();
            PicUp_longitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingsAlert();
        }

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                setCurrentLoc();
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        } else {
            requestPermissions();
        }

    }

    private void setUp() {

        binding.dashboard.RRSchedule.setOnClickListener(v -> {
            startActivity(new Intent(HomeActiivity.this,ScheduleRideActivity.class));
        });

        binding.dashboard.RRNext.setOnClickListener(v -> {

            startActivity(new Intent(HomeActiivity.this,RideActivity.class));

        });

        binding.dashboard.imgDrawer.setOnClickListener(v -> {
            navmenu();
        });

        binding.childNavDrawer.RRPayment.setOnClickListener(v -> {
            navmenu();
            startActivity(new Intent(HomeActiivity.this,PaymentOptionActivity.class));

        });

        binding.childNavDrawer.RRRideHistory.setOnClickListener(v -> {
            navmenu();
            startActivity(new Intent(HomeActiivity.this,RideHistoryActivity.class));

        });

        binding.childNavDrawer.RRSignOut.setOnClickListener(v -> {
            navmenu();
            Preference.clearPreference(this);
            startActivity(new Intent(HomeActiivity.this, ChosseLoginActivity.class));
            finish();

        });

        binding.childNavDrawer.RRSupport.setOnClickListener(v -> {
            navmenu();
            startActivity(new Intent(HomeActiivity.this,SupportActivity.class));

        });

        binding.childNavDrawer.RRPromocode.setOnClickListener(v -> {
            navmenu();
            startActivity(new Intent(HomeActiivity.this,PromoCodeScreen.class));

        });
           binding.childNavDrawer.RRwallet.setOnClickListener(v -> {
            navmenu();
            startActivity(new Intent(HomeActiivity.this, WalletUserScreen.class));

        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.clear();
        LatLng sydney = new LatLng(PicUp_latitude, PicUp_longitude);

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

    public void navmenu() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);
        } else {
            binding.drawer.openDrawer(GravityCompat.START);
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
        gpsTracker = new GPSTracker(HomeActiivity.this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String loc = "";

        if (gpsTracker.canGetLocation()) {
            loc = getAddress(HomeActiivity.this, gpsTracker.getLatitude(), gpsTracker.getLongitude());
        }

        Log.e("Location=====", loc);

        Log.e("Location====","Latitude=== :"+gpsTracker.getLatitude() + "  " + "Longitute=== : " + gpsTracker.getLongitude());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_ADDRESS) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                try {
                    Log.e("addressStreet====", place.getAddress());
                     PickUp_address = place.getAddress();
                    binding.dashboard.tvPickUp.setText(place.getAddress());
                    PicUp_latitude = place.getLatLng().latitude;
                    PicUp_longitude = place.getLatLng().longitude;
                    mMap.clear();

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(PicUp_latitude, PicUp_longitude))
                            .title("Marker in Location"));

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(new LatLng(PicUp_latitude, PicUp_longitude))));

                } catch (Exception e) {
                    e.printStackTrace();
                    //setMarker(latLng);
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            }

        }else if (requestCode == AUTOCOMPLETE_REQUEST_CODE_ADDRESS1) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                try {
                    
                    Log.e("addressStreet====", place.getAddress());
                    DropAddress_address = place.getAddress();
                    binding.dashboard.tvDropUp.setText(place.getAddress());
                    Droplatitude = place.getLatLng().latitude;
                    Droplongitude = place.getLatLng().longitude;
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Droplatitude, Droplongitude))
                            .title("Marker in Location"));

                 mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(new LatLng(Droplatitude, Droplongitude))));

                } catch (Exception e) {
                    e.printStackTrace();
                    //setMarker(latLng);
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            }
        }
    }


    public String getAddress(Context context, double latitude, double longitute) {
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitute, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addressStreet = addresses.get(0).getAddressLine(0);
            // address2 = addresses.get(0).getAddressLine(1); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            //  city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String region = addresses.get(0).getAdminArea();

         //   Preference.save(getActivity(), Preference.KEY_address, addressStreet);

            binding.dashboard.tvPickUp.setText(addressStreet + "");

            Log.e("region====", region);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressStreet;
    }

}

