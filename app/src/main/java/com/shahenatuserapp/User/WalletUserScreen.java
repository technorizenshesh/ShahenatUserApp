package com.shahenatuserapp.User;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.braintreepayments.cardform.view.CardForm;
import com.shahenatuserapp.Driver.AddWalletRecharge;
import com.shahenatuserapp.Driver.WalletScreen;
import com.shahenatuserapp.Driver.adapter.WalletPaymentHistoryAdapter;
import com.shahenatuserapp.R;
import com.shahenatuserapp.User.model.CategoryModel;
import com.shahenatuserapp.databinding.ActivityWalletUserScreenBinding;

import java.util.ArrayList;

public class WalletUserScreen extends AppCompatActivity {

    ActivityWalletUserScreenBinding binding;
    private ArrayList<CategoryModel> modelList = new ArrayList<>();
    WalletPaymentHistoryAdapter mAdapter;

    private View promptsView;
    private AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding= DataBindingUtil.setContentView(this,R.layout.activity_wallet_user_screen);

       binding.Imgback.setOnClickListener(v -> {
           onBackPressed();
       });

        binding.txtAdd.setOnClickListener(v -> {

            AlertDaliogRecharge();

        });

        setAdapter();
    }

    private void setAdapter() {

        this.modelList.add(new CategoryModel("Hammer",R.drawable.truck));
        this.modelList.add(new CategoryModel("Tipper",R.drawable.truck2));
        this.modelList.add(new CategoryModel("Sheol",R.drawable.truck3));
        this.modelList.add(new CategoryModel("Forklift",R.drawable.truck4));
        this.modelList.add(new CategoryModel("Steel transport",R.drawable.truck5));

        mAdapter = new WalletPaymentHistoryAdapter(WalletUserScreen.this,modelList);
        binding.recyclerPaymentHistory.setHasFixedSize(true);
        // use a linear layout manager
        binding.recyclerPaymentHistory.setLayoutManager(new LinearLayoutManager(WalletUserScreen.this));
        binding.recyclerPaymentHistory.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener(new WalletPaymentHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, CategoryModel model) {


            }
        });
    }


    private void AlertDaliogRecharge() {

        LayoutInflater li;
        ImageView ivBack;
        CardForm cardForm;
        RelativeLayout RRDone;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(WalletUserScreen.this);
        promptsView = li.inflate(R.layout.activity_stripe_payment, null);
        ivBack = (ImageView) promptsView.findViewById(R.id.ivBack);
        cardForm = (CardForm) promptsView.findViewById(R.id.card_form);
        RRDone = (RelativeLayout) promptsView.findViewById(R.id.RRDone);

        alertDialogBuilder = new AlertDialog.Builder(WalletUserScreen.this, R.style.myFullscreenAlertDialogStyle);   //second argument

        alertDialogBuilder.setView(promptsView);

        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                //.mobileNumberExplanation("SMS is required on this number")
                .setup(WalletUserScreen .this);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

        RRDone.setOnClickListener(v -> {
            finish();
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}