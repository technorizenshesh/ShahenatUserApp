package com.shahenat.Driver;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.shahenat.PrivacyPolicy;
import com.shahenat.User.LoginActivity;
import com.shahenat.utils.FileUtil;
import com.shahenat.R;
import com.shahenat.TermsCondition;
import com.shahenat.databinding.ActivitySignUpDriverBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import id.zelory.compressor.Compressor;

public class SignUpActivityDriver extends AppCompatActivity {

    ActivitySignUpDriverBinding binding;

    private Bitmap bitmap;
    private Uri resultUri;
    //private SessionManager sessionManager;
    public static File UserProfile_img_driver, codmpressedImage, compressActualFile;

    String firstName="";
    String lastName="";
    String email="";
    String mobile="";
    String home="";
    String home2="";
    String password="";
    String city="";

    boolean isProfileImage=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up_driver);

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.llLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivityDriver.this, LoginActivity.class));

        });

        binding.txtSignUp.setOnClickListener(v -> {

         //   startActivity(new Intent(SignUpActivityDriver.this,AddDetailTructEqiment.class));

           Validation();

        });

        binding.txtTerms.setOnClickListener(v -> {

          startActivity(new Intent(SignUpActivityDriver.this, TermsCondition.class));

        });

        binding.txtPrivacyPolicy.setOnClickListener(v -> {

          startActivity(new Intent(SignUpActivityDriver.this, PrivacyPolicy.class));

        });

        binding.RRDriverImage.setOnClickListener(v -> {

            Dexter.withActivity(SignUpActivityDriver.this)
                    .withPermissions(Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                Intent intent = CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).getIntent(SignUpActivityDriver.this);
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
    }


    private void showSettingDialogue() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivityDriver.this);
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

                    UserProfile_img_driver = FileUtil.from(this, resultUri);

                    Glide.with(this).load(bitmap).circleCrop().into(binding.DriverImg);

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
                            .compressToFile(UserProfile_img_driver);
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
            Intent intent = new Intent(this, AddDetailTructEqiment.class);
            Bundle extras = new Bundle();
            extras.putString("firstName",firstName);
            extras.putString("lastName",lastName);
            extras.putString("email",email);
            extras.putString("mobile",mobile);
            extras.putString("home",home);
            extras.putString("home2",home2);
            extras.putString("password",password);
            extras.putString("city",city);
            intent.putExtras(extras);
            startActivity(intent);

          /*  if (sessionManager.isNetworkAvailable()) {

                binding.progressBar.setVisibility(View.VISIBLE);

                ApISignUpMehod();

            }else {
                Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();
            }*/
        }
    }

}