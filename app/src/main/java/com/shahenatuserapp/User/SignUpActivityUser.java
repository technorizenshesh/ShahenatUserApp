package com.shahenatuserapp.User;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.shahenatuserapp.ChosseLoginActivity;
import com.shahenatuserapp.GPSTracker;
import com.shahenatuserapp.Preference;
import com.shahenatuserapp.R;
import com.shahenatuserapp.User.model.LoginModel;
import com.shahenatuserapp.databinding.ActivitySignUpBinding;
import com.shahenatuserapp.utils.FileUtil;
import com.shahenatuserapp.utils.RetrofitClients;
import com.shahenatuserapp.utils.SessionManager;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivityUser extends AppCompatActivity {

    ActivitySignUpBinding binding;

    private Bitmap bitmap;
    private Uri resultUri;
    //private SessionManager sessionManager;
    public static File UserProfile_img, codmpressedImage, compressActualFile;

    String firstName="";
    String lastName="";
    String email="";
    String mobile="";
    String home="";
    String home2="";
    String password="";
    String city="";

    String latitude="";
    String longitude="";
    GPSTracker gpsTracker;


    boolean isProfileImage=false;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_sign_up);

        sessionManager = new SessionManager(SignUpActivityUser.this);

       binding.Imgback.setOnClickListener(v -> {
           onBackPressed();
       });

       binding.llLogin.setOnClickListener(v -> {
           startActivity(new Intent(SignUpActivityUser.this,LoginActivity.class));

       });

       binding.txtSignUp.setOnClickListener(v -> {

           Validation();

       });

       binding.RRUserImg.setOnClickListener(v -> {

           Dexter.withActivity(SignUpActivityUser.this)
                   .withPermissions(Manifest.permission.CAMERA,
                           Manifest.permission.READ_EXTERNAL_STORAGE,
                           Manifest.permission.WRITE_EXTERNAL_STORAGE)
                   .withListener(new MultiplePermissionsListener() {
                       @Override
                       public void onPermissionsChecked(MultiplePermissionsReport report) {
                           if (report.areAllPermissionsGranted()) {
                               Intent intent = CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).getIntent(SignUpActivityUser.this);
                               startActivityForResult(intent, 1);
                           } else {
                               showSettingDialogue();
                           }
                       }
                       @Override
                       public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                           token.continuePermissionRequest();
                       }
                   }).check();
       });

        //Gps Lat Long
        gpsTracker = new GPSTracker(SignUpActivityUser.this);
        if (gpsTracker.canGetLocation()) {

            latitude = String.valueOf(gpsTracker.getLatitude());
            longitude = String.valueOf(gpsTracker.getLongitude());

        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void showSettingDialogue() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivityUser.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                openSetting();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();

    }

    private void openSetting() {

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);

                    UserProfile_img = FileUtil.from(this, resultUri);

                    Glide.with(this).load(bitmap).circleCrop().into(binding.imgeUSer);

                    isProfileImage = true;

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    codmpressedImage = new Compressor(this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                            .compressToFile(UserProfile_img);
                    Log.e("ActivityTag", "imageFilePAth: " + codmpressedImage);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void Validation() {

         firstName = binding.edtFirstName.getText().toString();
         lastName =  binding.edtLastName.getText().toString();
         email =  binding.edtEmail.getText().toString();
         mobile =  binding.edtMobile.getText().toString();
         home =  binding.edtHomeAdress.getText().toString();
         city =  binding.edtCity.getText().toString();
         home2 =  binding.edtHomeAdress2.getText().toString();
         password =  binding.edtpassword.getText().toString();

        if(!isProfileImage)
        {
            Toast.makeText(this, "Please Select Your Image", Toast.LENGTH_SHORT).show();

        }else if(firstName.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter first Name", Toast.LENGTH_SHORT).show();

        }else if(lastName.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter last Name", Toast.LENGTH_SHORT).show();

        }else if(email.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter email", Toast.LENGTH_SHORT).show();

        }else if(mobile.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter Mobile", Toast.LENGTH_SHORT).show();

        }else if(city.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter City", Toast.LENGTH_SHORT).show();

        }else if(home.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter Home Address 1", Toast.LENGTH_SHORT).show();

        }else if(home2.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter Home Address 2", Toast.LENGTH_SHORT).show();

        }else if(password.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();

        }else
        {

            if (sessionManager.isNetworkAvailable()) {

                binding.progressBar.setVisibility(View.VISIBLE);

                ApISignUpMehod();

            }else {
                Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();
            }




        }
    }


    private void ApISignUpMehod(){

        MultipartBody.Part imgFile = null;

        if (UserProfile_img == null) {

        } else {
            RequestBody requestFileOne = RequestBody.create(MediaType.parse("image/*"),UserProfile_img);
            imgFile = MultipartBody.Part.createFormData("image",UserProfile_img.getName(), requestFileOne);
        }

        RequestBody FirstName = RequestBody.create(MediaType.parse("text/plain"), firstName);
        RequestBody LastName = RequestBody.create(MediaType.parse("text/plain"), lastName);
        RequestBody Email = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody Mobile = RequestBody.create(MediaType.parse("text/plain"), mobile);
        RequestBody Home = RequestBody.create(MediaType.parse("text/plain"), home);
        RequestBody Home2 = RequestBody.create(MediaType.parse("text/plain"), home2);
        RequestBody Password = RequestBody.create(MediaType.parse("text/plain"), password);
        RequestBody City = RequestBody.create(MediaType.parse("text/plain"), city);
        RequestBody Type = RequestBody.create(MediaType.parse("text/plain"), "USER");
        RequestBody Lat = RequestBody.create(MediaType.parse("text/plain"), "75.00");
        RequestBody Long = RequestBody.create(MediaType.parse("text/plain"), "75.00");
        RequestBody Register_id = RequestBody.create(MediaType.parse("text/plain"), "dbnbcn");

        Call<LoginModel> call = RetrofitClients
                .getInstance()
                .getApi()
                .user_signup(FirstName,LastName,Email,Password,Mobile,City,Home,Home2,Register_id,Lat,Long,Type,imgFile);

        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {

                binding.progressBar.setVisibility(View.GONE);

                LoginModel finallyPr = response.body();

                String status = finallyPr.status;

                if (status.equalsIgnoreCase("1")) {
                    Toast.makeText(SignUpActivityUser.this, ""+finallyPr.message, Toast.LENGTH_SHORT).show();

                    Preference.save(SignUpActivityUser.this,Preference.KEY_Login_type,"User");

                    Preference.save(SignUpActivityUser.this,Preference.KEY_User_name,finallyPr.result.firstName);
                    Preference.save(SignUpActivityUser.this,Preference.KEY_User_email,finallyPr.result.email);

                    startActivity(new Intent(SignUpActivityUser.this,VerificationActivity.class));
                    finish();

                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivityUser.this, finallyPr.message, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {

                binding.progressBar.setVisibility(View.GONE);

                Toast.makeText(SignUpActivityUser.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}