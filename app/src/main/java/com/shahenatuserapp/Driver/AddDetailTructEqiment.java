package com.shahenatuserapp.Driver;

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
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.shahenatuserapp.Driver.Model.EquimentModel;
import com.shahenatuserapp.Driver.Model.EquimentModelData;
import com.shahenatuserapp.Driver.adapter.EquimentSpinnerAdapter;
import com.shahenatuserapp.GPSTracker;
import com.shahenatuserapp.Preference;
import com.shahenatuserapp.R;
import com.shahenatuserapp.User.VerificationActivity;
import com.shahenatuserapp.User.model.LoginModel;
import com.shahenatuserapp.databinding.ActivityAddDetailTructEqimentBinding;
import com.shahenatuserapp.utils.FileUtil;
import com.shahenatuserapp.utils.RetrofitClients;
import com.shahenatuserapp.utils.SessionManager;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.shahenatuserapp.Driver.SignUpActivityDriver.UserProfile_img_driver;

public class AddDetailTructEqiment extends AppCompatActivity {

    ActivityAddDetailTructEqimentBinding binding;

    String firstName = "";
    String lastName = "";
    String email = "";
    String mobile = "";
    String home = "";
    String home2 = "";
    String password = "";
    String city = "";

    String latitude = "";
    String longitude = "";
    GPSTracker gpsTracker;

    boolean isEquimentImage = false;
    SessionManager sessionManager;

    private String[] code = {"Male", "Female"};

    private Bitmap bitmap;
    private Uri resultUri;
    //private SessionManager sessionManager;
    public static File UserProfile_img, codmpressedImage1, compressActualFile;

    String equiment_Id = "";

    String equipment_name = "";
    String manufacturer = "";
    String model = "";
    String brand = "";
    String size = "";
    String number_plate = "";
    String color = "";
    String price_km = "";
    String description = "";

    public ArrayList<EquimentModelData> modelist = new ArrayList<>();
    String token="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_detail_truct_eqiment);

