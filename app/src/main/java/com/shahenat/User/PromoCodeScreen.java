package com.shahenat.User;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shahenat.R;
import com.shahenat.User.adapter.PromoCodeAdapter;
import com.shahenat.User.model.CategoryModel;
import com.shahenat.databinding.ActivityPromoCodeScreenBinding;

import java.util.ArrayList;

public class PromoCodeScreen extends AppCompatActivity {

    ActivityPromoCodeScreenBinding binding;
    private ArrayList<CategoryModel> modelList = new ArrayList<>();
    PromoCodeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = DataBindingUtil.setContentView(this,R.layout.activity_promo_code_screen);

       binding.Imgback.setOnClickListener(v -> {

           onBackPressed();
       });

        setAdapter();
    }

    private void setAdapter() {

        modelList.add(new CategoryModel("Hammer",R.drawable.truck));
        modelList.add(new CategoryModel("Tipper",R.drawable.truck2));
        modelList.add(new CategoryModel("Sheol",R.drawable.truck3));
        modelList.add(new CategoryModel("Forklift",R.drawable.truck4));
        modelList.add(new CategoryModel("Steel transport",R.drawable.truck5));

        mAdapter = new PromoCodeAdapter(PromoCodeScreen.this,modelList);
        binding.recyclerApplyPromoCode.setHasFixedSize(true);
        // use a linear layout manager
        binding.recyclerApplyPromoCode.setLayoutManager(new LinearLayoutManager(PromoCodeScreen.this));
        binding.recyclerApplyPromoCode.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener(new PromoCodeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, CategoryModel model) {


            }
        });
    }
}