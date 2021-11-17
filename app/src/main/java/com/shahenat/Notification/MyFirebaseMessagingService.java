package com.shahenat.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shahenat.Driver.HomeActivityDriver;
import com.shahenat.R;
import com.shahenat.User.HomeActiivity;
import com.shahenat.retrofit.Constant;
import com.shahenat.utils.DataManager;
import com.shahenat.utils.SessionManager;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private NotificationChannel mChannel;
    private NotificationManager notifManager;
    JSONObject object;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "Notification_Data:" + remoteMessage.getData());


        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            try {
                String title = "", key = "", status = "";

                object = new JSONObject(data.get("message"));
                status = object.getString("status");

                if(DataManager.getInstance().getUserData(getApplicationContext()).result.type.equals("User")){
                    if (status.equals("Accept")) {

                    /*    SessionManager.writeString(getApplicationContext(), Constant.driver_id, object.getString("driver_id"));
                        SessionManager.writeString(getApplicationContext(), Constant.request_id, object.getString("request_id"));
                        SessionManager.writeString(getApplicationContext(), Constant.first_name, object.getString("driver_firstname"));
                        SessionManager.writeString(getApplicationContext(), Constant.last_name, object.getString("driver_lastname"));*/
                        title = getString(R.string.booking_accept_by_driver);
                        Intent intent1 = new Intent("Job_Status_Action");
                        intent1.putExtra("request_id", object.getString("request_id"));
                        intent1.putExtra("status", status);
                        sendBroadcast(intent1);
                    }
                /*    else if (status.equals("Arrived")) {
                        title = getString(R.string.driver_arrived_pickup_location);
                        SessionManager.writeString(getApplicationContext(), Constant.driver_id, object.getString("driver_id"));
                        SessionManager.writeString(getApplicationContext(), Constant.request_id, object.getString("request_id"));
                        SessionManager.writeString(getApplicationContext(), Constant.first_name, object.getString("driver_firstname"));
                        SessionManager.writeString(getApplicationContext(), Constant.last_name, object.getString("driver_lastname"));
                        Intent intent1 = new Intent("Job_Status_Action");
                        intent1.putExtra("request_id", object.getString("request_id"));
                        intent1.putExtra("status", status);
                        sendBroadcast(intent1);
                    }

                    else if (status.equals("Start")) {
                        title = getString(R.string.driver_start_the_race);
                        SessionManager.writeString(getApplicationContext(), Constant.driver_id, object.getString("driver_id"));
                        SessionManager.writeString(getApplicationContext(), Constant.request_id, object.getString("request_id"));
                        SessionManager.writeString(getApplicationContext(), Constant.first_name, object.getString("driver_firstname"));
                        SessionManager.writeString(getApplicationContext(), Constant.last_name, object.getString("driver_lastname"));
                        Intent intent1 = new Intent("Job_Status_Action");
                        intent1.putExtra("request_id", object.getString("request_id"));
                        intent1.putExtra("status", status);
                        sendBroadcast(intent1);
                    }

                    else if (status.equals("Finish")) {

                        title = getString(R.string.driver_finish_race);
                        SessionManager.writeString(getApplicationContext(), Constant.driver_id, object.getString("driver_id"));
                        SessionManager.writeString(getApplicationContext(), Constant.request_id, object.getString("request_id"));
                        SessionManager.writeString(getApplicationContext(), Constant.first_name, object.getString("driver_firstname"));
                        SessionManager.writeString(getApplicationContext(), Constant.last_name, object.getString("driver_lastname"));
                        Intent intent1 = new Intent("Job_Status_Action");
                        intent1.putExtra("request_id", object.getString("request_id"));
                        intent1.putExtra("status", status);
                        sendBroadcast(intent1);
                    }

                    else if (status.equals("chat")) {
                        key = object.getString("message");
                        title = getString(R.string.new_msg_from_driver);
                        Intent intent1 = new Intent("Job_Status_Action");
                        intent1.putExtra("request_id", object.getString("request_id"));
                        intent1.putExtra("status", status);
                        sendBroadcast(intent1);
                    }*/

                    else if (status.equals("Cancel_by_driver")) {
                        key = object.getString("key");
                        title = getString(R.string.booking_cancel_by_driver);
                        Intent intent1 = new Intent("Job_Status_Action");
                        intent1.putExtra("request_id", object.getString("request_id"));
                        intent1.putExtra("status", status);
                        sendBroadcast(intent1);
                    }

                 /*   else if (status.equals("Cancel")) {
                        key = object.getString("key");
                        title = "Booking cancel by driver";
                        Intent intent1 = new Intent("Job_Status_Action");
                        intent1.putExtra("request_id", object.getString("request_id"));
                        intent1.putExtra("status", status);
                        sendBroadcast(intent1);
                    }*/
                }
              else {
                  if (status.equals("Pending")) {
                        // title =   object.getString("title");
                        title = getString(R.string.new_booking_request);
                        key = object.getString("key");
                        Intent intent1 = new Intent("Job_Status_Action1");
                        Log.e("SendData=====", object.toString());
                        intent1.putExtra("object", object.toString());
                        sendBroadcast(intent1);

                    }

                  /*  else if (status.equals("chat")) {
                        key = object.getString("message");
                        title = getString(R.string.new_chat_msg_user);
                        Intent intent1 = new Intent("Job_Status_Action1");
                        intent1.putExtra("request_id", object.getString("request_id"));
                        intent1.putExtra("status", status);
                        sendBroadcast(intent1);
                    }*/

                    else if (status.equals("Cancel_by_user")) {
                        key = object.getString("key");
                        title = getString(R.string.booking_cancel_by_user);
                        Intent intent1 = new Intent("Job_Status_Action1");
                        intent1.putExtra("request_id", object.getString("request_id"));
                        intent1.putExtra("status", status);
                        intent1.putExtra("object", object.toString());
                        sendBroadcast(intent1);
                    }
                }

                if (DataManager.getInstance().getUserData(getApplicationContext()).result.type.equals("User")){
                    wakeUpScreen();
                    displayCustomNotificationForOrdersUser(status, title, key);
                }
                else {
                    wakeUpScreen();
                    displayCustomNotificationForOrdersDriver(status, title, key);

                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void displayCustomNotificationForOrdersUser(String status, String title, String msg) {
        // SessionManager.writeString(getApplicationContext(),"provider_id",provider_id);
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService
                    (Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder;
            Intent intent = null;
            intent = new Intent(this, HomeActiivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent;
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (mChannel == null) {
                mChannel = new NotificationChannel
                        ("0", title, importance);
                mChannel.setDescription((String) msg);
                mChannel.enableVibration(true);
                mChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),attributes);
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, "0");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
            builder.setContentTitle(title)
                    .setSmallIcon(R.mipmap.logo) // required
                    .setContentText(msg)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri
                            (RingtoneManager.TYPE_RINGTONE));

            Notification notification = builder.build();
            notifManager.notify(0, notification);
        } else {
            Intent intent = null;
            intent = new Intent(this, HomeActiivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setSmallIcon(R.mipmap.logo)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(msg));

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(1251, notificationBuilder.build());
        }
    }

    private void displayCustomNotificationForOrdersDriver(String status, String title, String msg) {
        // SessionManager.writeString(getApplicationContext(),"provider_id",provider_id);
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService
                    (Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder;
            Intent intent = null;
            intent = new Intent(this, HomeActivityDriver.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent;
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (mChannel == null) {
                mChannel = new NotificationChannel
                        ("0", title, importance);
                mChannel.setDescription((String) msg);
                mChannel.enableVibration(true);
                mChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),attributes);
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, "0");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
            builder.setContentTitle(title)
                    .setSmallIcon(R.mipmap.logo) // required
                    .setContentText(msg)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri
                            (RingtoneManager.TYPE_RINGTONE));

            Notification notification = builder.build();
            notifManager.notify(0, notification);
        } else {
            Intent intent = null;
            intent = new Intent(this, HomeActivityDriver.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setSmallIcon(R.mipmap.logo)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(msg));

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(1251, notificationBuilder.build());
        }
    }


    private void wakeUpScreen() {
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();

        Log.e("screen on......", "" + isScreenOn);
        if (isScreenOn == false) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
            wl_cpu.acquire(10000);
        }
    }


}

