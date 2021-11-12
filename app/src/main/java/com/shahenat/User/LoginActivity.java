package com.shahenat.User;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.shahenat.Driver.HomeActivityDriver;
import com.shahenat.Driver.SignUpActivityDriver;
import com.shahenat.GPSTracker;
import com.shahenat.R;
import com.shahenat.User.model.LoginModel;
import com.shahenat.databinding.ActivityLogin2Binding;
import com.shahenat.retrofit.ApiClient;
import com.shahenat.retrofit.Constant;
import com.shahenat.retrofit.ShahenatInterface;
import com.shahenat.utils.DataManager;
import com.shahenat.utils.NetworkAvailablity;
import com.shahenat.utils.SessionManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LoginActivity";
    ActivityLogin2Binding binding;

    String Email = "";
    String Password = "";
    GPSTracker gpsTracker;
    String latitude = "";
    String longitude = "";

    //Google SignIn
    private RelativeLayout RR_faceBook_login;
    private RelativeLayout RR_google_login;
    private SignInButton signInButton;
    FirebaseAuth mAuth;
    private final static int RC_SIGN_IN = 1;
    private GoogleApiClient googleApiClient;

    String token = "";
    String Type = "";

    String Socilal_FirstName = "";
    String Socilal_last_name = "";
    String Socilal_email = "";
    String Socilal_mobile = "";
    String Socilal_city = "";
    String Socilal_address = "";
    String Socilal_address2 = "";
    String Socilal_type = "";
    String social_id = "";
    String social_image = "";

    //FaceBook
    CallbackManager mCallbackManager;
    LoginButton loginButton;
    ShahenatInterface shahenatInterfaceInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shahenatInterfaceInterface = ApiClient.getClient().create(ShahenatInterface.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login2);

        Type = SessionManager.readString(LoginActivity.this,Constant.KEY_Login_type,"");

        if (Type.equalsIgnoreCase("Driver")) {
            binding.llSocialLogin.setVisibility(View.GONE);
            binding.txtSocila.setVisibility(View.GONE);

        } else {
            binding.llSocialLogin.setVisibility(View.VISIBLE);
            binding.txtSocila.setVisibility(View.VISIBLE);
        }

        binding.txtLogin.setText(Type + " Login");

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(runnable -> {
            token = runnable.getToken();
            Log.e("Tokennnn", token);
        });

        //Gps Lat Long
        gpsTracker = new GPSTracker(LoginActivity.this);
        if (gpsTracker.canGetLocation()) {

            latitude = String.valueOf(gpsTracker.getLatitude());
            longitude = String.valueOf(gpsTracker.getLongitude());

        } else {
            gpsTracker.showSettingsAlert();
        }


        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
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

        //FaceBook
        loginButton = findViewById(R.id.connectWithFbButton);
        mCallbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("TAG", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "btnCancel", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Btnerrror", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "facebook:onError", error);
                // ...
            }
        });

        binding.imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginButton.performClick();
            }
        });


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

            if (Type.equalsIgnoreCase("Driver")) {
                startActivity(new Intent(LoginActivity.this, SignUpActivityDriver.class));

            } else {
                startActivity(new Intent(LoginActivity.this, SignUpActivityUser.class));
            }
        });

        binding.loginID.setOnClickListener(v -> {

            Validation();

           /* if(Type.equalsIgnoreCase("Driver"))
            {
                Validation();

            }else
            {
                Validation();
            }*/
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {

            GoogleSignInAccount account = result.getSignInAccount();

            Socilal_FirstName = account.getDisplayName();
            Socilal_last_name = "";
            Socilal_email = account.getEmail();
            Socilal_mobile = "";
            Socilal_city = "";
            Socilal_address = "";
            Socilal_address2 = "";
            Socilal_type = "";
            social_id = account.getId();
            social_image = String.valueOf(account.getPhotoUrl());

            social_image = String.valueOf(account.getPhotoUrl());

            if (NetworkAvailablity.checkNetworkStatus(LoginActivity.this)) ApISignUpMehod("USER");

             else Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();


        } else {

            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }


    private void Validation() {

        Email = binding.edtEmail.getText().toString();
        Password = binding.edtPassword.getText().toString();

        if (Email.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();

        } else if (Password.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();

        } else {
            if (NetworkAvailablity.checkNetworkStatus(LoginActivity.this)) loginMethod();
            else Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();

        }
    }

    private void loginMethod() {
        DataManager.getInstance().showProgressMessage(LoginActivity.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("email", Email);
        map.put("password", Password);
        map.put("lat", latitude);
        map.put("lon", longitude);
        map.put("register_id", token);
        Log.e(TAG, "Login Request :" + map);
        Call<LoginModel> call = shahenatInterfaceInterface.login(map);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    LoginModel finallyPr = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Login Response :" + responseString);
                    if (finallyPr.status.equalsIgnoreCase("1")) {

                        // Preference.save(LoginActivity.this,Preference.KEY_USER_ID,finallyPr.result.id);
                        SessionManager.writeString(LoginActivity.this, Constant.USER_INFO, responseString);
                        Toast.makeText(LoginActivity.this, getString(R.string.sign_in_successful), Toast.LENGTH_SHORT).show();
                        if (finallyPr.result.type.equalsIgnoreCase("USER"))
                            startActivity(new Intent(LoginActivity.this, HomeActiivity.class));
                        else
                       /* Preference.save(LoginActivity.this,Preference.KEY_User_name,finallyPr.result.firstName);
                        Preference.save(LoginActivity.this,Preference.KEY_User_email,finallyPr.result.email);
                        Preference.save(LoginActivity.this,Preference.KEY_USer_img,finallyPr.result.image);*/

                            startActivity(new Intent(LoginActivity.this, HomeActivityDriver.class));


                    } else {
                        DataManager.getInstance().hideProgressMessage();
                        Toast.makeText(LoginActivity.this, finallyPr.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    private void ApISignUpMehod(String LoginType) {
        DataManager.getInstance().showProgressMessage(LoginActivity.this, getString(R.string.please_wait));
        Call<LoginModel> call = shahenatInterfaceInterface.social_login(Socilal_FirstName, Socilal_last_name, Socilal_email, Socilal_mobile, Socilal_city, Socilal_address, Socilal_address2, token, latitude, longitude, LoginType, social_id, social_image);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    LoginModel finallyPr = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Login Response :" + responseString);
                    if (finallyPr.status.equalsIgnoreCase("1")) {
                        //    Preference.save(LoginActivity.this,Preference.KEY_USER_ID,finallyPr.result.id);
                        SessionManager.writeString(LoginActivity.this, Constant.USER_INFO, responseString);
                        Toast.makeText(LoginActivity.this, getString(R.string.sign_in_successful), Toast.LENGTH_SHORT).show();
                        if (finallyPr.result.type.equalsIgnoreCase("USER"))
                            startActivity(new Intent(LoginActivity.this, HomeActiivity.class));
                        else
                       /* Preference.save(LoginActivity.this,Preference.KEY_User_name,finallyPr.result.firstName);
                        Preference.save(LoginActivity.this,Preference.KEY_User_email,finallyPr.result.email);
                        Preference.save(LoginActivity.this,Preference.KEY_USer_img,finallyPr.result.image);*/

                            startActivity(new Intent(LoginActivity.this, HomeActivityDriver.class));


                    } else {
                        DataManager.getInstance().hideProgressMessage();
                        Toast.makeText(LoginActivity.this, finallyPr.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {

                DataManager.getInstance().hideProgressMessage();
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //   Toast.makeText(Activity_LoginOption.this, ""+token, Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();

                            String UsernAME = user.getDisplayName();
                            String email = user.getEmail();
                            String SocialId = user.getUid();
                            Uri Url = user.getPhotoUrl();

                            Socilal_FirstName = user.getDisplayName();
                            Socilal_last_name = "";
                            Socilal_email = user.getEmail();
                            Socilal_mobile = "";
                            Socilal_city = "";
                            Socilal_address = "";
                            Socilal_address2 = "";
                            Socilal_type = "";
                            social_id = user.getUid();

                            social_image = String.valueOf(user.getPhotoUrl());

                            if (NetworkAvailablity.checkNetworkStatus(LoginActivity.this)) ApISignUpMehod("USER");
                            else Toast.makeText(LoginActivity.this, R.string.checkInternet, Toast.LENGTH_SHORT).show();

                            //Toast.makeText(LoginActivity.this, "" + UsernAME, Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(LoginActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}