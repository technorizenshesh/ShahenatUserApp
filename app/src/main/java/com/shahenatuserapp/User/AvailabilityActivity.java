package com.shahenatuserapp.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.shahenatuserapp.Preference;
import com.shahenatuserapp.R;
import com.shahenatuserapp.User.adapter.AvalibilityAdapter;
import com.shahenatuserapp.User.model.EquimentModelAvalaible;
import com.shahenatuserapp.databinding.ActivityAvailabilityBinding;
import com.shahenatuserapp.utils.RetrofitClients;
import com.shahenatuserapp.utils.SessionManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailabilityActivity extends AppCompatActivity {

    ActivityAvailabilityBinding binding;
    private ArrayList<EquimentModelAvalaible.Result> modelList = new ArrayList<>();
    AvalibilityAdapter mAdapter;

    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_availability);

        binding.Imgback.setOnClickListener(v -> {

            onBackPressed();
        });

        sessionManager = new SessionManager(AvailabilityActivity.this);

        if (sessionManager.isNetworkAvailable()) {

            binding.progressBar.setVisibility(View.VISIBLE);

            getAvailableEuimentMethod();

        }else {

            Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();
        }


    }

    private void setAdapter(ArrayList<EquimentModelAvalaible.Result> modelList) {

        mAdapter = new AvalibilityAdapter(AvailabilityActivity.this, this.modelList);
        binding.recyclerAllCategory.setHasFixedSize(true);
        // use a linear layout manager
        binding.recyclerAllCategory.setLayoutManager(new LinearLayoutManager(AvailabilityActivity.this));
        binding.recyclerAllCategory.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener(new AvalibilityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, EquimentModelAvalaible.Result model) {

                Preference.save(AvailabilityActivity.this,Preference.KEY_avail_id,model.getId());

              startActivity(new Intent(AvailabilityActivity.this,AvaliviltyDetails.class));

            }
        });
    }


    private void getAvailableEuimentMethod(){

        Call<EquimentModelAvalaible> call = RetrofitClients.getInstance().getApi()
                .get_avalable_equipment();
        call.enqueue(new Callback<EquimentModelAvalaible>() {
            @Override
            public void onResponse(Call<EquimentModelAvalaible> call, Response<EquimentModelAvalaible> response) {

                binding.progressBar.setVisibility(View.GONE);

                EquimentModelAvalaible finallyPr = response.body();

                String status = finallyPr.getStatus();
                String Message = finallyPr.getMessage();

                if (status.equalsIgnoreCase("1")) {

                   modelList= (ArrayList<EquimentModelAvalaible.Result>) finallyPr.getResult();

                   setAdapter(modelList);

                } else {

                    binding.progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<EquimentModelAvalaible> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

}