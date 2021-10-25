package com.shahenatuserapp.Driver;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.shahenatuserapp.R;
import com.shahenatuserapp.User.SendFeedBackUserScree;
import com.shahenatuserapp.databinding.ActivityAcceptingBinding;

public class AcceptingActivity extends AppCompatActivity {

    ActivityAcceptingBinding binding;

    private View promptsView;
    private AlertDialog alertDialog;

    String Btn="Start";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_accepting);



        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.cardCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AcceptingActivity.this, CancelActivityDriver.class));
            }
        });

        binding.cardCall.setOnClickListener(v -> {

            AlertDaliogCall(AcceptingActivity.this);
        });

        binding.RRstart.setOnClickListener(v -> {

            if(Btn.equalsIgnoreCase("Finish"))
            {
                AlertDaliogCollectPayment(AcceptingActivity.this);

            }else
            {
                AlertDaliog();
            }


        });


    }

    private void AlertDaliogCall(Context context) {

        LayoutInflater li;
        RelativeLayout RRICall;
        RelativeLayout RRCall;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(AcceptingActivity.this);
        promptsView = li.inflate(R.layout.alert_call, null);
        RRICall = (RelativeLayout) promptsView.findViewById(R.id.RRICall);
        RRCall = (RelativeLayout) promptsView.findViewById(R.id.RRCall);
        alertDialogBuilder = new AlertDialog.Builder(AcceptingActivity.this);   //second argument

        //alertDialogBuilder = new AlertDialog.Builder(RideActivity.this);
        alertDialogBuilder.setView(promptsView);

        RRICall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phno="0789456123";

                Intent i=new Intent(Intent.ACTION_DIAL, Uri.parse(phno));
                startActivity(i);


                alertDialog.dismiss();
            }
        });

        RRCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phno="0789456123";

                Intent i=new Intent(Intent.ACTION_DIAL,Uri.parse(phno));
                startActivity(i);

                alertDialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void AlertDaliog() {

        LayoutInflater li;
        RelativeLayout RRDone;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(AcceptingActivity.this);
        promptsView = li.inflate(R.layout.alert_start, null);
        RRDone = (RelativeLayout) promptsView.findViewById(R.id.RRDone);
        alertDialogBuilder = new AlertDialog.Builder(AcceptingActivity.this);   //second argument

        alertDialogBuilder.setView(promptsView);

        RRDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Btn="Finish";

                binding.txtBtn.setText("Finish");

                alertDialog.dismiss();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    private void AlertDaliogCollectPayment(Context context) {

        LayoutInflater li;
        RelativeLayout RRPayment;
        AlertDialog.Builder alertDialogBuilder;
        li = LayoutInflater.from(AcceptingActivity.this);
        promptsView = li.inflate(R.layout.alert_collect_payment, null);
        RRPayment = (RelativeLayout) promptsView.findViewById(R.id.RRPayment);
        alertDialogBuilder = new AlertDialog.Builder(AcceptingActivity.this);   //second argument

        alertDialogBuilder.setView(promptsView);

        RRPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(context, SendFeedBackUserScree.class));
                alertDialog.dismiss();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

}