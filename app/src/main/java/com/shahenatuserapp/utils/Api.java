package com.shahenatuserapp.utils;


import com.shahenatuserapp.User.model.LoginModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    String login ="login";
    String user_signup ="user_signup";
    String get_profile ="get_profile";
    String seller_signup ="seller_signup";
    String  add_product="add_product";
    String  category_list="category_list";
    String  get_subcategory="get_subcategory";
    String update_buyer_profile ="update_buyer_profile";
    String get_product ="get_product";


    @FormUrlEncoded
    @POST(login)
    Call<LoginModel> login(
            @Field("email") String email,
            @Field("password") String password,
            @Field("lat") String lat,
            @Field("lon") String lon,
            @Field("register_id") String register_id
    );

    @Multipart
    @POST(user_signup)
    Call<LoginModel>user_signup(
            @Part("first_name") RequestBody first_name,
            @Part("last_name") RequestBody last_name,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("mobile") RequestBody mobile,
            @Part("city") RequestBody city,
            @Part("address") RequestBody address,
            @Part("address2") RequestBody address2,
            @Part("register_id") RequestBody register_id,
            @Part("lat") RequestBody lat,
            @Part("lon") RequestBody lon,
            @Part("type") RequestBody type,
            @Part MultipartBody.Part part1
    );

}
