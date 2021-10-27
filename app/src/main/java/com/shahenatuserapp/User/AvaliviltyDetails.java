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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shahenatuserapp.Preference;
import com.shahenatuserapp.R;
import com.shahenatuserapp.User.model.DetailsModel;
import com.shahenatuserapp.User.model.EquimentModelAvalaible;
import com.shahenatuserapp.databinding.ActivityAvaliviltyDetailsBinding;
import com.shahenatuserapp.utils.RetrofitClients;
import com.shahenatuserapp.utils.SessionManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvaliviltyDetails extends AppCompatActivity {

    ActivityAvaliviltyDetailsBinding binding;
    private View promptsView;
    private AlertDialog alertDialog;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_avalivilty_details);


       binding.Imgback.setOnClickListener(v -> {

           onBackPressed();
       });

        binding.txtNegotiate.setOnClickListener(v -> {

            AlertDaliogArea();

        });

        binding.txtNext.setOnClickListener(v -> {

            startActivity(new Intent(AvaliviltyDetails.this,ArrivingActivity.class));

        });

        sessionManager = new SessionManager(AvaliviltyDetails.this);

        if (sessionManager.isNetworkAvailable()) {

            binding.progressBar.setVisibility(View.VISIBLE);

            getAvailableEuimentMethod();

        }else {

            Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();
        }

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

    private void getAvailableEuimentMethod(){

      String id= Preference.get(AvaliviltyDetails.this,Preference.KEY_avail_id);

        Call<DetailsModel> call = RetrofitClients.getInstance().getApi()
                .get_avalable_equipment_details(id);
        call.enqueue(new Callback<DetailsModel>() {
            @Override
            public void onResponse(Call<DetailsModel> call, Response<DetailsModel> response) {

                binding.progressBar.setVisibility(View.GONE);

                DetailsModel finallyPr = response.body();

                String status = finallyPr.getStatus();
                String Message = finallyPr.getMessage();

                if (status.equalsIgnoreCase("1")) {

                    if(finallyPr.getResult().getVehicleImage()!=null)
                    {
                        Glide.with(AvaliviltyDetails.this).load(finallyPr.getResult().getVehicleImage()).placeholder(R.drawable.buldozer)
                                .into(binding.img);
                    }

                    binding.txtEquimentName.setText(finallyPr.getResult().getEquipmentName());
                    binding.txtPrice.setText(finallyPr.getResult().getPriceKm());
                    binding.txtDescription.setText(finallyPr.getResult().getDescription());
                    binding.txtColorName.setText(finallyPr.getResult().getColor());
                    binding.txtNumberPlate.setText(finallyPr.getResult().getNumberPlate());
                    binding.txtManufacturing.setText(finallyPr.getResult().getNumberPlate());
                    binding.txtModel.setText(finallyPr.getResult().getModel());
                    binding.txtBrand.setText(finallyPr.getResult().getBrand());
                    binding.txtSize.setText(finallyPr.getResult().getSize());

                    binding.txtTool.setText(finallyPr.getResult().getEquipmentName());

                } else {

                    binding.progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<DetailsModel> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

}