package com.shahenatuserapp.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.shahenatuserapp.Preference;
import com.shahenatuserapp.R;
import com.shahenatuserapp.User.adapter.AvalibilityAdapter;
import com.shahenatuserapp.User.model.EquimentModelAvalaible;
import com.shahenatuserapp.User.model.ScheduleRide;
import com.shahenatuserapp.databinding.ActivityAvailabilityBinding;
import com.shahenatuserapp.utils.RetrofitClients;
import com.shahenatuserapp.utils.SessionManager;

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


    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_availability);

        Intent intent=getIntent();

        if(intent!=null)
        {
            Type_id=intent.getStringExtra("Type_id");
            FromDate=intent.getStringExtra("FromDate");
            ToDates=intent.getStringExtra("ToDates");
            from_time=intent.getStringExtra("from_time");
            PickUp_address=intent.getStringExtra("PickUp_address");
            PicUp_latitude=intent.getStringExtra("PicUp_latitude");
            PicUp_longitude=intent.getStringExtra("PicUp_longitude");
            DropUp_address=intent.getStringExtra("DropUp_address");
            DropUp_latitude=intent.getStringExtra("DropUp_latitude");
            DropUp_longitude=intent.getStringExtra("DropUp_longitude");
            No_Vechcli=intent.getStringExtra("No_Vechcli");
        }


        binding.Imgback.setOnClickListener(v -> {

            onBackPressed();
        });

        sessionManager = new SessionManager(AvailabilityActivity.this);

        if (sessionManager.isNetworkAvailable()) {

            binding.progressBar.setVisibility(View.VISIBLE);

            getAvailableEuimentMethod();

        }else {

            Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();
        }


    }

    private void setAdapter(ArrayList<ScheduleRide.Result> modelList) {

        mAdapter = new AvalibilityAdapter(AvailabilityActivity.this,modelList);
        binding.recyclerAllCategory.setHasFixedSize(true);
        // use a linear layout manager
        binding.recyclerAllCategory.setLayoutManager(new LinearLayoutManager(AvailabilityActivity.this));
        binding.recyclerAllCategory.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener(new AvalibilityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, ScheduleRide.Result model) {

                Preference.save(AvailabilityActivity.this,Preference.KEY_avail_id,model.getId());

              startActivity(new Intent(AvailabilityActivity.this,AvaliviltyDetails.class));

            }
        });
    }


    private void getAvailableEuimentMethod(){

        String User_id=Preference.get(AvailabilityActivity.this,Preference.KEY_USER_ID);

        Call<ScheduleRide> call = RetrofitClients.getInstance().getApi()
                .get_schedule_ride(User_id,Type_id,FromDate,ToDates,from_time,"1"
                ,PickUp_address,PicUp_latitude,PicUp_longitude,DropUp_address,DropUp_latitude,DropUp_longitude);
        call.enqueue(new Callback<ScheduleRide>() {
            @Override
            public void onResponse(Call<ScheduleRide> call, Response<ScheduleRide> response) {

                binding.progressBar.setVisibility(View.GONE);

                ScheduleRide finallyPr = response.body();

                String status = finallyPr.getStatus();
                String Message = finallyPr.getMessage();

                if (status.equalsIgnoreCase("1")) {

                   modelList= (ArrayList<ScheduleRide.Result>) finallyPr.getResult();

                   setAdapter(modelList);

                } else {

                    binding.progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<ScheduleRide> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

}