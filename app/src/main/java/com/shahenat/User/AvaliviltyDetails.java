package com.shahenat.User;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.shahenat.R;
import com.shahenat.User.model.DetailsModel;
import com.shahenat.databinding.ActivityAvaliviltyDetailsBinding;
import com.shahenat.retrofit.ApiClient;
import com.shahenat.retrofit.Constant;
import com.shahenat.retrofit.ShahenatInterface;
import com.shahenat.utils.DataManager;
import com.shahenat.utils.NetworkAvailablity;
import com.shahenat.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvaliviltyDetails extends AppCompatActivity {
    public String TAG = "AvaliviltyDetails";
    ActivityAvaliviltyDetailsBinding binding;
    private View promptsView;
    private AlertDialog alertDialog;

    DetailsModel finallyPr;
    ShahenatInterface shahenatInterfaceInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shahenatInterfaceInterface = ApiClient.getClient().create(ShahenatInterface.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_avalivilty_details);


        binding.Imgback.setOnClickListener(v -> {

            onBackPressed();
        });

        binding.txtNegotiate.setOnClickListener(v -> {

            AlertDaliogArea(finallyPr);

        });

        binding.txtNext.setOnClickListener(v -> {

            startActivity(new Intent(AvaliviltyDetails.this, ArrivingActivity.class));

        });


        if (NetworkAvailablity.checkNetworkStatus(AvaliviltyDetails.this))
            getAvailableEuimentMethod();
        else Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();


    }


    private void AlertDaliogArea(DetailsModel model) {

        LayoutInflater li;
        ImageView imgCross;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(AvaliviltyDetails.this);
        promptsView = li.inflate(R.layout.alert_negosiate, null);
        imgCross = (ImageView) promptsView.findViewById(R.id.imgCross);
        TextView tvTotalAmt = (TextView) promptsView.findViewById(R.id.tvTotalAmt);
        TextView tvAmt1 = (TextView) promptsView.findViewById(R.id.tvAmt1);
        TextView tvAmt2 = (TextView) promptsView.findViewById(R.id.tvAmt2);
        TextView tvMyAmt1 = (TextView) promptsView.findViewById(R.id.tvMyAmt1);
        TextView tvMyAmt2 = (TextView) promptsView.findViewById(R.id.tvMyAmt2);

        alertDialogBuilder = new AlertDialog.Builder(AvaliviltyDetails.this);
        alertDialogBuilder.setView(promptsView);


        tvTotalAmt.setText(String.format("%.2f", Double.parseDouble(model.getResult().getPriceKm())));
        tvAmt1.setText(String.format("%.2f", Double.parseDouble(model.getResult().getPriceKm())));
        tvAmt2.setText(String.format("%.2f",Double.parseDouble(model.getResult().getPriceKm())));
        tvMyAmt1.setText(String.format("%.2f", Double.parseDouble(model.getResult().getPriceKm())));
        tvMyAmt2.setText(String.format("%.2f",Double.parseDouble(model.getResult().getPriceKm())));



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

    private void getAvailableEuimentMethod() {
        DataManager.getInstance().showProgressMessage(AvaliviltyDetails.this, getString(R.string.please_wait));
        String id = SessionManager.readString(AvaliviltyDetails.this, Constant.KEY_avail_id, "");


        Call<DetailsModel> call = shahenatInterfaceInterface.get_avalable_equipment_details(id);
        call.enqueue(new Callback<DetailsModel>() {
            @Override
            public void onResponse(Call<DetailsModel> call, Response<DetailsModel> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                     finallyPr = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Availivility Response :" + responseString);
                    if (finallyPr.getStatus().equalsIgnoreCase("1")) {

                        if (finallyPr.getResult().getVehicleImage() != null) {
                            Glide.with(AvaliviltyDetails.this).load(finallyPr.getResult().getVehicleImage()).placeholder(R.drawable.buldozer)
                                    .into(binding.img);
                        }
                        binding.txtTool.setText(finallyPr.getResult().getEquipmentName());
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
                        DataManager.getInstance().hideProgressMessage();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<DetailsModel> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

}