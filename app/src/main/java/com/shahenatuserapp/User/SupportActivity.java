package com.shahenatuserapp.User;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shahenatuserapp.R;
import com.shahenatuserapp.databinding.ActivitySupportBinding;

public class SupportActivity extends AppCompatActivity {

    ActivitySupportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_support);

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}