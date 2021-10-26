package com.shahenatuserapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import com.shahenatuserapp.databinding.ActivityTermsConditionBinding;
import com.shahenatuserapp.utils.RetrofitClients;
import com.shahenatuserapp.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TermsCondition extends AppCompatActivity {

    ActivityTermsConditionBinding binding;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_terms_condition);

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });

        sessionManager = new SessionManager(TermsCondition.this);


        if (sessionManager.isNetworkAvailable()) {

            binding.progressBar.setVisibility(View.VISIBLE);

            getTermsCondition();

        }else {
            Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();
        }

    }

    public void getTermsCondition() {
        Call<PrivacyPolicyModel> call = RetrofitClients
                .getInstance()
                .getApi()
                .get_termsconditions();
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
                        Toast.makeText(TermsCondition.this, result, Toast.LENGTH_SHORT).show();

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