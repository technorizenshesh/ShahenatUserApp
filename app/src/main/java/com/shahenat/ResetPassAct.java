package com.shahenat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.shahenat.databinding.ActivityResetPassBinding;
import com.shahenat.retrofit.ApiClient;
import com.shahenat.retrofit.ShahenatInterface;
import com.shahenat.utils.DataManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassAct extends AppCompatActivity {
    public String TAG ="ResetPassAct";
    ActivityResetPassBinding binding;
    ShahenatInterface apiInterface;
    String email="",otp="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(ShahenatInterface.class);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_reset_pass);
        initViews();
    }

    private void initViews() {

        if(getIntent()!=null){
           email =  getIntent().getStringExtra("email");
            otp =  getIntent().getStringExtra("otp");
        }

        binding.Imgback.setOnClickListener(v -> {finish();});

        binding.btnSend.setOnClickListener(v -> {
            validation();
        });

    }

    private void validation() {
        if(binding.etOtp.getText().toString().equals("")){
            binding.etOtp.setError(getString(R.string.please_enter_otp));
            binding.etOtp.setFocusable(true);
        }
        else if(binding.etPassword.getText().toString().equals("")){
            binding.etPassword.setError(getString(R.string.please_enter_pass));
            binding.etPassword.setFocusable(true);
        }

        else  if(!binding.etOtp.getText().toString().equals(otp)){
            Toast.makeText(this, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show();
        }
        else {
            resetPass();
        }

    }

    private void resetPass() {
        DataManager.getInstance().showProgressMessage(ResetPassAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("otp", binding.etOtp.getText().toString());
        map.put("password", binding.etPassword.getText().toString());
        Log.e(TAG, "OTP SEND REQUEST" + map);
        Call<Map<String,String>> loginCall = apiInterface.resetPassword( map);
        loginCall.enqueue(new Callback<Map<String,String>>() {
            @Override
            public void onResponse(Call<Map<String,String>> call, Response<Map<String,String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String,String> data = response.body();
                    if (data.get("status").equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "OTP SEND RESPONSE" + dataResponse);
                        Toast.makeText(ResetPassAct.this, getString(R.string.password_changes_successfully), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ResetPassAct.this,ChosseLoginActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();

                    } else if (data.get("status").equals("0")) {
                        Toast.makeText(ResetPassAct.this, data.get("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onFailure(Call<Map<String,String>> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }

        });

    }

}
