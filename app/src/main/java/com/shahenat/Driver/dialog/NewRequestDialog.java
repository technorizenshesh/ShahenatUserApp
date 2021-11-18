package com.shahenat.Driver.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.shahenat.Driver.ArrivingDriverAct;
import com.shahenat.R;
import com.shahenat.User.model.BookingDetailModel;
import com.shahenat.databinding.DialogNewRequestBinding;
import com.shahenat.retrofit.ApiClient;
import com.shahenat.retrofit.ShahenatInterface;
import com.shahenat.utils.DataManager;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewRequestDialog {
    public static String TAG = "NewRequestDialog";
    private static final NewRequestDialog ourInstance = new NewRequestDialog();

    public static NewRequestDialog getInstance() {
        return ourInstance;
    }

    private NewRequestDialog() {
    }

    private long timeCountInMilliSeconds = 1 * 60000;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private ProgressBar progressBarCircle;

    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private CountDownTimer countDownTimer;
    private TextView textViewTime;

    Dialog dialog;
    DialogNewRequestBinding binding;
    ShahenatInterface apiInterface;
    String driver_id = "", request_id = "";
    MediaPlayer mp;
    Context contx;


    public void Request(Context context, String obj) {
        JSONObject object;
        dialog = new Dialog(context);
        contx =context;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_new_request, null, false);
        dialog.setContentView(binding.getRoot());
        apiInterface = ApiClient.getClient().create(ShahenatInterface.class);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        try {
            // {message={"car_type_id":"1","driver_id":"10","booktype":"NOW","shareride_type":null,"picuplocation":"indore","result":"successful","estimated_arrival_distance":"17123.34","estimated_arrival_time":"17123.34","dropofflocation":"bhopal","droplon":"77.4126","alert":"Booking request","user_id":"1","picklatertime":"08:00","droplat":"23.2599","picuplat":"22.7196","picklaterdate":"2021-02-21","request_id":10,"key":"New Booking Request","status":"Pending","pickuplon":"75.8577"}}
            Log.e("DialogChala====", "=======" + obj);
            object = new JSONObject(obj);
            driver_id =   DataManager.getInstance().getUserData(context).result.id; //String.valueOf(object.get("driver_id"));
            request_id = String.valueOf(object.get("request_id"));
            binding.tvPickupLoc.setText(object.getString("pickup_add"));
            binding.tvDestinationLoc.setText(object.getString("drop_add"));
            binding.tvFare.setText("       :       " + "€" + object.getString("total_price"));
         //   if(SessionManager.readString(context, Constant.LANGUAGE,"").equals("fr"))
            binding.tvMinut.setText("     :  " + object.getString("total_time") + " Mins");
         //   else  binding.tvMinuts.setText("     :  " + object.getString("estimated_arrival_time") + " Minuts");
            binding.tvText.setText(object.getString("user_name"));
            binding.tvPayType.setText("     :  " + object.getString("payment_type"));
         //   binding.tvModel.setText("       :      " + object.getString("car_name"));
           // binding.ratingBar.setRating(Float.parseFloat(object.getString("rating")));


        } catch (JSONException e) {
            e.printStackTrace();
        }


        binding.tvAccept.setOnClickListener(v -> {
            AcceptCancel(context, driver_id, request_id, "Accepted","User");
        });
        binding.tvRefuse.setOnClickListener(v -> {
            AcceptCancel(context, driver_id, request_id, "Rejected","User");
        });

        startStop();


        dialog.show();

    }


    /**
     * method to reset count down timer
     */
    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();
    }


    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {
            // call to initialize the timer values
            setTimerValues();
            // call to initialize the progress bar values
            setProgressBarValues();
            timerStatus = TimerStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

        } else {
            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();
            dialog.dismiss();

        }

    }

    /**
     * method to initialize the values for count down timer
     */
    private void setTimerValues() {
        // assigning values after converting to milliseconds
        // timeCountInMilliSeconds = 1 * 60 * 1000;
        timeCountInMilliSeconds = 40000;
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                binding.textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

                binding.progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {

                //  textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
                //  setProgressBarValues();
                timerStatus = TimerStatus.STOPPED;
                mp.stop();
                dialog.dismiss();
            }

        }.start();
        countDownTimer.start();
        mp=MediaPlayer.create(contx,R.raw.alarm);
        mp.start();
    }

    /**
     * method to stop count down timer
     */
    public void stopCountDownTimer() {
        countDownTimer.cancel();
        timerStatus = TimerStatus.STOPPED;
        mp.stop();
        dialog.dismiss();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {

        binding.progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        binding.progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }


    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        String ms[] = hms.split(":");

        return ms[1] + ":" + ms[2];
    }

    public void AcceptCancel(Context context, String driver_id, String request_id, String status,String type) {
        DataManager.getInstance().showProgressMessage((Activity) context, context.getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("driver_id", DataManager.getInstance().getUserData(context).result.id);
        map.put("request_id", request_id);
        map.put("status", status);
        map.put("type", type);
        Log.e(TAG, "Request Accept or Cancel Request :" + map);
        Call<BookingDetailModel> call = apiInterface.acceptCancelRequest(map);
        call.enqueue(new Callback<BookingDetailModel>() {
            @Override
            public void onResponse(Call<BookingDetailModel> call, Response<BookingDetailModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    BookingDetailModel data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Request Accept or Cancel Response :" + responseString);
                    if (data.status.equals("1")) {
                        if (status.equals("Accepted")) {
                            context.startActivity(new Intent(context, ArrivingDriverAct.class).putExtra("request_id", request_id));
                            dialog.dismiss();
                            mp.stop();
                        } else {
                            dialog.dismiss();
                            mp.stop();
                        }
                    } else if (data.status.equals("0")) {
                        dialog.dismiss();
                        mp.stop();
                        Toast.makeText(context, data.message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BookingDetailModel> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

}

