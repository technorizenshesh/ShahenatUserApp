package com.shahenatuserapp.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shahenatuserapp.R;
import com.shahenatuserapp.User.adapter.AvalibilityAdapter;
import com.shahenatuserapp.User.model.CategoryModel;
import com.shahenatuserapp.databinding.ActivityAvailabilityBinding;

import java.util.ArrayList;

public class AvailabilityActivity extends AppCompatActivity {

    ActivityAvailabilityBinding binding;
    private ArrayList<CategoryModel> modelList = new ArrayList<>();
    AvalibilityAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_availability);

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

        mAdapter = new AvalibilityAdapter(AvailabilityActivity.this,modelList);
        binding.recyclerAllCategory.setHasFixedSize(true);
        // use a linear layout manager
        binding.recyclerAllCategory.setLayoutManager(new LinearLayoutManager(AvailabilityActivity.this));
        binding.recyclerAllCategory.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener(new AvalibilityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, CategoryModel model) {

                startActivity(new Intent(AvailabilityActivity.this,AvaliviltyDetails.class).putExtra("ProductName",model.getName()));

            }
        });
    }
}