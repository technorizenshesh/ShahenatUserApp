package com.shahenatuserapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.shahenatuserapp.Driver.AddDetailTructEqiment;
import com.shahenatuserapp.Driver.Model.EquimentModel;
import com.shahenatuserapp.Driver.Model.EquimentModelData;
import com.shahenatuserapp.Driver.adapter.EquimentSpinnerAdapter;
import com.shahenatuserapp.User.LoginActivity;
import com.shahenatuserapp.databinding.ActivityPrivacyPolicyBinding;
import com.shahenatuserapp.utils.RetrofitClients;
import com.shahenatuserapp.utils.SessionManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrivacyPolicy extends AppCompatActivity {

    ActivityPrivacyPolicyBinding binding;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_privacy_policy);

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });

        sessionManager = new SessionManager(PrivacyPolicy.this);


        if (sessionManager.isNetworkAvailable()) {

            binding.progressBar.setVisibility(View.VISIBLE);

            getPrivacyPolicy();

        }else {
            Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();
        }

    }

    public void getPrivacyPolicy() {
        Call<PrivacyPolicyModel> call = RetrofitClients
                .getInstance()
                .getApi()
                .get_privacypolicy();
        call.enqueue(new Callback<PrivacyPolicyModel>() {
            @Override
            public void onResponse(Call<PrivacyPolicyModel> call, Response<PrivacyPolicyModel> response) {
                try {

                    binding.progressBar.setVisibility(View.GONE);

                    PrivacyPolicyModel myclass = response.body();

                    String status = myclass.status;
                    String result = myclass.message;

                    if (status.equalsIgnoreCase("1")) {

                        String Description= myclass.result.description;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding.txt.setText(Html.fromHtml(Description, Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            binding.txt.setText(Html.fromHtml(Description));
                        }

                    } else {
                        Toast.makeText(PrivacyPolicy.this, result, Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<PrivacyPolicyModel> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

}