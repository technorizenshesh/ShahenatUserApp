package com.shahenat.Driver;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.braintreepayments.cardform.view.CardForm;
import com.shahenat.User.model.CategoryModel;
import com.shahenat.Driver.adapter.WalletPaymentHistoryAdapter;
import com.shahenat.R;
import com.shahenat.databinding.ActivityWalletScreenBinding;

import java.util.ArrayList;

public class WalletScreen extends AppCompatActivity {

    ActivityWalletScreenBinding binding;
    private ArrayList<CategoryModel> modelList = new ArrayList<>();
    WalletPaymentHistoryAdapter mAdapter;

    private View promptsView;
    private AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_wallet_screen);

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });

      /*  binding.txtAdd.setOnClickListener(v -> {
            startActivity(new Intent(WalletScreen.this,AddWalletRecharge.class));
        });*/

        setAdapter();

        setUp();

    }

    private void setAdapter() {

        this.modelList.add(new CategoryModel("Hammer",R.drawable.truck));
        this.modelList.add(new CategoryModel("Tipper",R.drawable.truck2));
        this.modelList.add(new CategoryModel("Sheol",R.drawable.truck3));
        this.modelList.add(new CategoryModel("Forklift",R.drawable.truck4));
        this.modelList.add(new CategoryModel("Steel transport",R.drawable.truck5));

        mAdapter = new WalletPaymentHistoryAdapter(WalletScreen.this,modelList);
        binding.recyclerPaymentHistory.setHasFixedSize(true);
        // use a linear layout manager
        binding.recyclerPaymentHistory.setLayoutManager(new LinearLayoutManager(WalletScreen.this));
        binding.recyclerPaymentHistory.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener(new WalletPaymentHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, CategoryModel model) {


            }
        });
    }

    private void setUp() {

        binding.txtAdd.setOnClickListener(v -> {
            AlertDaliogRecharge();
        });
    }


    private void AlertDaliogRecharge() {

        LayoutInflater li;
        ImageView ivBack;
        CardForm cardForm;
        RelativeLayout RRDone;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(WalletScreen.this);
        promptsView = li.inflate(R.layout.activity_stripe_payment, null);
        ivBack = (ImageView) promptsView.findViewById(R.id.ivBack);
        cardForm = (CardForm) promptsView.findViewById(R.id.card_form);
        RRDone = (RelativeLayout) promptsView.findViewById(R.id.RRDone);

        alertDialogBuilder = new AlertDialog.Builder(WalletScreen.this, R.style.myFullscreenAlertDialogStyle);   //second argument

        alertDialogBuilder.setView(promptsView);

        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                //.mobileNumberExplanation("SMS is required on this number")
                .setup(WalletScreen .this);

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