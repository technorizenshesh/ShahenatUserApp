package com.shahenatuserapp.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.shahenatuserapp.Driver.AddDetailTructEqiment;
import com.shahenatuserapp.Driver.Model.EquimentModel;
import com.shahenatuserapp.Driver.Model.EquimentModelData;
import com.shahenatuserapp.Driver.adapter.EquimentSpinnerAdapter;
import com.shahenatuserapp.R;
import com.shahenatuserapp.databinding.ActivityScheduleRideBinding;
import com.shahenatuserapp.utils.RetrofitClients;
import com.shahenatuserapp.utils.SessionManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleRideActivity extends AppCompatActivity {

    ActivityScheduleRideBinding binding;
    public ArrayList<EquimentModelData> modelist = new ArrayList<>();
    SessionManager sessionManager;

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
}