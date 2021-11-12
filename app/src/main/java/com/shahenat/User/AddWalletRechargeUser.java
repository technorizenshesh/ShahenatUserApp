package com.shahenat.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.shahenat.R;
import com.shahenat.databinding.ActivityAddWalletRechargeUserBinding;

public class AddWalletRechargeUser extends AppCompatActivity {

    ActivityAddWalletRechargeUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_add_wallet_recharge_user);


        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}