package com.shahenatuserapp.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.shahenatuserapp.R;
import com.shahenatuserapp.databinding.ActivityScheduleRideBinding;

public class ScheduleRideActivity extends AppCompatActivity {

    ActivityScheduleRideBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_schedule_ride);

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.txtNext.setOnClickListener(v -> {

           startActivity(new Intent(ScheduleRideActivity.this,AvailabilityActivity.class));

        });
    }
}