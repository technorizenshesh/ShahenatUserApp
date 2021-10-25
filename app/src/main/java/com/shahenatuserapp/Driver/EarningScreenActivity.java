package com.shahenatuserapp.Driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.shahenatuserapp.R;
import com.shahenatuserapp.databinding.ActivityEarningScreenBinding;

import java.util.ArrayList;

public class EarningScreenActivity extends AppCompatActivity {


    ActivityEarningScreenBinding binding;

    // variable for our bar data.
    BarData barData;
    // variable for our bar data set.
    BarDataSet barDataSet;
    // array list for storing entries.
    ArrayList barEntriesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding= DataBindingUtil.setContentView(this,R.layout.activity_earning_screen);

       binding.Imgback.setOnClickListener(v -> {

           onBackPressed();
       });

       binding.RRtoday.setOnClickListener(v -> {

           binding.txtToday.setTextColor(ContextCompat.getColor(this,R.color.purple_200));
           binding.txtWeekly.setTextColor(ContextCompat.getColor(this,R.color.gray_new));

           binding.viewColor.setBackgroundResource(R.color.purple_200);
           binding.viewColor1.setBackgroundResource(R.color.gray_light);

           binding.EarningToday.setVisibility(View.VISIBLE);
           binding.llWeekly.setVisibility(View.GONE);
       });

       binding.RRWeekly.setOnClickListener(v -> {

           binding.txtToday.setTextColor(ContextCompat.getColor(this,R.color.gray_new));
           binding.txtWeekly.setTextColor(ContextCompat.getColor(this,R.color.purple_200));

           binding.viewColor.setBackgroundResource(R.color.gray_light);
           binding.viewColor1.setBackgroundResource(R.color.purple_200);

           binding.EarningToday.setVisibility(View.GONE);
           binding.llWeekly.setVisibility(View.VISIBLE);
       });

       // calling method to get bar entries.
        getBarEntries();

        // creating a new bar data set.
        barDataSet = new BarDataSet(barEntriesArrayList, "Earnigs");

        // creating a new bar data and
        // passing our bar data set.
        barData = new BarData(barDataSet);

        // below line is to set data
        // to our bar chart.
        binding.chart.setData(barData);

        // adding color to our bar data set.
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        // setting text color.
        barDataSet.setValueTextColor(Color.BLACK);

        // setting text size
        barDataSet.setValueTextSize(16f);
        binding.chart.getDescription().setEnabled(false);


    }


    private void getBarEntries() {
        // creating a new array list
        barEntriesArrayList = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntriesArrayList.add(new BarEntry(1f, 4));
        barEntriesArrayList.add(new BarEntry(2f, 6));
        barEntriesArrayList.add(new BarEntry(3f, 8));
        barEntriesArrayList.add(new BarEntry(4f, 2));
        barEntriesArrayList.add(new BarEntry(5f, 4));
        barEntriesArrayList.add(new BarEntry(6f, 1));
    }
}