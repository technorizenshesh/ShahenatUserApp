package com.shahenatuserapp.User;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shahenatuserapp.Driver.HomeActivityDriver;
import com.shahenatuserapp.R;
import com.shahenatuserapp.databinding.ActivitySendFeedBackUserScreeBinding;

public class SendFeedBackUserScree extends AppCompatActivity {

    ActivitySendFeedBackUserScreeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_send_feed_back_user_scree);

        binding.RRFeedBAck.setOnClickListener(v -> {
            startActivity(new Intent(SendFeedBackUserScree.this, HomeActivityDriver.class));
        });

        binding.RRCancel.setOnClickListener(v -> {

            startActivity(new Intent(SendFeedBackUserScree.this, HomeActivityDriver.class));

        });
    }
}