        Intent intent = getIntent();
        if (intent != null) {

            Bundle extras = intent.getExtras();
            firstName = extras.getString("firstName");
            lastName = extras.getString("lastName");
            email = extras.getString("email");
            mobile = extras.getString("mobile");
            mobile = extras.getString("mobile");
            home2 = extras.getString("home2");
            password = extras.getString("password");
            city = extras.getString("city");
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(runnable -> {
            token = runnable.getToken();
            Log.e( "Tokennnn" ,token);
        });

        sessionManager = new SessionManager(AddDetailTructEqiment.this);

        setUi();

        //Gps Lat Long
        gpsTracker = new GPSTracker(AddDetailTructEqiment.this);
        if (gpsTracker.canGetLocation()) {

            latitude = String.valueOf(gpsTracker.getLatitude());
            longitude = String.valueOf(gpsTracker.getLongitude());

        } else {
            gpsTracker.showSettingsAlert();
        }

        if (sessionManager.isNetworkAvailable()) {

            binding.progressBar.setVisibility(View.VISIBLE);

            getAllEquiment();

        } else {
            Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();
        }

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

        binding.RRADDImg.setOnClickListener(v ->
        {
            Dexter.withActivity(AddDetailTructEqiment.this)
                    .withPermissions(Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                Intent intent = CropImage.activity().setGuidelines(CropImageView.Guidelines.OFF).getIntent(AddDetailTructEqiment.this);
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

        if (!isEquimentImage) {
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

            if (sessionManager.isNetworkAvailable()) {

                binding.progressBar.setVisibility(View.VISIBLE);

                ApISignUpMehod();

            } else {

                Toast.makeText(this, R.string.checkInternet, Toast.LENGTH_SHORT).show();

            }
        }
    }


    private void showSettingDialogue() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AddDetailTructEqiment.this);
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

                    binding.CardEquimentImg.setVisibility(View.VISIBLE);
                    binding.RRAddEuimentImg.setVisibility(View.GONE);

                    Glide.with(this).load(bitmap).into(binding.vDriverImg);

                    isEquimentImage = true;

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    codmpressedImage1 = new Compressor(this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                            .compressToFile(UserProfile_img);
                    Log.e("ActivityTag", "imageFilePAth: " + codmpressedImage1);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    public void getAllEquiment() {
        Call<EquimentModel> call = RetrofitClients
                .getInstance()
                .getApi()
                .get_equipment();
        call.enqueue(new Callback<EquimentModel>() {
            @Override
            public void onResponse(Call<EquimentModel> call, Response<EquimentModel> response) {
                try {

                    binding.progressBar.setVisibility(View.GONE);

                    EquimentModel myclass = response.body();

                    String status = myclass.getStatus();
                    String result = myclass.getMessage();

                    if (status.equalsIgnoreCase("1")) {

                        modelist = (ArrayList<EquimentModelData>) myclass.getResult();
                        EquimentSpinnerAdapter customAdapter = new EquimentSpinnerAdapter(AddDetailTructEqiment.this, modelist);
                        binding.spinnerCatgory.setAdapter(customAdapter);

                    } else {
                        Toast.makeText(AddDetailTructEqiment.this, result, Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<EquimentModel> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(AddDetailTructEqiment.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void ApISignUpMehod() {

        MultipartBody.Part imgFile = null;
        MultipartBody.Part imgFileEquiment = null;

        if (UserProfile_img_driver == null) {

        } else if (UserProfile_img == null) {

        } else {

            RequestBody requestFileOne = RequestBody.create(MediaType.parse("image/*"), UserProfile_img_driver);
            imgFile = MultipartBody.Part.createFormData("image", UserProfile_img_driver.getName(), requestFileOne);

            RequestBody requesEUiment = RequestBody.create(MediaType.parse("image/*"), UserProfile_img);
            imgFileEquiment = MultipartBody.Part.createFormData("vehicle_image", UserProfile_img.getName(), requesEUiment);

        }

        RequestBody FirstName = RequestBody.create(MediaType.parse("text/plain"), firstName);
        RequestBody LastName = RequestBody.create(MediaType.parse("text/plain"), lastName);
        RequestBody Email = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody Mobile = RequestBody.create(MediaType.parse("text/plain"), mobile);
        RequestBody Home = RequestBody.create(MediaType.parse("text/plain"), home);
        RequestBody Home2 = RequestBody.create(MediaType.parse("text/plain"), home2);
        RequestBody Password = RequestBody.create(MediaType.parse("text/plain"), password);
        RequestBody City = RequestBody.create(MediaType.parse("text/plain"), city);
        RequestBody Lat = RequestBody.create(MediaType.parse("text/plain"), "75.00");
        RequestBody Long = RequestBody.create(MediaType.parse("text/plain"), "75.00");
        RequestBody Register_id = RequestBody.create(MediaType.parse("text/plain"), token);
        RequestBody Type = RequestBody.create(MediaType.parse("text/plain"), "DRIVER");


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


        Call<LoginModel> call = RetrofitClients
                .getInstance()
                .getApi()
                .driver_signup(FirstName, LastName, Email, Password, Mobile, City, Home, Home2, Register_id, Lat, Long, Type, imgFile, imgFileEquiment,
                        Equiment_Id, Equiment_Name, Manufacturer, Model, Brand, Size, Number_plate, Color, Price_km, Description);

        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {

                binding.progressBar.setVisibility(View.GONE);

                LoginModel finallyPr = response.body();

                Log.e("bjbbj","knkbbkb"+response.body());

                String status = finallyPr.status;

                if (status.equalsIgnoreCase("1")) {
                    Toast.makeText(AddDetailTructEqiment.this, "" + finallyPr.message, Toast.LENGTH_SHORT).show();

                    Preference.save(AddDetailTructEqiment.this, Preference.KEY_Login_type, "Driver");

                    Preference.save(AddDetailTructEqiment.this, Preference.KEY_User_name, finallyPr.result.firstName);
                    Preference.save(AddDetailTructEqiment.this, Preference.KEY_User_email, finallyPr.result.email);
                    Preference.save(AddDetailTructEqiment.this, Preference.KEY_USer_img, finallyPr.result.image);

                    startActivity(new Intent(AddDetailTructEqiment.this, VerificationActivity.class));
                    finish();

                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddDetailTructEqiment.this, finallyPr.message, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {

                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(AddDetailTructEqiment.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}