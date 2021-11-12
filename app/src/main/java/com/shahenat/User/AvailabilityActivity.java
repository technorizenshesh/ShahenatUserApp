package com.shahenat.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailabilityActivity extends AppCompatActivity {

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
        binding.recyclerAllCategory.setHasFixedSize(true);
        // use a linear layout manager
        binding.recyclerAllCategory.setLayoutManager(new LinearLayoutManager(AvailabilityActivity.this));
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
        Call<ScheduleRide> call = shahenatInterfaceInterface.get_schedule_ride(DataManager.getInstance().getUserData(AvailabilityActivity.this).result.id, Type_id, FromDate, ToDates, from_time, "1"
                , PickUp_address, PicUp_latitude, PicUp_longitude, DropUp_address, DropUp_latitude, DropUp_longitude);
        call.enqueue(new Callback<ScheduleRide>() {
            @Override
            public void onResponse(Call<ScheduleRide> call, Response<ScheduleRide> response) {
                DataManager.getInstance().hideProgressMessage();
                try {

                    ScheduleRide finallyPr = response.body();
                    String status = finallyPr.getStatus();
                    String Message = finallyPr.getMessage();

                    if (status.equalsIgnoreCase("1")) {

                        modelList = (ArrayList<ScheduleRide.Result>) finallyPr.getResult();

                        setAdapter(modelList);

                    } else {

                        DataManager.getInstance().hideProgressMessage();
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

}