package com.shahenat.Driver;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.shahenat.ChangePassAct;
import com.shahenat.R;
import com.shahenat.User.EditProfileAct;
import com.shahenat.User.model.LoginModel;
import com.shahenat.databinding.ActivityProfileBinding;
import com.shahenat.utils.DataManager;

public class DriverProfileAct extends AppCompatActivity {
    ActivityProfileBinding binding;
    LoginModel loginModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        initView();
    }

    private void initView() {
        binding.Imgback.setOnClickListener(v -> {finish();});

        binding.ivEdit.setOnClickListener(v -> {startActivity(new Intent(this, DriverEditProfileAct.class));});

        binding.btnChangePass.setOnClickListener(v -> {startActivity(new Intent(this, ChangePassAct.class));});

    }

    private void setUserInfo() {
        loginModel = new LoginModel();
        loginModel = DataManager.getInstance().getUserData(DriverProfileAct.this);
        binding.tvName.setText(loginModel.result.firstName + " " + loginModel.result.lastName);
        binding.tvMail.setText(loginModel.result.email);
        binding.tvPlace.setText("+"+loginModel.result.countryCode+ loginModel.result.mobile);
        Glide.with(DriverProfileAct.this)
                .load(loginModel.result.image)
                .centerCrop()
                .error(R.drawable.user_default)
                .override(100,100)
                .into(binding.ivUser);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserInfo();
    }
}