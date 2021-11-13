package com.shahenat.Driver;

;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shahenat.BuildConfig;
import com.shahenat.PrivacyPolicy;
import com.shahenat.User.LoginActivity;
import com.shahenat.utils.DataManager;

import com.shahenat.R;
import com.shahenat.TermsCondition;
import com.shahenat.databinding.ActivitySignUpDriverBinding;


import java.io.File;
import java.util.Calendar;


public class SignUpActivityDriver extends AppCompatActivity {

    ActivitySignUpDriverBinding binding;
    String firstName="" , lastName="",email="",mobile="", home="",home2="",password="",city="";
    String str_image_path = "";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int MY_PERMISSION_CONSTANT = 5;
    private Uri uriSavedImage;

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

        binding.ivUser.setOnClickListener(v -> {
            if (checkPermisssionForReadStorage())
                showImageSelection();
        });

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

        if(str_image_path.equals(""))
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

        }/*else if(city.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter City", Toast.LENGTH_SHORT).show();

        }else if(home.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter Home Address 1", Toast.LENGTH_SHORT).show();

        }else if(home2.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter Home Address 2", Toast.LENGTH_SHORT).show();

        }*/else if(password.equalsIgnoreCase(""))
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
            extras.putString("country_code",binding.ccp.getSelectedCountryCode()+"");
            extras.putString("home",home);
            extras.putString("home2",home2);
            extras.putString("password",password);
            extras.putString("city",city);
            extras.putString("driverImg",str_image_path);
            intent.putExtras(extras);
            startActivity(intent);

        }
    }


    public void showImageSelection() {

        final Dialog dialog = new Dialog(SignUpActivityDriver.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Widget_Material_ListPopupWindow;
        dialog.setContentView(R.layout.dialog_show_image_selection);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        LinearLayout layoutCamera = (LinearLayout) dialog.findViewById(R.id.layoutCemera);
        LinearLayout layoutGallary = (LinearLayout) dialog.findViewById(R.id.layoutGallary);
        layoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                openCamera();
            }
        });
        layoutGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                getPhotoFromGallary();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void getPhotoFromGallary() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE);

    }

    private void openCamera() {

        File dirtostoreFile = new File(Environment.getExternalStorageDirectory() + "/Shahenat/Images/");

        if (!dirtostoreFile.exists()) {
            dirtostoreFile.mkdirs();
        }

        String timestr = DataManager.getInstance().convertDateToString(Calendar.getInstance().getTimeInMillis());

        File tostoreFile = new File(Environment.getExternalStorageDirectory() + "/Shahenat/Images/" + "IMG_" + timestr + ".jpg");

        str_image_path = tostoreFile.getPath();

        uriSavedImage = FileProvider.getUriForFile(SignUpActivityDriver.this,
                BuildConfig.APPLICATION_ID + ".provider",
                tostoreFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

        startActivityForResult(intent, REQUEST_CAMERA);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.e("Result_code", requestCode + "");
            if (requestCode == SELECT_FILE) {
                str_image_path = DataManager.getInstance().getRealPathFromURI(SignUpActivityDriver.this, data.getData());
                Glide.with(SignUpActivityDriver.this)
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.ivUser);

            } else if (requestCode == REQUEST_CAMERA) {
                Glide.with(SignUpActivityDriver.this)
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.ivUser);


            }

        }
    }


    //CHECKING FOR Camera STATUS
    public boolean checkPermisssionForReadStorage() {
        if (ContextCompat.checkSelfPermission(SignUpActivityDriver.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(SignUpActivityDriver.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(SignUpActivityDriver.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivityDriver.this,
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivityDriver.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivityDriver.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)


            ) {


                ActivityCompat.requestPermissions(SignUpActivityDriver.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(SignUpActivityDriver.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);
            }
            return false;
        } else {

            //  explain("Please Allow Location Permission");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean read_external_storage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean write_external_storage = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (camera && read_external_storage && write_external_storage) {
                        showImageSelection();
                    } else {
                        Toast.makeText(SignUpActivityDriver.this, " permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUpActivityDriver.this, "  permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
            }


        }
    }

}