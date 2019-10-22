package com.wazzaby.android.wazzaby.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.appviews.Problematique;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static String TAG = "Something going well!!";
    private String channel_id = "channel_id";

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    public void sendRegistrationToServer(String token)
    {

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);



        Intent intent = new Intent();
        intent.setAction("com.wazzaby.android.wazzaby.broadcast");
        intent.putExtra("message",remoteMessage.getNotification().getBody());
        sendBroadcast(intent);

        //startActivity(pushNotification);
        Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

        /*NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);*/

        createNotificationChannel();

        //notificationManager.notify(1, notificationBuilder.build());
        //playNotificationSound();
    }

    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + this.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(this, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Nom du channel";
            String description = "Description du channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
           // notificationManager.notify(1,notificationBuilder.build());
        }
    }



}