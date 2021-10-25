package com.shahenatuserapp.Driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.shahenatuserapp.R;
import com.shahenatuserapp.databinding.ActivityPurchasePlanBinding;

public class PurchesHistorySceen extends AppCompatActivity {

    ActivityPurchasePlanBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_purchase_plan);

        binding.ivBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

}