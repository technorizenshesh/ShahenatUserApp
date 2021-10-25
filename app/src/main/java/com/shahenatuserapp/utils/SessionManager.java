package com.shahenatuserapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class SessionManager {

  private SharedPreferences preferences;
  private SharedPreferences.Editor editor;
  private Activity activity;
  private Context context;


  private String PREF_NAME = "myPreference";
  private int PRIVATE_MODE =0;

    public static final String KEY_USER_TOKEN = "keyUserToken";
    public static final String KEY_USER_AUTHKEY = "keyUserAuth";
    public static final String KEY_USER_NAME = "keyUserToken";
    public static final String KEY_USER_MOBILE = "keyUserMobile";
    public static final String KEY_USER_ID = "keyUserID";
    public static final String KEY_USER_EMAIL = "keyUserEmail";
    public static final String KEY_USER_GENDER = "keyUserGender";
    public static final String KEY_USER_ADDRESS = "keyUserAddress";
    public static final String KEY_USER_PROFILE_PICTURE = "keyUserProfile";
    public static final String KEY_DEVICE_TOKEN = "keyDeviceToken";
    public static final String KEY_USER_STATUS = "keyUserStatus";
    public static final String KEY_TERM_CONDITION = "keyTermCondition";
    public static final String KEY_PRIME = "keyPrime";
    public final String PROFILE_URL = "http://dssolution.in/demo/ecomm/upload/profile/";
    public final String PRODUCT_IMAGE_URL = "http://dssolution.in/demo/ecomm/upload/product/";
    public final String BASE_URL = "http://ixorainfotech.in/apicollection/Allelle/";


    public void saveUserToken(String userAuthKey){
    editor = preferences.edit();
    editor.putString(KEY_USER_AUTHKEY, userAuthKey);
    editor.apply();
    }

    public SessionManager(Activity activity){
          this.activity = activity;
          this.context = activity.getApplicationContext();
          preferences = activity.getApplicationContext().getSharedPreferences(PREF_NAME,PRIVATE_MODE);
    }

    private SessionManager(Context context){
    this.context = context;
    preferences = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
    }

    public void createLoginSession(String userID, String fullName, String email, String address, String address1,
                                   String profilePic, String mobile, String deviceToken){

        editor = preferences.edit();
        editor.putString(KEY_USER_ID,userID);
        editor.putString(KEY_USER_NAME, fullName);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_ADDRESS, address);
        editor.putString(KEY_USER_STATUS, address1);
        editor.putString(KEY_USER_PROFILE_PICTURE, profilePic);
        editor.putString(KEY_DEVICE_TOKEN, deviceToken);
        editor.putString(KEY_USER_MOBILE, mobile);
        editor.apply();

    }

    public void saveUserImage(String profilePic){
        editor = preferences.edit();
        editor.putString(KEY_USER_PROFILE_PICTURE, profilePic);
        editor.apply();
    }


    public void getEditData(String fullName, String email){

        editor = preferences.edit();
        editor.putString(KEY_USER_NAME, fullName);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();

    }

    public void saveUserId(String id){

        editor = preferences.edit();
        editor.putString(KEY_USER_ID, id);
        editor.apply();

    }

    public String getUserID(){
        String userID = preferences.getString(KEY_USER_ID,"");
        return userID;
    }

    public String getUserName(){
        String userName = preferences.getString(KEY_USER_NAME,"");
        return userName;
    }

    public String getAuthKEy(){
        String authKey = preferences.getString(KEY_USER_AUTHKEY,"");
        return authKey;
    }

    public String getUserEmail(){
        String userEmail = preferences.getString(KEY_USER_EMAIL,"");
        return userEmail;
    }

    public String getUserAddress(){
        String userAddress = preferences.getString(KEY_USER_ADDRESS,"");
        return userAddress;
    }

    public String getUserProfilePicture(){
        String userProfile = preferences.getString(KEY_USER_PROFILE_PICTURE,"");
        return userProfile;
    }

    public String getUserMobile(){
        String mobile = preferences.getString(KEY_USER_MOBILE,"");
        return mobile;
    }

    public String getUserStatus(){

        String userStatus = preferences.getString(KEY_USER_STATUS,"");
        return userStatus;
    }



   public void logoutUser(){
        editor = preferences.edit();
        editor.clear();
        editor.apply();
   }


    public boolean isNetworkAvailable()
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }


    public void hideKeybaord(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }

//    public void showToast(Activity activity, String msg, boolean isDurationLong)
//    {
//        try {
//            if(activity != null)
//            {
//                LayoutInflater inflater = activity.getLayoutInflater();
//                View layout = inflater.inflate(R.layout.custom_toast, null);
//
//                TextView text = (TextView) layout.findViewById(R.id.text);
//                text.setText(msg);
//
//                Toast toast = new Toast(activity);
//                if(isDurationLong)
//                {
//                    toast.setDuration(Toast.LENGTH_LONG);
//                }
//                else
//                {
//                    toast.setDuration(Toast.LENGTH_SHORT);
//                }
//                toast.setView(layout);
//
//                if(toast.getView().isShown())
//                {
//                    toast.cancel();
//                }
//                else
//                {
//                    toast.show();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
