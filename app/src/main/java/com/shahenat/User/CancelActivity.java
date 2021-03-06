package com.shahenat.User;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shahenat.R;
import com.shahenat.databinding.ActivityCancelBinding;

public class CancelActivity extends AppCompatActivity {

    ActivityCancelBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_cancel);

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.RRSubmit.setOnClickListener(v -> {
            startActivity(new Intent(CancelActivity.this,HomeActiivity.class));
        });
    }
}