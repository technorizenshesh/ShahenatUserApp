package com.shahenatuserapp.User;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shahenatuserapp.R;
import com.shahenatuserapp.databinding.ActivityAvaliviltyDetailsBinding;

public class AvaliviltyDetails extends AppCompatActivity {

    ActivityAvaliviltyDetailsBinding binding;
    private View promptsView;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_avalivilty_details);


        Intent intent=getIntent();
        if(intent !=null)
        {
           String ProductName=intent.getStringExtra("ProductName");

           if(ProductName.equalsIgnoreCase(""))
           {
               binding.txtTool.setText(ProductName);
               binding.txtProductName.setText(ProductName);
           }
        }

       binding.Imgback.setOnClickListener(v -> {

           onBackPressed();
       });

        binding.txtNegotiate.setOnClickListener(v -> {

            AlertDaliogArea();

        });

        binding.txtNext.setOnClickListener(v -> {

            startActivity(new Intent(AvaliviltyDetails.this,ArrivingActivity.class));

        });

    }


    private void AlertDaliogArea() {

        LayoutInflater li;
        ImageView imgCross;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(AvaliviltyDetails.this);
        promptsView = li.inflate(R.layout.alert_negosiate, null);
        imgCross = (ImageView) promptsView.findViewById(R.id.imgCross);
        alertDialogBuilder = new AlertDialog.Builder(AvaliviltyDetails.this);
        alertDialogBuilder.setView(promptsView);

        imgCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

}