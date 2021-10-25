package com.shahenatuserapp.Driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.shahenatuserapp.R;
import com.shahenatuserapp.databinding.ActivityAddWalletRechargeBinding;

public class AddWalletRecharge extends AppCompatActivity {

    ActivityAddWalletRechargeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_add_wallet_recharge);

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}