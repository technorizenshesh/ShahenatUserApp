package com.shahenat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.shahenat.Driver.HomeActivityDriver;
import com.shahenat.User.HomeActiivity;
import com.shahenat.databinding.ActivityVerificationBinding;
import com.shahenat.retrofit.Constant;
import com.shahenat.utils.DataManager;
import com.shahenat.utils.SessionManager;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    ActivityVerificationBinding binding;
    private FirebaseAuth mAuth;
    String mobile="",countryCode="",id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_verification);
        initView();
    }


    private void initView() {
        mAuth = FirebaseAuth.getInstance();

        if(getIntent()!=null){
            mobile = getIntent().getStringExtra("mobile");
            countryCode = getIntent().getStringExtra("countryCode");
            mobile = "+"+countryCode + mobile;
        }

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });

          mobile = "+91" + "9755463923";

        sendVerificationCode();

        binding.btnVerify.setOnClickListener(v -> {

            if(binding.Otp.getOTP().equals("")){

            }
            else {
                DataManager.getInstance().showProgressMessage(VerificationActivity.this,getString(R.string.please_wait));
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, binding.Otp.getOTP());
                signInWithPhoneAuthCredential(credential);
            }


        });

        binding.tvResend.setOnClickListener(v -> {sendVerificationCode();});


    }


    private void sendVerificationCode() {

        binding.tvDes.setText("We have sent you an SMS on " + mobile + " with 6 digit verfication code");

        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.tvResend.setText("" + millisUntilFinished / 1000);
                binding.tvResend.setEnabled(false);
            }
            @Override
            public void onFinish() {
                binding.tvResend.setText(getString(R.string.did_not_received));
                binding.tvResend.setEnabled(true);
            }
        }.start();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile.replace(" ", ""), 60,  TimeUnit.SECONDS,  this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    // Phone number to verify
                    // Timeout duration
                    // Unit of timeout
                    // Activity (for callback binding)

                    @Override
                    public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        VerificationActivity.this.id = id;
                        DataManager.getInstance().hideProgressMessage();
                        Toast.makeText(VerificationActivity.this, "Otp send successfully.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        //   ProjectUtil.pauseProgressDialog()
                        DataManager.getInstance().hideProgressMessage();
                        Toast.makeText(VerificationActivity.this, ""+phoneAuthCredential.getSmsCode(), Toast.LENGTH_SHORT).show();
                        signInWithPhoneAuthCredential(phoneAuthCredential);

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // ProjectUtil.pauseProgressDialog();
                        DataManager.getInstance().hideProgressMessage();
                        Toast.makeText(VerificationActivity.this, "Failed"+e, Toast.LENGTH_SHORT).show();
                    }
                });        // OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // ProjectUtil.pauseProgressDialog();
                            // Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = task.getResult().getUser();

                            Toast.makeText(VerificationActivity.this, "Successs", Toast.LENGTH_SHORT).show();


                            startActivity(new Intent(VerificationActivity.this, SplashActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();


                        } else {
                            // ProjectUtil.pauseProgressDialog();
                            Toast.makeText(VerificationActivity.this, "Invalid Otp.", Toast.LENGTH_SHORT).show();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }

                        }
                    }
                });

    }
}