package com.shahenat.User;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shahenat.R;
import com.shahenat.User.adapter.AvalibilityAdapter;
import com.shahenat.User.model.ScheduleRide;
import com.shahenat.databinding.ActivityAvailabilityBinding;
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
import retrofit2.http.Field;

public class AvailabilityActivity extends AppCompatActivity {
    public String TAG = "AvailabilityActivity";
    ActivityAvailabilityBinding binding;
    private ArrayList<ScheduleRide.Result> modelList = new ArrayList<>();
    AvalibilityAdapter mAdapter;

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

    String No_Vechcli = "";
    ShahenatInterface shahenatInterfaceInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shahenatInterfaceInterface = ApiClient.getClient().create(ShahenatInterface.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_availability);

        Intent intent = getIntent();

        if (intent != null) {
            Type_id = intent.getStringExtra("Type_id");
            FromDate = intent.getStringExtra("FromDate");
            ToDates = intent.getStringExtra("ToDates");
            from_time = intent.getStringExtra("from_time");
            PickUp_address = intent.getStringExtra("PickUp_address");
            PicUp_latitude = intent.getStringExtra("PicUp_latitude");
            PicUp_longitude = intent.getStringExtra("PicUp_longitude");
            DropUp_address = intent.getStringExtra("DropUp_address");
            DropUp_latitude = intent.getStringExtra("DropUp_latitude");
            DropUp_longitude = intent.getStringExtra("DropUp_longitude");
            No_Vechcli = intent.getStringExtra("No_Vechcli");
        }


        binding.Imgback.setOnClickListener(v -> {

            onBackPressed();
        });


        if (NetworkAvailablity.checkNetworkStatus(AvailabilityActivity.this))
            getAvailableEuimentMethod();
        else Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();


    }

    private void setAdapter(ArrayList<ScheduleRide.Result> modelList) {

        mAdapter = new AvalibilityAdapter(AvailabilityActivity.this, modelList);
        binding.recyclerAllCategory.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener(new AvalibilityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, ScheduleRide.Result model) {
                SessionManager.writeString(AvailabilityActivity.this, Constant.KEY_avail_id, model.getId());
                startActivity(new Intent(AvailabilityActivity.this, AvaliviltyDetails.class));

            }
        });
    }


    private void getAvailableEuimentMethod() {
        DataManager.getInstance().showProgressMessage(AvailabilityActivity.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", DataManager.getInstance().getUserData(AvailabilityActivity.this).result.id);
        map.put("type", Type_id);
        map.put("from_date", FromDate);
        map.put("to_date", ToDates);
        map.put("from_time", from_time);
        map.put("no_vehicle", "1");
        map.put("from_address", PickUp_address);
        map.put("from_lat", PicUp_latitude);
        map.put("from_lon", PicUp_longitude);
        map.put("to_address", DropUp_address);
        map.put("to_lat", DropUp_latitude);
        map.put("to_lon", DropUp_longitude);
        Log.e(TAG, "AvailableDriver Request :" + map);
        Call<ScheduleRide> call = shahenatInterfaceInterface.get_schedule_ride(map);
        call.enqueue(new Callback<ScheduleRide>() {
            @Override
            public void onResponse(Call<ScheduleRide> call, Response<ScheduleRide> response) {
                DataManager.getInstance().hideProgressMessage();
                try {

                    ScheduleRide finallyPr = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "AvailableDriver Response :" + responseString);

                    if (finallyPr.status.equalsIgnoreCase("1")) {
                        modelList.clear();
                        modelList = (ArrayList<ScheduleRide.Result>) finallyPr.getResult();
                        setAdapter(modelList);

                    } else {
                        NoDriverAlert();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ScheduleRide> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }



    public void NoDriverAlert(){
        AlertDialog.Builder  builder1 = new AlertDialog.Builder(AvailabilityActivity.this);
        builder1.setMessage(getResources().getString(R.string.no_driver_available_in_this_area));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Back",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                       finish();
                    }
                });

  /*      builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });*/

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}