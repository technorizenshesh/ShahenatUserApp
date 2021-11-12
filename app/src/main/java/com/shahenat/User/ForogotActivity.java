package com.shahenat.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shahenat.R;
import com.shahenat.ResetPassAct;
import com.shahenat.databinding.ActivityForogotBinding;
import com.shahenat.retrofit.ApiClient;
import com.shahenat.retrofit.ShahenatInterface;
import com.shahenat.utils.DataManager;
import com.shahenat.utils.NetworkAvailablity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.shahenat.retrofit.Constant.emailPattern;

public class ForogotActivity extends AppCompatActivity {
    public String TAG = "ForogotActivity";
    ActivityForogotBinding binding;
    ShahenatInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(ShahenatInterface.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forogot);
        initView();

    }


    private void initView() {

        binding.Imgback.setOnClickListener(v -> {
            finish();
        });

        binding.btnSend.setOnClickListener(v -> {
            validation();
        });
    }

    private void validation() {
        if (binding.etEmail.getText().toString().equals("")) {
            binding.etEmail.setError(getString(R.string.please_enter_email));
            binding.etEmail.setFocusable(true);
        } else if (!binding.etEmail.getText().toString().matches(emailPattern)) {
            binding.etEmail.setError(getString(R.string.wrong_email));
            binding.etEmail.setFocusable(true);
        } else {
            if (NetworkAvailablity.checkNetworkStatus(ForogotActivity.this)) forGotPass();
            else
                Toast.makeText(this, getString(R.string.checkInternet), Toast.LENGTH_SHORT).show();
        }
    }


    private void forGotPass() {
        DataManager.getInstance().showProgressMessage(ForogotActivity.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("email", binding.etEmail.getText().toString());
        Log.e(TAG, "ForgotPass Request :" + map.toString());
        Call<Map<String, String>> signupCall = apiInterface.forgotPass(map);
        signupCall.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String, String> data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "ForgotPass Response :" + responseString);
                    if (data.get("status").equals("1")) {
                        Toast.makeText(ForogotActivity.this, getString(R.string.please_check_your_email_id), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForogotActivity.this, ResetPassAct.class)
                                .putExtra("email", binding.etEmail.getText().toString())
                                .putExtra("otp", data.get("otp")+""));
                    } else if (data.get("status").equals("0")) {
                        Toast.makeText(ForogotActivity.this, getString(R.string.wrong_email_id), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                call.cancel();
            }
        });

    }
}