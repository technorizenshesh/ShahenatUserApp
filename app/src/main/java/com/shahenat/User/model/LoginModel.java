package com.shahenat.User.model;


import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shahenat.R;

public class LoginModel {

    @SerializedName("result")
    @Expose
    public Result result;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("status")
    @Expose
    public String status;

    public class Result {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("customer_id")
        @Expose
        public String customerId;
        @SerializedName("first_name")
        @Expose
        public String firstName;
        @SerializedName("last_name")
        @Expose
        public String lastName;
        @SerializedName("country_code")
        @Expose
        public String countryCode;
        @SerializedName("mobile")
        @Expose
        public String mobile;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("password")
        @Expose
        public String password;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("social_id")
        @Expose
        public String socialId;
       /* @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("address2")
        @Expose
        public String address2;
        @SerializedName("lat")
        @Expose
        public String lat;
        @SerializedName("lon")
        @Expose
        public String lon;*/
        @SerializedName("register_id")
        @Expose
        public String registerId;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("date_time")
        @Expose
        public String dateTime;
       /* @SerializedName("country")
        @Expose
        public String country;
        @SerializedName("state")
        @Expose
        public String state;
        @SerializedName("city")
        @Expose
        public String city;*/
        @SerializedName("online_status")
        @Expose
        public String onlineStatus;
        @SerializedName("wallet")
        @Expose
        public String wallet;

    }
    @BindingAdapter({"android:image"})
    public static void loadImage(ImageView view, String imageUrl){
        Glide.with(view.getContext())
                .load(imageUrl)
                .apply(new RequestOptions().placeholder(R.drawable.user_default))
                .override(200,200)
                .into(view);
    }

}

