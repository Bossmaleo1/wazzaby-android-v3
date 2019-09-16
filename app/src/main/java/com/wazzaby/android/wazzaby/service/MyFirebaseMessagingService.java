package com.wazzaby.android.wazzaby.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.appviews.Historique;
import com.wazzaby.android.wazzaby.appviews.Home;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static String TAG = "Something going well!!";

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

        // Create an explicit intent for an Activity in your app
        Intent intentlaunchernotification = new Intent(this, Home.class);
        intentlaunchernotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentlaunchernotification, 0);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSmallIcon(R.drawable.wazaby)
                .setContentIntent(pendingIntent)
                .setColor(Color.parseColor("#188dc8"))
                .setAutoCancel(true);


        //Color.parseColor("#188dc8")

        /*Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
        pushNotification.putExtra("message", remoteMessage.getData().toString());
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);*/
        //Toast.makeText(Home.context,"Test malin du bossmaleo !!",Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setAction("com.wazzaby.android.wazzaby.broadcast");
        intent.putExtra("message",remoteMessage.getNotification().getBody());
        sendBroadcast(intent);
        //LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

        //int mCartItemCount = 10;
        //Accueil.badge = Accueil.navigation.showBadge(R.id.notification);
        /*Accueil.badge.setNumber(mCartItemCount);
        Accueil.badge.setBadgeTextColor(Color.WHITE);*/

        //startActivity(pushNotification);
        Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
        playNotificationSound();
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



}