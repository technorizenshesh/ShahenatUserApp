package com.shahenatuserapp.User;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.shahenatuserapp.Driver.HomeActivityDriver;
import com.shahenatuserapp.Driver.SignUpActivityDriver;
import com.shahenatuserapp.GPSTracker;
import com.shahenatuserapp.Preference;
import com.shahenatuserapp.R;
import com.shahenatuserapp.User.model.LoginModel;
import com.shahenatuserapp.databinding.ActivityLogin2Binding;
import com.shahenatuserapp.utils.RetrofitClients;
import com.shahenatuserapp.utils.SessionManager;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    ActivityLogin2Binding binding;

    private SessionManager sessionManager;

    String Email="";
    String Password ="";
    GPSTracker gpsTracker;
    String latitude="";
    String longitude="";

    //Google SignIn
    private RelativeLayout RR_faceBook_login;
    private RelativeLayout RR_google_login;
    private SignInButton signInButton;
    FirebaseAuth mAuth;
    private final static int RC_SIGN_IN = 1;
    private GoogleApiClient googleApiClient;
    private static final String TAG = "fireBaseToken";
    String token="";
    String Type="";

    String Socilal_FirstName="";
    String Socilal_last_name="";
    String Socilal_email="";
    String Socilal_mobile="";
    String Socilal_city="";
    String Socilal_address="";
    String Socilal_address2="";
    String Socilal_type="";
    String social_id="";
    String social_image="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login2);

         Type= Preference.get(LoginActivity.this,Preference.KEY_Login_type);

        if(Type.equalsIgnoreCase("Driver"))
        {
             binding.llSocialLogin.setVisibility(View.GONE);
             binding.txtSocila.setVisibility(View.GONE);

        }else
        {
            binding.llSocialLogin.setVisibility(View.VISIBLE);
            binding.txtSocila.setVisibility(View.VISIBLE);
        }

        binding.txtLogin.setText(Type+" Login");

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(runnable -> {
            token = runnable.getToken();
            Log.e( "Tokennnn" ,token);
        });

        //Gps Lat Long
        gpsTracker = new GPSTracker(LoginActivity.this);
        if (gpsTracker.canGetLocation()) {

            latitude = String.valueOf(gpsTracker.getLatitude());
            longitude = String.valueOf(gpsTracker.getLongitude());

        } else {
            gpsTracker.showSettingsAlert();
        }

        //Google SignIn
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        sessionManager = new SessionManager(LoginActivity.this);



         setUp();

    }

    private void setUp() {

        binding.imgGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });

        binding.llForogot.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForogotActivity.class));
        });

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.llsingUp.setOnClickListener(v -> {

            if(Type.equalsIgnoreCase("Driver"))
            {
                startActivity(new Intent(LoginActivity.this, SignUpActivityDriver.class));

            }else
            {
                startActivity(new Intent(LoginActivity.this, SignUpActivityUser.class));
            }
        });

        binding.loginID.setOnClickListener(v -> {

            if(Type.equalsIgnoreCase("Driver"))
            {
                Validation();

            }else
            {
                Validation();
            }
        });
    }


    //Google Login
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent( data );
            handleSignInResult( result );
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {

            GoogleSignInAccount account = result.getSignInAccount();

             Socilal_FirstName=account.getDisplayName();
             Socilal_last_name="";
             Socilal_email=account.getEmail();
             Socilal_mobile="";
             Socilal_city="";
             Socilal_address="";
             Socilal_address2="";
             Socilal_type="";
             social_id=account.getId();

             social_image= String.valueOf(account.getPhotoUrl());

            if (sessionManager.isNetworkAvailable()) {

                binding.progressBar.setVisibility(View.VISIBLE);

                ApISignUpMehod("USER");

            }else {

                Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();
            }

        } else {

            Toast.makeText( this, "Login Unsuccessful", Toast.LENGTH_SHORT ).show();
        }
    }



    private void Validation() {

         Email = binding.edtEmail.getText().toString();
         Password =  binding.edtPassword.getText().toString();

        if(Email.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();

        }else if(Password.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();

        }else
        {
            if (sessionManager.isNetworkAvailable()) {

                binding.progressBar.setVisibility(View.VISIBLE);

                loginMethod();

            }else {
                Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginMethod(){

        Call<LoginModel> call = RetrofitClients.getInstance().getApi()
                .login(Email,Password,latitude,longitude,token);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {

                binding.progressBar.setVisibility(View.GONE);

                LoginModel finallyPr = response.body();

                String status = finallyPr.status;

                if (status.equalsIgnoreCase("1")) {

                    Preference.save(LoginActivity.this,Preference.KEY_USER_ID,finallyPr.result.id);

                    if(finallyPr.result.type.equalsIgnoreCase("USER"))
                    {

                        Preference.save(LoginActivity.this,Preference.KEY_User_name,finallyPr.result.firstName);
                        Preference.save(LoginActivity.this,Preference.KEY_User_email,finallyPr.result.email);
                        Preference.save(LoginActivity.this,Preference.KEY_USer_img,finallyPr.result.image);


                        startActivity(new Intent(LoginActivity.this, HomeActiivity.class));

                    }else
                    {
                        Preference.save(LoginActivity.this,Preference.KEY_User_name,finallyPr.result.firstName);
                        Preference.save(LoginActivity.this,Preference.KEY_User_email,finallyPr.result.email);
                        Preference.save(LoginActivity.this,Preference.KEY_USer_img,finallyPr.result.image);

                        startActivity(new Intent(LoginActivity.this, HomeActivityDriver.class));
                    }

                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, finallyPr.message, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }



    private void ApISignUpMehod(String LoginType){

        Call<LoginModel> call = RetrofitClients
                .getInstance()
                .getApi()
                .social_login(Socilal_FirstName,Socilal_last_name,Socilal_email,Socilal_mobile,Socilal_city,Socilal_address,Socilal_address2,token,latitude,longitude,LoginType,social_id,social_image);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {

                binding.progressBar.setVisibility(View.GONE);

                LoginModel finallyPr = response.body();

                String status = finallyPr.status;

                if (status.equalsIgnoreCase("1")) {

                    Preference.save(LoginActivity.this,Preference.KEY_USER_ID,finallyPr.result.id);

                    if(finallyPr.result.type.equalsIgnoreCase("USER"))
                    {

                        Preference.save(LoginActivity.this,Preference.KEY_User_name,finallyPr.result.firstName);
                        Preference.save(LoginActivity.this,Preference.KEY_User_email,finallyPr.result.email);
                        Preference.save(LoginActivity.this,Preference.KEY_USer_img,finallyPr.result.image);


                        startActivity(new Intent(LoginActivity.this, HomeActiivity.class));

                    }else
                    {
                        Preference.save(LoginActivity.this,Preference.KEY_User_name,finallyPr.result.firstName);
                        Preference.save(LoginActivity.this,Preference.KEY_User_email,finallyPr.result.email);
                        Preference.save(LoginActivity.this,Preference.KEY_USer_img,finallyPr.result.image);

                        startActivity(new Intent(LoginActivity.this, HomeActivityDriver.class));
                    }

                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, finallyPr.message, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {

                binding.progressBar.setVisibility(View.GONE);

                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}