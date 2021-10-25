package com.shahenatuserapp.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.shahenatuserapp.ChosseLoginActivity;
import com.shahenatuserapp.R;
import com.shahenatuserapp.databinding.ActivityLoginBinding;

public class LoginAct extends AppCompatActivity {

    ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_login);

        binding.RRLoogin.setOnClickListener(v -> {

            startActivity(new Intent(LoginAct.this,LoginActivity.class));

        });
    }
}