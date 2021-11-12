package com.shahenat.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.shahenat.Driver.HomeActivityDriver;
import com.shahenat.R;
import com.shahenat.databinding.ActivityVerificationBinding;
import com.shahenat.retrofit.Constant;
import com.shahenat.utils.SessionManager;

public class VerificationActivity extends AppCompatActivity {

    ActivityVerificationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_verification);

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.loginID.setOnClickListener(v -> {

           String Type = SessionManager.readString(VerificationActivity.this, Constant.KEY_Login_type,"");

           if(Type.equalsIgnoreCase("User"))
           {
               startActivity(new Intent(VerificationActivity.this,HomeActiivity.class));

           }else
           {
               startActivity(new Intent(VerificationActivity.this, HomeActivityDriver.class));

           }
        });

    }
}