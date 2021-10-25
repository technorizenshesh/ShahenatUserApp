package com.shahenatuserapp.Driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.shahenatuserapp.R;
import com.shahenatuserapp.databinding.ActivityAddDetailTructEqimentBinding;

public class AddDetailTructEqiment extends AppCompatActivity {

    ActivityAddDetailTructEqimentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_add_detail_truct_eqiment);

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.txtSignUp.setOnClickListener(v -> {
            startActivity(new Intent(AddDetailTructEqiment.this, HomeActivityDriver.class));

        });
    }
}