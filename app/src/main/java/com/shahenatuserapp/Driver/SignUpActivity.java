package com.shahenatuserapp.Driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.shahenatuserapp.R;
import com.shahenatuserapp.User.LoginActivity;
import com.shahenatuserapp.User.SignUpActivityUser;
import com.shahenatuserapp.User.VerificationActivity;
import com.shahenatuserapp.databinding.ActivitySignUp2Binding;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUp2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up2);

       binding.Imgback.setOnClickListener(v -> {
           onBackPressed();
       });

        binding.Imgback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.llLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));

        });

        binding.txtSignUp.setOnClickListener(v -> {

            startActivity(new Intent(SignUpActivity.this, AddDetailTructEqiment.class));

        });

        binding.RRDriverImage.setOnClickListener(v ->{


        });

    }

    private void Validation() {

        String firstName="";
        String lastName="";
        String email="";
        String Mobile="";
        String Home="";
        String Home2="";
        String Password="";
        String City="";

        firstName = binding.edtFirstName.getText().toString();
        lastName =  binding.edtLastName.getText().toString();
        email =  binding.edtEmail.getText().toString();
        Mobile =  binding.edtMobile.getText().toString();
        City =  binding.edtCity.getText().toString();
        Home =  binding.edtHomeAdress.getText().toString();
        Home2 =  binding.edtHomeAdress2.getText().toString();
        Password =  binding.edtpassword.getText().toString();


        if(firstName.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter first Name", Toast.LENGTH_SHORT).show();

        }else if(lastName.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter last Name", Toast.LENGTH_SHORT).show();

        }else if(email.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter email", Toast.LENGTH_SHORT).show();

        }else if(Mobile.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter Mobile", Toast.LENGTH_SHORT).show();

        }else if(City.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter City", Toast.LENGTH_SHORT).show();

        }else if(Home.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter Home Address 1", Toast.LENGTH_SHORT).show();

        }else if(Home2.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter Home Address 2", Toast.LENGTH_SHORT).show();

        }else if(Password.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();

        }else
        {

        }

    }

}