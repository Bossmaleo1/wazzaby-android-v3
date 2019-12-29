package com.wazzaby.android.wazzaby.service;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wazzaby.android.wazzaby.appviews.Home;
import com.wazzaby.android.wazzaby.connInscript.ProblematiqueConnexion;
import com.wazzaby.android.wazzaby.model.data.Profil;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static String TAG = "Something going well!!";
    private String channel_id = "channel_id";
    private JSONObject reponse;
    private int succes;
    private String title;

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

       // Toast.makeText(getApplicationContext(),"Test du bossmaleo !!",Toast.LENGTH_LONG).show();

        try {
            JSONObject reponse = new JSONObject(remoteMessage.getNotification().getBody());
            succes = reponse.getInt("succes");
            if(succes == 1) {
                //message = reponse.getString("message");
                //title = reponse.getString("name");
                //MessageCorps(res,message);
                Intent intent = new Intent();
                intent.setAction("com.wazzaby.android.wazzaby.broadcast.receiverMessage");
                intent.putExtra("message",remoteMessage.getNotification().getBody());
                sendBroadcast(intent);
            } else {
                //message = reponse.getString("message");
                //title = "Wazzaby";
                Intent intent = new Intent();
                intent.setAction("com.wazzaby.android.wazzaby.broadcast.receiverNotification");
                intent.putExtra("message",remoteMessage.getNotification().getBody());
                sendBroadcast(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        //startActivity(pushNotification);
        Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

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