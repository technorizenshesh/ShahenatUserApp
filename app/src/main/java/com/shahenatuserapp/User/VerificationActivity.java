package com.shahenatuserapp.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.shahenatuserapp.Driver.HomeActivityDriver;
import com.shahenatuserapp.Preference;
import com.shahenatuserapp.R;
import com.shahenatuserapp.databinding.ActivityVerificationBinding;

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

           String Type = Preference.get(VerificationActivity.this,Preference.KEY_Login_type);

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