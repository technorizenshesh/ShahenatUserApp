package com.shahenat.Driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.shahenat.R;
import com.shahenat.databinding.ActivityCancelDriverBinding;

public class CancelActivityDriver extends AppCompatActivity {

    ActivityCancelDriverBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_cancel_driver);

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });
        binding.RRSubmit.setOnClickListener(v -> {
            startActivity(new Intent(CancelActivityDriver.this,HomeActivityDriver.class));
        });
    }
}