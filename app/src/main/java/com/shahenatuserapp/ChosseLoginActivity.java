package com.shahenatuserapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.shahenatuserapp.User.LoginActivity;
import com.shahenatuserapp.databinding.ActivityChosseLoginBinding;

public class ChosseLoginActivity extends AppCompatActivity {

    ActivityChosseLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_chosse_login);

        binding.RRUserLoogin.setOnClickListener(v -> {

            binding.RRUserLoogin.setBackgroundResource(R.drawable.btn_bg);
            binding.RRDriverLoogin.setBackgroundResource(R.drawable.btn_bg_white);

            binding.txtUser.setTextColor(getResources().getColor(R.color.white));
            binding.txtDriver.setTextColor(getResources().getColor(R.color.black_new));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Preference.save(ChosseLoginActivity.this,Preference.KEY_Login_type,"User");

                    startActivity(new Intent(ChosseLoginActivity.this,LoginActivity.class));

                }
            }, 1000);

        });

        binding.RRDriverLoogin.setOnClickListener(v -> {

            binding.RRUserLoogin.setBackgroundResource(R.drawable.btn_bg_white);
            binding.RRDriverLoogin.setBackgroundResource(R.drawable.btn_bg);

            binding.txtUser.setTextColor(getResources().getColor(R.color.black_new));
            binding.txtDriver.setTextColor(getResources().getColor(R.color.white));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Preference.save(ChosseLoginActivity.this,Preference.KEY_Login_type,"Driver");

                    startActivity(new Intent(ChosseLoginActivity.this, LoginActivity.class));

                }
            }, 1000);

        });
    }
}