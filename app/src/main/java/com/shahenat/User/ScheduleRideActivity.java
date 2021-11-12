package com.shahenat.User;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.shahenat.Driver.Model.EquimentModel;
import com.shahenat.Driver.Model.EquimentModelData;
import com.shahenat.Driver.adapter.EquimentSpinnerAdapter;
import com.shahenat.PrivacyPolicy;
import com.shahenat.R;
import com.shahenat.User.model.ScheduleRide;
import com.shahenat.databinding.ActivityScheduleRideBinding;
import com.shahenat.retrofit.ApiClient;
import com.shahenat.retrofit.ShahenatInterface;
import com.shahenat.utils.DataManager;
import com.shahenat.utils.NetworkAvailablity;
import com.shahenat.utils.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleRideActivity extends AppCompatActivity {

    ActivityScheduleRideBinding binding;
    public ArrayList<EquimentModelData> modelist = new ArrayList<>();
    SessionManager sessionManager;
    String dob = "";

    int AUTOCOMPLETE_REQUEST_CODE_ADDRESS = 101;
    int AUTOCOMPLETE_REQUEST_CODE_ADDRESS1 = 102;


    private int mYear, mMonth, mDay;
    private int mYear1, mMonth1, mDay1;
    String DateIn = "";

    String DateOut = "";
    String NewDate;
    String DateInnew;
    Calendar c1;
    Calendar c2;
    DatePickerDialog datePickerDialog;

    int Check_In_Year;
    int Check_In_month;
    int Check_In_day;


    long ConvertDate;

    boolean isSelectedFirst = false;

    boolean FromdDate = false;
    boolean ToDate = false;
    boolean isTime = false;

    String Type_id = "";
    String FromDate = "";
    String ToDates = "";
    String from_time = "";

    String PickUp_address = "";
    String PicUp_latitude = "";
    String PicUp_longitude = "";

    String DropUp_address = "";
    String DropUp_latitude = "";
    String DropUp_longitude = "";

    ShahenatInterface shahenatInterfaceInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shahenatInterfaceInterface = ApiClient.getClient().create(ShahenatInterface.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule_ride);
        c1 = Calendar.getInstance();

        binding.Imgback.setOnClickListener(v -> {

            onBackPressed();

        });


        binding.spinnerCatgory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {

                Type_id = modelist.get(pos).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        binding.txtNext.setOnClickListener(v -> {

            Valiedation();


           // startActivity(new Intent(ScheduleRideActivity.this, AvailabilityActivity.class));

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

        binding.RRtime.setOnClickListener(v -> {

            final Calendar c2 = Calendar.getInstance();
            int hour = c2.get(Calendar.HOUR_OF_DAY);
            int minute = c2.get(Calendar.MINUTE);
            // Create a new instance of TimePickerDialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleRideActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                    binding.txtTime.setText(selectedHour + ":" + selectedMinute);


                    from_time = selectedHour + ":" + selectedMinute;

                    TimeSet(timePicker, selectedHour, selectedMinute);

                    isTime = true;

                }
            }, hour, minute, false);//Yes 24 hour time
            timePickerDialog.setTitle("Select Time");
            timePickerDialog.show();

        });

        binding.RRdateFrom.setOnClickListener(v -> {

            mYear = c1.get(Calendar.YEAR);
            mMonth = c1.get(Calendar.MONTH);
            mDay = c1.get(Calendar.DAY_OF_MONTH);

            datePickerDialog = new DatePickerDialog(ScheduleRideActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            //  RR_booking_Date.setVisibility( View.VISIBLE );
                            //  txt_time.setVisibility(View.VISIBLE);
                            view.setVisibility(View.VISIBLE);
                            //String   Date = (dayOfMonth+"-"+(monthOfYear+1)+"-"+year);
                            DateIn = (year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                            String DateIn1 = (year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                            Check_In_Year = year;
                            Check_In_month = monthOfYear;
                            Check_In_day = dayOfMonth;

                            DateInnew = String.valueOf(year + monthOfYear + dayOfMonth);

                            FromDate = getDate(DateIn);

                            ConvertDate = milliseconds(DateIn1);

                            binding.txtFromDate.setText(FromDate);

                            FromdDate = true;


                            isSelectedFirst = true;

                        }
                    }, mYear, mMonth, mDay);

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

            datePickerDialog.show();
        });

        binding.RRdateto.setOnClickListener(v -> {

            if (isSelectedFirst) {
                datePickerDialog = new DatePickerDialog(ScheduleRideActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                //  RR_booking_Date.setVisibility( View.VISIBLE );
                                //  txt_time.setVisibility(View.VISIBLE);
                                view.setVisibility(View.VISIBLE);
                                //String   Date = (dayOfMonth+"-"+(monthOfYear+1)+"-"+
                                //year);
                                DateOut = (year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                                DateInnew = String.valueOf(year + monthOfYear + dayOfMonth);

                                ToDates = getDate(DateOut);

                                binding.toDate.setText(ToDates);

                                ToDate = true;

                            }
                        }, Check_In_Year, Check_In_month, Check_In_day);

                datePickerDialog.getDatePicker().setMinDate(ConvertDate);

                datePickerDialog.show();

            } else {
                binding.toDate.setText("mm/dd/yy");

                Toast.makeText(this, "Please Select From Date.", Toast.LENGTH_SHORT).show();
            }
        });

        if (NetworkAvailablity.checkNetworkStatus(ScheduleRideActivity.this)) getAllEquiment();
            else Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();


    }


    public void getAllEquiment() {
        DataManager.getInstance().showProgressMessage(ScheduleRideActivity.this,getString(R.string.please_wait));
        Call<EquimentModel> call = shahenatInterfaceInterface.get_equipment();
        call.enqueue(new Callback<EquimentModel>() {
            @Override
            public void onResponse(Call<EquimentModel> call, Response<EquimentModel> response) {
                DataManager.getInstance().hideProgressMessage();
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
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
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

                    PickUp_address = place.getAddress();

                    PicUp_latitude = String.valueOf(place.getLatLng().latitude);
                    PicUp_longitude = String.valueOf(place.getLatLng().longitude);

                    binding.tvPickUp.setText(place.getAddress());


                } catch (Exception e) {
                    e.printStackTrace();
                    //setMarker(latLng);
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            }

        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE_ADDRESS1) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                try {

                    String PickUp_address = place.getAddress();

                    DropUp_address = place.getAddress();

                    DropUp_latitude = String.valueOf(place.getLatLng().latitude);
                    DropUp_longitude = String.valueOf(place.getLatLng().longitude);

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


    public void TimeSet(TimePicker view, int hourOfDay, int minute) {

        String am_pm = "";

        Calendar datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        datetime.set(Calendar.MINUTE, minute);

        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
            am_pm = "AM";
        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
            am_pm = "PM";

        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";

        from_time = strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm;

        binding.txtTime.setText(strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm);

    }


    public long milliseconds(String date) {
        //String date_ = date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
            return timeInMilliseconds;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;
    }


    private String getDate(String Date) {
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date dateObj = null;
        try {
            dateObj = curFormater.parse(Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat postFormater = new SimpleDateFormat("dd MMM yyyy");
        String newDateStr = postFormater.format(dateObj);
        return newDateStr;
    }


    private void Valiedation() {


        if (Type_id.equalsIgnoreCase("")) {

            Toast.makeText(this, "Please Select Equiment", Toast.LENGTH_SHORT).show();

        } else if (!FromdDate) {

            Toast.makeText(this, "Please Select From Date.", Toast.LENGTH_SHORT).show();

        } else if (!ToDate) {

            Toast.makeText(this, "Pleae Select TO Date.", Toast.LENGTH_SHORT).show();

        } else if (!isTime) {

            Toast.makeText(this, "Please Select Time", Toast.LENGTH_SHORT).show();

        }else if (PickUp_address.equalsIgnoreCase("")) {

            Toast.makeText(this, "Please Select Pickup Address..", Toast.LENGTH_SHORT).show();

        }else if (DropUp_address.equalsIgnoreCase("")) {

            Toast.makeText(this, "Please Select Drop Address..", Toast.LENGTH_SHORT).show();

        }else
        {

            Intent intent=new Intent(ScheduleRideActivity.this,AvailabilityActivity.class);
            intent.putExtra("Type_id",Type_id);
            intent.putExtra("FromDate",FromDate);
            intent.putExtra("ToDates",ToDates);
            intent.putExtra("from_time",from_time);
            intent.putExtra("PickUp_address",PickUp_address);
            intent.putExtra("PicUp_latitude",PicUp_latitude);
            intent.putExtra("PicUp_longitude",PicUp_longitude);
            intent.putExtra("DropUp_address",DropUp_address);
            intent.putExtra("DropUp_latitude",DropUp_latitude);
            intent.putExtra("DropUp_longitude",DropUp_longitude);
            intent.putExtra("No_Vechcli","1");
            startActivity(intent);

        }

    }
}