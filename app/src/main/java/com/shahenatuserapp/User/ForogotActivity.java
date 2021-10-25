package com.shahenatuserapp.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.shahenatuserapp.R;
import com.shahenatuserapp.databinding.ActivityForogotBinding;

public class ForogotActivity extends AppCompatActivity {

    ActivityForogotBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_forogot);

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });

    }
}