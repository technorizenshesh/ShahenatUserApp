package com.shahenat.User;

import android.Manifest;
import android.annotation.SuppressLint;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.shahenat.BuildConfig;
import com.shahenat.R;
import com.shahenat.User.model.LoginModel;
import com.shahenat.databinding.ActivityEditProfileBinding;
import com.shahenat.retrofit.ApiClient;
import com.shahenat.retrofit.Constant;
import com.shahenat.retrofit.ShahenatInterface;
import com.shahenat.utils.DataManager;
import com.shahenat.utils.NetworkAvailablity;
import com.shahenat.utils.SessionManager;

import java.io.File;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.shahenat.retrofit.Constant.emailPattern;

public class EditProfileAct extends AppCompatActivity  {
    ActivityEditProfileBinding binding;
    LoginModel loginModel;
    ShahenatInterface apiInterface;
    String str_image_path = "";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int MY_PERMISSION_CONSTANT = 5;
    private Uri uriSavedImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(ShahenatInterface.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        initView();
    }

    private void initView() {

        binding.Imgback.setOnClickListener(v -> {
            finish();
        });
        loginModel = new LoginModel();
        loginModel = DataManager.getInstance().getUserData(EditProfileAct.this);
        setUserInfo();



        binding.btnUpdate.setOnClickListener(v -> {
            validation();
        });

        binding.ivUser.setOnClickListener(v -> {
            if (checkPermisssionForReadStorage())
                showImageSelection();
        });
    }

    private void validation() {
        if (binding.edFname.getText().toString().equals("")) {
            binding.edFname.setError(getString(R.string.required));
            binding.edFname.setFocusable(true);
        } else if (binding.etLName.getText().toString().equals("")) {
            binding.etLName.setError(getString(R.string.required));
            binding.etLName.setFocusable(true);
        } else if (binding.etEmail.getText().toString().equals("")) {
            binding.etEmail.setError(getString(R.string.please_enter_email));
            binding.etEmail.setFocusable(true);
        } else if (!binding.etEmail.getText().toString().matches(emailPattern)) {
            binding.etEmail.setError(getString(R.string.wrong_email));
            binding.etEmail.setFocusable(true);
        } else if (binding.edMobile.getText().toString().equals("")) {
            binding.edMobile.setError(getString(R.string.required));
            binding.edMobile.setFocusable(true);
        } else if (binding.edMobile.getText().toString().length() < 9) {
            binding.edMobile.setError(getString(R.string.enter_valid_number));
            binding.edMobile.setFocusable(true);
        } else {
            if (NetworkAvailablity.checkNetworkStatus(EditProfileAct.this)) {
                updateProfile();
            } else {
                Toast.makeText(this, getString(R.string.checkInternet), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void showImageSelection() {

        final Dialog dialog = new Dialog(EditProfileAct.this);
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

        uriSavedImage = FileProvider.getUriForFile(EditProfileAct.this,
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
                str_image_path = DataManager.getInstance().getRealPathFromURI(EditProfileAct.this, data.getData());
                Glide.with(EditProfileAct.this)
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.ivUser);

            } else if (requestCode == REQUEST_CAMERA) {
                Glide.with(EditProfileAct.this)
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.ivUser);


            }

        }
    }


    //CHECKING FOR Camera STATUS
    public boolean checkPermisssionForReadStorage() {
        if (ContextCompat.checkSelfPermission(EditProfileAct.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(EditProfileAct.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(EditProfileAct.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileAct.this,
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(EditProfileAct.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(EditProfileAct.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)


            ) {


                ActivityCompat.requestPermissions(EditProfileAct.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(EditProfileAct.this,
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
                        Toast.makeText(EditProfileAct.this, " permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfileAct.this, "  permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
            }


        }
    }


    private void updateProfile() {
        Log.e("UserId", "updateProfile: " + DataManager.getInstance().getUserData(EditProfileAct.this).result.id);
        DataManager.getInstance().showProgressMessage(EditProfileAct.this, "Please wait...");
        MultipartBody.Part filePart;
        if (!str_image_path.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path));
            filePart = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }
        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), DataManager.getInstance().getUserData(EditProfileAct.this).result.id);
        RequestBody f_name = RequestBody.create(MediaType.parse("text/plain"), binding.edFname.getText().toString());
        RequestBody l_name = RequestBody.create(MediaType.parse("text/plain"), binding.etLName.getText().toString());
       // RequestBody email = RequestBody.create(MediaType.parse("text/plain"), binding.etEmail.getText().toString());
        RequestBody mobile_number = RequestBody.create(MediaType.parse("text/plain"), binding.edMobile.getText().toString());
        RequestBody countrty_code = RequestBody.create(MediaType.parse("text/plain"), binding.ccp.getSelectedCountryCode());
        RequestBody city = RequestBody.create(MediaType.parse("text/plain"), DataManager.getInstance().getUserData(EditProfileAct.this).result.city);
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"),DataManager.getInstance().getUserData(EditProfileAct.this).result.address);
        RequestBody address2 = RequestBody.create(MediaType.parse("text/plain"),DataManager.getInstance().getUserData(EditProfileAct.this).result.address2);
        RequestBody lat = RequestBody.create(MediaType.parse("text/plain"),DataManager.getInstance().getUserData(EditProfileAct.this).result.lat);
        RequestBody lon = RequestBody.create(MediaType.parse("text/plain"),DataManager.getInstance().getUserData(EditProfileAct.this).result.lon);



        Call<LoginModel> signupCall = apiInterface.editprofile(user_id, f_name, l_name,/* email,*/ mobile_number, countrty_code,city,address,address2,lat,lon, filePart);
        signupCall.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    LoginModel data = response.body();
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        SessionManager.writeString(EditProfileAct.this, Constant.USER_INFO, dataResponse);
                        Toast.makeText(EditProfileAct.this, data.message, Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (data.status.equals("0")) {
                        Toast.makeText(EditProfileAct.this, data.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }

        });
    }



    private void setUserInfo() {
        loginModel = new LoginModel();
        loginModel = DataManager.getInstance().getUserData(EditProfileAct.this);
        binding.edFname.setText(loginModel.result.firstName);
        binding.etLName.setText(loginModel.result.lastName);
        binding.etEmail.setText(loginModel.result.email);
        binding.edMobile.setText(loginModel.result.mobile);
        Glide.with(EditProfileAct.this)
                .load(loginModel.result.image)
                .centerCrop()
                .error(R.drawable.user_default)
                .override(100,100)
                .into(binding.ivUser);
        if (loginModel.result.phoneCode!=null)
            binding.ccp.setCountryForPhoneCode(Integer.parseInt(loginModel.result.phoneCode));
        else binding.ccp.setCountryForPhoneCode(91);

    }
}
