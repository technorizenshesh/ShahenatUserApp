package com.shahenat.User;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.shahenat.BuildConfig;
import com.shahenat.PrivacyPolicy;
import com.shahenat.GPSTracker;
import com.shahenat.R;
import com.shahenat.TermsCondition;
import com.shahenat.User.model.LoginModel;
import com.shahenat.VerificationActivity;
import com.shahenat.databinding.ActivitySignUpBinding;
import com.shahenat.retrofit.ApiClient;
import com.shahenat.retrofit.Constant;
import com.shahenat.retrofit.ShahenatInterface;
import com.shahenat.utils.DataManager;
import com.shahenat.utils.FileUtil;
import com.shahenat.utils.NetworkAvailablity;
import com.shahenat.utils.SessionManager;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.shahenat.retrofit.Constant.emailPattern;

public class SignUpActivityUser extends AppCompatActivity {
    public String TAG = "SignUpActivityUser";
    ActivitySignUpBinding binding;
    String firstName = "", lastName = "", email = "", mobile = "", home = "", home2 = "", password = "", city = "", latitude = "", longitude = "";

    String str_image_path = "";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int MY_PERMISSION_CONSTANT = 5;
    private Uri uriSavedImage;
    GPSTracker gpsTracker;
    ShahenatInterface apiInterface;


    String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(ShahenatInterface.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(runnable -> {
            token = runnable.getToken();
            Log.e("Tokennnn", token);
        });

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.llLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivityUser.this, LoginActivity.class));

        });

        binding.txtSignUp.setOnClickListener(v -> {

            Validation();

        });

        binding.txtTerms.setOnClickListener(v -> {

            startActivity(new Intent(SignUpActivityUser.this, TermsCondition.class));

        });

        binding.txtPrivacyPolicy.setOnClickListener(v -> {

            startActivity(new Intent(SignUpActivityUser.this, PrivacyPolicy.class));

        });

        binding.ivUser.setOnClickListener(v -> {
            if (checkPermisssionForReadStorage())
                showImageSelection();
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


    private void Validation() {

        firstName = binding.edtFirstName.getText().toString();
        lastName = binding.edtLastName.getText().toString();
        email = binding.edtEmail.getText().toString();
        mobile = binding.edtMobile.getText().toString();
        home = binding.edtHomeAdress.getText().toString();
        city = binding.edtCity.getText().toString();
        home2 = binding.edtHomeAdress2.getText().toString();
        password = binding.edtpassword.getText().toString();

        if (str_image_path.equals("")) {
            Toast.makeText(this, "Please Select Your Image", Toast.LENGTH_SHORT).show();

        } else if (firstName.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter first Name", Toast.LENGTH_SHORT).show();

        } else if (lastName.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter last Name", Toast.LENGTH_SHORT).show();

        } else if (email.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter email", Toast.LENGTH_SHORT).show();
        } else if (!email.matches(emailPattern)) {
            Toast.makeText(this, getString(R.string.wrong_email), Toast.LENGTH_SHORT).show();

        } else if (mobile.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter Mobile", Toast.LENGTH_SHORT).show();

        } /*else if (city.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter City", Toast.LENGTH_SHORT).show();

        } else if (home.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter Home Address 1", Toast.LENGTH_SHORT).show();

        } else if (home2.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter Home Address 2", Toast.LENGTH_SHORT).show();

        }*/ else if (password.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();

        } else {
            if (NetworkAvailablity.checkNetworkStatus(SignUpActivityUser.this)) ApISignUpMehod();
            else Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();


        }
    }


    private void ApISignUpMehod() {
        DataManager.getInstance().showProgressMessage(SignUpActivityUser.this, getString(R.string.please_wait));
        DataManager.getInstance().showProgressMessage(SignUpActivityUser.this, "Please wait...");
        MultipartBody.Part filePart;
        if (!str_image_path.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path));
            filePart = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }

        RequestBody FirstName = RequestBody.create(MediaType.parse("text/plain"), firstName);
        RequestBody LastName = RequestBody.create(MediaType.parse("text/plain"), lastName);
        RequestBody Email = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody Mobile = RequestBody.create(MediaType.parse("text/plain"), mobile);
        RequestBody country_code = RequestBody.create(MediaType.parse("text/plain"), binding.ccp.getSelectedCountryCode() + "");
      //  RequestBody Home = RequestBody.create(MediaType.parse("text/plain"), "");
    //    RequestBody Home2 = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody Password = RequestBody.create(MediaType.parse("text/plain"), password);
       // RequestBody City = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody Type = RequestBody.create(MediaType.parse("text/plain"), "User");
      //  RequestBody Lat = RequestBody.create(MediaType.parse("text/plain"), "");
     //   RequestBody Long = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody Register_id = RequestBody.create(MediaType.parse("text/plain"), token);

        Call<LoginModel> call = apiInterface.user_signup(FirstName, LastName, Email, Password, Mobile, country_code, /*City, Home, Home2,*/ Register_id, /*Lat, Long,*/ Type, filePart);

        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {

                DataManager.getInstance().hideProgressMessage();
                try {


                    LoginModel finallyPr = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Signup Response :" + responseString);

                    if (finallyPr.status.equalsIgnoreCase("1")) {
                        Toast.makeText(SignUpActivityUser.this, "" + finallyPr.message, Toast.LENGTH_SHORT).show();
                        //SessionManager.writeString(SignUpActivityUser.this, Constant.KEY_Login_type, "User");
                  /*      startActivity(new Intent(SignUpActivityUser.this, VerificationActivity.class)
                                .putExtra("mobile", mobile)
                                .putExtra("countryCode", binding.ccp.getSelectedCountryCode() + ""));*/
                        startActivity(new Intent(SignUpActivityUser.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();

                    } else {
                        DataManager.getInstance().hideProgressMessage();
                        Toast.makeText(SignUpActivityUser.this, finallyPr.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
                Toast.makeText(SignUpActivityUser.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void showImageSelection() {

        final Dialog dialog = new Dialog(SignUpActivityUser.this);
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

        uriSavedImage = FileProvider.getUriForFile(SignUpActivityUser.this,
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
                str_image_path = DataManager.getInstance().getRealPathFromURI(SignUpActivityUser.this, data.getData());
                Glide.with(SignUpActivityUser.this)
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.ivUser);

            } else if (requestCode == REQUEST_CAMERA) {
                Glide.with(SignUpActivityUser.this)
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.ivUser);


            }

        }
    }


    //CHECKING FOR Camera STATUS
    public boolean checkPermisssionForReadStorage() {
        if (ContextCompat.checkSelfPermission(SignUpActivityUser.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(SignUpActivityUser.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(SignUpActivityUser.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivityUser.this,
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivityUser.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivityUser.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)


            ) {


                ActivityCompat.requestPermissions(SignUpActivityUser.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(SignUpActivityUser.this,
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
                        Toast.makeText(SignUpActivityUser.this, " permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUpActivityUser.this, "  permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
            }


        }
    }

}