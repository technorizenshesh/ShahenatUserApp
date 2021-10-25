package com.shahenatuserapp.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shahenatuserapp.R;
import com.shahenatuserapp.User.adapter.AvalibilityAdapter;
import com.shahenatuserapp.User.adapter.RideHistoryAdapter;
import com.shahenatuserapp.User.model.CategoryModel;
import com.shahenatuserapp.databinding.ActivityRideHistoryBinding;

import java.util.ArrayList;

public class RideHistoryActivity extends AppCompatActivity {

    ActivityRideHistoryBinding binding;
    private ArrayList<CategoryModel> modelList = new ArrayList<>();
    RideHistoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_ride_history);

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });

        setAdapter();

    }

    private void setAdapter() {

        this.modelList.add(new CategoryModel("Hammer",R.drawable.truck));
        this.modelList.add(new CategoryModel("Tipper",R.drawable.truck2));
        this.modelList.add(new CategoryModel("Sheol",R.drawable.truck3));
        this.modelList.add(new CategoryModel("Forklift",R.drawable.truck4));
        this.modelList.add(new CategoryModel("Steel transport",R.drawable.truck5));

        mAdapter = new RideHistoryAdapter(RideHistoryActivity.this,modelList);
        binding.recyclerRideHistory.setHasFixedSize(true);
        // use a linear layout manager
        binding.recyclerRideHistory.setLayoutManager(new LinearLayoutManager(RideHistoryActivity.this));
        binding.recyclerRideHistory.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener(new RideHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, CategoryModel model) {


            }
        });
    }
}