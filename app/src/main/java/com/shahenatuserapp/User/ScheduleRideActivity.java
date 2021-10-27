package com.shahenatuserapp.User;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.shahenatuserapp.Driver.AddDetailTructEqiment;
import com.shahenatuserapp.Driver.Model.EquimentModel;
import com.shahenatuserapp.Driver.Model.EquimentModelData;
import com.shahenatuserapp.Driver.adapter.EquimentSpinnerAdapter;
import com.shahenatuserapp.R;
import com.shahenatuserapp.databinding.ActivityScheduleRideBinding;
import com.shahenatuserapp.utils.RetrofitClients;
import com.shahenatuserapp.utils.SessionManager;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleRideActivity extends AppCompatActivity{

    ActivityScheduleRideBinding binding;
    public ArrayList<EquimentModelData> modelist = new ArrayList<>();
    SessionManager sessionManager;
    String dob="";

    int AUTOCOMPLETE_REQUEST_CODE_ADDRESS = 101;
    int AUTOCOMPLETE_REQUEST_CODE_ADDRESS1 = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_schedule_ride);

        sessionManager = new SessionManager(ScheduleRideActivity.this);

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.txtNext.setOnClickListener(v -> {

           startActivity(new Intent(ScheduleRideActivity.this,AvailabilityActivity.class));

        });

        if (!Places.isInitialized()) {
            Places.initialize(ScheduleRideActivity.this, getString(R.string.place_api_key));
        }


        binding.RRpickUpAddress.setOnClickListener(v -> {

            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(ScheduleRideActivity.this);

            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_ADDRESS);

        });

        binding.RRDropUpAddress.setOnClickListener(v -> {

            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(ScheduleRideActivity.this);

            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_ADDRESS1);

        });

        binding.RRdateFrom.setOnClickListener(v -> {

            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int  mMonth = c.get(Calendar.MONTH);
            int  mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(ScheduleRideActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            view.setVisibility(View.VISIBLE);
                            dob = (dayOfMonth+"-"+(monthOfYear+1)+"-"+year);
                            binding.txtFromDate.setText(dob);
                        }
                    }, mYear, mMonth, mDay);

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

            datePickerDialog.show();

        });

        binding.RRdateto.setOnClickListener(v -> {

            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int  mMonth = c.get(Calendar.MONTH);
            int  mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(ScheduleRideActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            view.setVisibility(View.VISIBLE);
                            dob = (dayOfMonth+"-"+(monthOfYear+1)+"-"+year);
                            binding.toDate.setText(dob);
                        }
                    }, mYear, mMonth, mDay);

             datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

            datePickerDialog.show();

        });

        if (sessionManager.isNetworkAvailable()) {

            binding.progressBar.setVisibility(View.VISIBLE);

            getAllEquiment();

        } else {
            Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();
        }

    }



    public void getAllEquiment() {
        Call<EquimentModel> call = RetrofitClients
                .getInstance()
                .getApi()
                .get_equipment();
        call.enqueue(new Callback<EquimentModel>() {
            @Override
            public void onResponse(Call<EquimentModel> call, Response<EquimentModel> response) {
                try {

                    binding.progressBar.setVisibility(View.GONE);

                    EquimentModel myclass = response.body();

                    String status = myclass.getStatus();
                    String result = myclass.getMessage();

                    if (status.equalsIgnoreCase("1")) {

                        modelist = (ArrayList<EquimentModelData>) myclass.getResult();
                        EquimentSpinnerAdapter customAdapter = new EquimentSpinnerAdapter(ScheduleRideActivity.this, modelist);
                        binding.spinnerCatgory.setAdapter(customAdapter);

                    } else {
                        Toast.makeText(ScheduleRideActivity.this, result, Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<EquimentModel> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(ScheduleRideActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_ADDRESS) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                try {
                    Log.e("addressStreet====", place.getAddress());
                    String PickUp_address = place.getAddress();
                    binding.tvPickUp.setText(place.getAddress());

                 /*   PicUp_latitude = place.getLatLng().latitude;
                    PicUp_longitude = place.getLatLng().longitude;*/


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

                    String PickUp_address = place.getAddress();

                    binding.tvDropUp.setText(place.getAddress());

                  /*  Droplatitude = place.getLatLng().latitude;
                    Droplongitude = place.getLatLng().longitude;*/

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

}