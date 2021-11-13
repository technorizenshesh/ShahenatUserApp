package com.shahenat;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.shahenat.databinding.ActivityChangePassBinding;
import com.shahenat.retrofit.ApiClient;
import com.shahenat.retrofit.ShahenatInterface;
import com.shahenat.utils.DataManager;
import com.shahenat.utils.NetworkAvailablity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassAct extends AppCompatActivity {
    public String TAG ="ChangePassAct";
    ActivityChangePassBinding binding;
    ShahenatInterface apiInterface;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_change_pass);
        apiInterface = ApiClient.getClient().create(ShahenatInterface.class);
        initViews();
    }

    private void initViews() {

        binding.Imgback.setOnClickListener(v -> {finish();});

        binding.btnCahngePass.setOnClickListener(v -> {validation();});
    }

    private void validation() {
        if(binding.etNewPassword.getText().toString().equals("")){
            binding.etNewPassword.setError(getString(R.string.enter_new_password));
            binding.etNewPassword.setFocusable(true);
        }
        else if(binding.etConfirmPassword.getText().toString().equals("")){
            binding.etConfirmPassword.setError(getString(R.string.enter_confirm_password));
            binding.etConfirmPassword.setFocusable(true);
        }
        else if(!binding.etConfirmPassword.getText().toString().equals(binding.etNewPassword.getText().toString())){
            binding.etConfirmPassword.setError(getString(R.string.password_does_not_matched));
            binding.etConfirmPassword.setFocusable(true);
        }
        else {
            if (NetworkAvailablity.checkNetworkStatus(ChangePassAct.this)) ChangePassword();
            else
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }


    private void ChangePassword() {
        DataManager.getInstance().showProgressMessage(ChangePassAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", DataManager.getInstance().getUserData(ChangePassAct.this).result.id);
      //  map.put("old_password  ", binding.etNewPassword.getText().toString());
        map.put("new_password", binding.etNewPassword.getText().toString());
        Log.e(TAG, "CHANGE PASS REQUEST" + map);
        Call<Map<String,String>> loginCall = apiInterface.changePassword( map);
        loginCall.enqueue(new Callback<Map<String,String>>() {
            @Override
            public void onResponse(Call<Map<String,String>> call, Response<Map<String,String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String,String> data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e(TAG
                            , "CHANGE PASS RESPONSE" + dataResponse);
                    if (data.get("status").equals("1")) {
                        Toast.makeText(ChangePassAct.this, data.get("message"), Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (data.get("status").equals("0")) {
                        Toast.makeText(ChangePassAct.this, data.get("message"), Toast.LENGTH_SHORT).show();
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
