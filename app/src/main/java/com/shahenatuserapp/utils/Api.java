package com.shahenatuserapp.utils;


import com.shahenatuserapp.Driver.Model.EquimentModel;
import com.shahenatuserapp.PrivacyPolicyModel;
import com.shahenatuserapp.User.model.DetailsModel;
import com.shahenatuserapp.User.model.EquimentModelAvalaible;
import com.shahenatuserapp.User.model.GetPriceModel;
import com.shahenatuserapp.User.model.LoginModel;
import com.shahenatuserapp.User.model.NearestDriverModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    //User
    String login ="login";
    String user_signup ="user_signup";
    String social_login ="social_login";
    String get_neareast_drivers ="get_neareast_drivers";
    String get_price_km ="get_price_km";
    String get_avalable_equipment ="get_avalable_equipment";
    String get_avalable_equipment_details ="get_avalable_equipment_details";
    String get_schedule_ride  ="schedule_ride ";

    //Driver
    String driver_signup ="driver_signup";
    String get_equipment ="get_equipment";


    //Common
    String get_privacypolicy ="get_privacypolicy";
    String get_termsconditions ="get_termsconditions";

    @FormUrlEncoded
    @POST(login)
    Call<LoginModel> login(
            @Field("email") String email,
            @Field("password") String password,
            @Field("lat") String lat,
            @Field("lon") String lon,
            @Field("register_id") String register_id
    );

    @FormUrlEncoded
    @POST(social_login)
    Call<LoginModel>social_login(
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("email") String email,
            @Field("mobile") String mobile,
            @Field("city") String city,
            @Field("address") String address,
            @Field("address2") String address2,
            @Field("register_id") String register_id,
            @Field("lat") String lat,
            @Field("lon") String lon,
            @Field("type") String type,
            @Field("social_id") String social_id,
            @Field("image") String image
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



    @FormUrlEncoded
    @POST(get_neareast_drivers)
    Call<NearestDriverModel> get_neareast_drivers(
            @Field("lat") String lat,
            @Field("lon") String lon
    );

  @FormUrlEncoded
    @POST(get_price_km)
    Call<GetPriceModel> get_price_km(
            @Field("user_id") String user_id,
            @Field("pickup_add") String pickup_add,
            @Field("p_lat") String p_lat,
            @Field("p_lon") String p_lon,
            @Field("drop_add") String drop_add,
            @Field("d_lat") String d_lat,
            @Field("d_lon") String d_lon
    );

  @FormUrlEncoded
    @POST(get_schedule_ride)
    Call<GetPriceModel> get_schedule_ride(
            @Field("user_id") String user_id,
            @Field("pickup_add") String pickup_add,
            @Field("p_lat") String p_lat,
            @Field("p_lon") String p_lon,
            @Field("drop_add") String drop_add,
            @Field("d_lat") String d_lat,
            @Field("d_lon") String d_lon
    );

    @Multipart
    @POST(driver_signup)
    Call<LoginModel>driver_signup(
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
            @Part MultipartBody.Part part1,
            @Part MultipartBody.Part part2,

            @Part("equipment_id") RequestBody equipment_id,
            @Part("equipment_name") RequestBody equipment_name,
            @Part("manufacturer") RequestBody manufacturer,
            @Part("model") RequestBody model,
            @Part("brand") RequestBody brand,
            @Part("size") RequestBody size,
            @Part("number_plate") RequestBody number_plate,
            @Part("color") RequestBody color,
            @Part("price_km") RequestBody price_km,
            @Part("description") RequestBody description
    );


    @POST(get_equipment)
    Call<EquimentModel> get_equipment();

    @POST(get_privacypolicy)
    Call<PrivacyPolicyModel> get_privacypolicy();

    @POST(get_termsconditions)
    Call<PrivacyPolicyModel> get_termsconditions();

    @POST(get_avalable_equipment)
    Call<EquimentModelAvalaible> get_avalable_equipment();

    @FormUrlEncoded
    @POST(get_avalable_equipment_details)
    Call<DetailsModel> get_avalable_equipment_details(
            @Field("id") String id
    );

}
