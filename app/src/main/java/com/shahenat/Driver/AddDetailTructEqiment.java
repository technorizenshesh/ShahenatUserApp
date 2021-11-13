package com.shahenat.Driver;

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
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.shahenat.BuildConfig;
import com.shahenat.User.LoginActivity;
import com.shahenat.User.SignUpActivityUser;
import com.shahenat.VerificationActivity;
import com.shahenat.User.model.LoginModel;
import com.shahenat.retrofit.Constant;
import com.shahenat.retrofit.ShahenatInterface;
import com.shahenat.utils.DataManager;
import com.shahenat.utils.FileUtil;
import com.shahenat.utils.NetworkAvailablity;
import com.shahenat.utils.SessionManager;
import com.shahenat.Driver.Model.EquimentModel;
import com.shahenat.Driver.Model.EquimentModelData;
import com.shahenat.Driver.adapter.EquimentSpinnerAdapter;
import com.shahenat.GPSTracker;
import com.shahenat.R;
import com.shahenat.databinding.ActivityAddDetailTructEqimentBinding;
import com.shahenat.retrofit.ApiClient;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class AddDetailTructEqiment extends AppCompatActivity {
    public String TAG = "AddDetailTructEqiment";
    ActivityAddDetailTructEqimentBinding binding;
    String firstName="", lastName="",email="",mobile="",countryCode="", home="",home2="",password="",city="",latitude = "",longitude = "",equiment_Id = "",equipment_name = "",manufacturer = "",model = "",brand = "",size = "",number_plate = "",color = "",price_km = "",description = "";
    GPSTracker gpsTracker;
    boolean isEquimentImage = false;
    private String[] code = {"Male", "Female"};
    private Bitmap bitmap;
    private Uri resultUri;
    public static File UserProfile_img, codmpressedImage1;
    public ArrayList<EquimentModelData> modelist = new ArrayList<>();
    String token = "";
    ShahenatInterface shahenatInterfaceInterface;
    String str_image_path = "",str_image_path2 = "";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int MY_PERMISSION_CONSTANT = 5;
    private Uri uriSavedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shahenatInterfaceInterface = ApiClient.getClient().create(ShahenatInterface.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_detail_truct_eqiment);

        Intent intent = getIntent();

        if (intent != null) {

            Bundle extras = intent.getExtras();
            firstName = extras.getString("firstName");
            lastName = extras.getString("lastName");
            email = extras.getString("email");
            mobile = extras.getString("mobile");
            countryCode = extras.getString("country_code");
            home2 = extras.getString("home2");
            password = extras.getString("password");
            city = extras.getString("city");
            str_image_path = extras.getString("driverImg");
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(runnable -> {
            token = runnable.getToken();
            Log.e("Tokennnn", token);
        });


        setUi();

        //Gps Lat Long
        gpsTracker = new GPSTracker(AddDetailTructEqiment.this);
        if (gpsTracker.canGetLocation()) {

            latitude = String.valueOf(gpsTracker.getLatitude());
            longitude = String.valueOf(gpsTracker.getLongitude());

        } else {
            gpsTracker.showSettingsAlert();
        }

        if (NetworkAvailablity.checkNetworkStatus(AddDetailTructEqiment.this)) getAllEquiment();
        else Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();


    }

    private void setUi() {

        binding.spinnerCatgory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {

                equiment_Id = modelist.get(pos).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.txtSignUp.setOnClickListener(v -> {

            Validation();
            //  startActivity(new Intent(AddDetailTructEqiment.this, HomeActivityDriver.class));

        });


        binding.RRADDImg.setOnClickListener(v -> {
            if (checkPermisssionForReadStorage())
                showImageSelection();
        });



    }


    private void Validation() {

        equipment_name = binding.equipmentName.getText().toString();
        manufacturer = binding.edtManufacturer.getText().toString();
        model = binding.edtModel.getText().toString();
        brand = binding.edtBrand.getText().toString();
        size = binding.edtSize.getText().toString();
        number_plate = binding.edtNumberPlate.getText().toString();
        color = binding.edtColor.getText().toString();
        price_km = binding.edtPrice.getText().toString();
        description = binding.edtDescription.getText().toString();

        if (str_image_path2.equals("")) {
            Toast.makeText(this, "Please Select Your Image", Toast.LENGTH_SHORT).show();

        } else if (equipment_name.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter equipment name", Toast.LENGTH_SHORT).show();

        } else if (manufacturer.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter manufacturer", Toast.LENGTH_SHORT).show();

        } else if (model.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter model", Toast.LENGTH_SHORT).show();

        } else if (brand.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter brand", Toast.LENGTH_SHORT).show();

        } else if (size.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter size", Toast.LENGTH_SHORT).show();

        } else if (number_plate.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter number plate", Toast.LENGTH_SHORT).show();

        } else if (color.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter size", Toast.LENGTH_SHORT).show();

        } else if (price_km.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter price km", Toast.LENGTH_SHORT).show();

        } else if (description.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please Enter description", Toast.LENGTH_SHORT).show();

        } else {

            if (NetworkAvailablity.checkNetworkStatus(AddDetailTructEqiment.this)) ApISignUpMehod();
            else Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();
        }
    }


    public void getAllEquiment() {
        DataManager.getInstance().showProgressMessage(AddDetailTructEqiment.this, getString(R.string.please_wait));
        Call<EquimentModel> call = shahenatInterfaceInterface.get_equipment();
        call.enqueue(new Callback<EquimentModel>() {
            @Override
            public void onResponse(Call<EquimentModel> call, Response<EquimentModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    EquimentModel myclass = response.body();
                    String status = myclass.getStatus();
                    String result = myclass.getMessage();

                    if (status.equalsIgnoreCase("1")) {

                        modelist = (ArrayList<EquimentModelData>) myclass.getResult();
                        EquimentSpinnerAdapter customAdapter = new EquimentSpinnerAdapter(AddDetailTructEqiment.this, modelist);
                        binding.spinnerCatgory.setAdapter(customAdapter);

                    } else {
                        DataManager.getInstance().hideProgressMessage();
                        Toast.makeText(AddDetailTructEqiment.this, result, Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<EquimentModel> call, Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                Toast.makeText(AddDetailTructEqiment.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void ApISignUpMehod() {
        DataManager.getInstance().showProgressMessage(AddDetailTructEqiment.this, getString(R.string.please_wait));
        String lat = "22.7244";
        String lon = "75.8839";
        MultipartBody.Part filePart,filePart1;
        if (!str_image_path.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path));
            filePart = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }

        if (!str_image_path2.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path2));
            filePart1 = MultipartBody.Part.createFormData("vehicle_image", file.getName(), RequestBody.create(MediaType.parse("vehicle_image/*"), file));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart1 = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }
        RequestBody FirstName = RequestBody.create(MediaType.parse("text/plain"), firstName);
        RequestBody LastName = RequestBody.create(MediaType.parse("text/plain"), lastName);
        RequestBody Email = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody Mobile = RequestBody.create(MediaType.parse("text/plain"), mobile);
        RequestBody country_code = RequestBody.create(MediaType.parse("text/plain"), countryCode);

      //  RequestBody Home = RequestBody.create(MediaType.parse("text/plain"), home);
     //   RequestBody Home2 = RequestBody.create(MediaType.parse("text/plain"), home2);
        RequestBody Password = RequestBody.create(MediaType.parse("text/plain"), password);
     //   RequestBody City = RequestBody.create(MediaType.parse("text/plain"), city);
     //   RequestBody Lat = RequestBody.create(MediaType.parse("text/plain"), lat);
     //   RequestBody Long = RequestBody.create(MediaType.parse("text/plain"), lon);
        RequestBody Register_id = RequestBody.create(MediaType.parse("text/plain"), token);
        RequestBody Type = RequestBody.create(MediaType.parse("text/plain"), "Driver");


        RequestBody Equiment_Id = RequestBody.create(MediaType.parse("text/plain"), equiment_Id);
        RequestBody Equiment_Name = RequestBody.create(MediaType.parse("text/plain"), equipment_name);
        RequestBody Manufacturer = RequestBody.create(MediaType.parse("text/plain"), manufacturer);
        RequestBody Model = RequestBody.create(MediaType.parse("text/plain"), model);
        RequestBody Brand = RequestBody.create(MediaType.parse("text/plain"), brand);
        RequestBody Size = RequestBody.create(MediaType.parse("text/plain"), size);
        RequestBody Number_plate = RequestBody.create(MediaType.parse("text/plain"), number_plate);
        RequestBody Color = RequestBody.create(MediaType.parse("text/plain"), color);
        RequestBody Price_km = RequestBody.create(MediaType.parse("text/plain"), price_km);
        RequestBody Description = RequestBody.create(MediaType.parse("text/plain"), description);


        Call<LoginModel> call = shahenatInterfaceInterface.driver_signup(FirstName, LastName, Email, Password, Mobile,country_code,/* City, Home, Home2,*/ Register_id,/* Lat, Long,*/ Type, filePart, filePart1,
                Equiment_Id, Equiment_Name, Manufacturer, Model, Brand, Size, Number_plate, Color, Price_km, Description);

        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    LoginModel finallyPr = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Login Response :" + responseString);
                    if (finallyPr.status.equalsIgnoreCase("1")) {
                        Toast.makeText(AddDetailTructEqiment.this, "" + finallyPr.message, Toast.LENGTH_SHORT).show();
                        SessionManager.writeString(AddDetailTructEqiment.this, Constant.KEY_Login_type, "Driver");
                        SessionManager.writeString(AddDetailTructEqiment.this, Constant.USER_INFO, responseString);
                      /*  startActivity(new Intent(AddDetailTructEqiment.this, VerificationActivity.class)
                        .putExtra("mobile",mobile)
                        .putExtra("countryCode",countryCode));*/
                        startActivity(new Intent(AddDetailTructEqiment.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP));

                        finish();

                    } else {
                        DataManager.getInstance().hideProgressMessage();
                        Toast.makeText(AddDetailTructEqiment.this, finallyPr.message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                Toast.makeText(AddDetailTructEqiment.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void showImageSelection() {

        final Dialog dialog = new Dialog(AddDetailTructEqiment.this);
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

        str_image_path2 = tostoreFile.getPath();

        uriSavedImage = FileProvider.getUriForFile(AddDetailTructEqiment.this,
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
            binding.RRAddEuimentImg.setVisibility(View.GONE);
            binding.CardEquimentImg.setVisibility(View.VISIBLE);
            if (requestCode == SELECT_FILE) {
                str_image_path2 = DataManager.getInstance().getRealPathFromURI(AddDetailTructEqiment.this, data.getData());
                Glide.with(AddDetailTructEqiment.this)
                        .load(str_image_path2)
                        .centerCrop()
                        .into(binding.vDriverImg);

            } else if (requestCode == REQUEST_CAMERA) {
                Glide.with(AddDetailTructEqiment.this)
                        .load(str_image_path2)
                        .centerCrop()
                        .into(binding.vDriverImg);


            }

        }
    }


    //CHECKING FOR Camera STATUS
    public boolean checkPermisssionForReadStorage() {
        if (ContextCompat.checkSelfPermission(AddDetailTructEqiment.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(AddDetailTructEqiment.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(AddDetailTructEqiment.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddDetailTructEqiment.this,
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(AddDetailTructEqiment.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(AddDetailTructEqiment.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)


            ) {


                ActivityCompat.requestPermissions(AddDetailTructEqiment.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(AddDetailTructEqiment.this,
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
                        Toast.makeText(AddDetailTructEqiment.this, " permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddDetailTructEqiment.this, "  permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
            }


        }
    }

}