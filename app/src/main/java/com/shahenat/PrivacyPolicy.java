package com.shahenat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import android.widget.Toast;

import com.shahenat.R;
import com.shahenat.databinding.ActivityPrivacyPolicyBinding;
import com.shahenat.retrofit.ApiClient;
import com.shahenat.retrofit.ShahenatInterface;
import com.shahenat.utils.DataManager;
import com.shahenat.utils.NetworkAvailablity;
import com.shahenat.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrivacyPolicy extends AppCompatActivity {

    ActivityPrivacyPolicyBinding binding;
    SessionManager sessionManager;
    ShahenatInterface shahenatInterfaceInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shahenatInterfaceInterface = ApiClient.getClient().create(ShahenatInterface.class);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_privacy_policy);

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });




        if (NetworkAvailablity.checkNetworkStatus(PrivacyPolicy.this)) getPrivacyPolicy();
        else Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();


    }

    public void getPrivacyPolicy() {
        DataManager.getInstance().showProgressMessage(PrivacyPolicy.this,getString(R.string.please_wait));
        Call<PrivacyPolicyModel> call =
                shahenatInterfaceInterface.get_privacypolicy();
        call.enqueue(new Callback<PrivacyPolicyModel>() {
            @Override
            public void onResponse(Call<PrivacyPolicyModel> call, Response<PrivacyPolicyModel> response) {
                DataManager.getInstance().hideProgressMessage();

                try {


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
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

}