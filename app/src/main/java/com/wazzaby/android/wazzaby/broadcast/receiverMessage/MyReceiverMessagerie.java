package com.wazzaby.android.wazzaby.broadcast.receiverMessage;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.snackbar.Snackbar;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.appviews.MessageConstitution;
import com.wazzaby.android.wazzaby.model.data.Conversationprivateitem;

import org.json.JSONException;
import org.json.JSONObject;

import static com.wazzaby.android.wazzaby.appviews.MessageConstitution.ID_USER_ONFOCUS;
import static com.wazzaby.android.wazzaby.appviews.MessageConstitution.allUsersAdapter;
import static com.wazzaby.android.wazzaby.appviews.MessageConstitution.context_messageconstitution;
import static com.wazzaby.android.wazzaby.appviews.MessageConstitution.data_recyclerview;
import static com.wazzaby.android.wazzaby.appviews.MessageConstitution.etat_du_boss;
import static com.wazzaby.android.wazzaby.appviews.MessageConstitution.layoutManager;
import static com.wazzaby.android.wazzaby.appviews.MessageConstitution.smoothScroller;
import static com.wazzaby.android.wazzaby.fragments.Accueil.mCartItemCountforchat;
import static com.wazzaby.android.wazzaby.fragments.Accueil.navigation;
import static com.wazzaby.android.wazzaby.utils.MyApplication.CHANNEL_1_ID;

public class MyReceiverMessagerie extends BroadcastReceiver {

    private String message;
    private String title;
    private int succes;
    private Snackbar snackbar;
    private Resources res;
    private String photo;
    private int id_user_we_receved;

    public MyReceiverMessagerie() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        mCartItemCountforchat++;
        BadgeDrawable badge = navigation.showBadge(R.id.conversationprivee);
        badge.setNumber(mCartItemCountforchat);
        badge.setBadgeTextColor(Color.WHITE);

        Intent notifyIntent = new Intent(context, MessageConstitution.class);
        // Set the Activity to start in a new, empty task
        /*notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
        res = context.getResources();
        try {
            JSONObject reponse = new JSONObject(intent.getStringExtra("message"));
            succes = reponse.getInt("succes");
            if(succes == 1) {
                message = reponse.getString("message");
                title = reponse.getString("name");
                photo = reponse.getString("photo");
                id_user_we_receved = reponse.getInt("ID");
                MessageCorps(res,message);
            } else {
                message = reponse.getString("message");
                title = "Wazzaby";
                photo = reponse.getString("photo");
                id_user_we_receved = reponse.getInt("ID");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        notifyIntent.putExtra("imageview",photo);
        notifyIntent.putExtra("name",title);
        // Create the PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );


        Drawable d = res.getDrawable(R.drawable.ic_profile_colorier);
        Bitmap myBitmap = ((BitmapDrawable)d).getBitmap();

        Notification notification = new NotificationCompat.Builder(context,CHANNEL_1_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(notifyPendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setLargeIcon(myBitmap)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setColor(Color.BLUE)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, notification);

        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Notification notification_inferieur_oreo = new NotificationCompat.Builder(context,CHANNEL_1_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setContentIntent(notifyPendingIntent)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .setLargeIcon(myBitmap)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(message))
                    .setColor(Color.BLUE)
                    .build();
            NotificationManager notificationManager_inferieur_oreo = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager_inferieur_oreo.notify(Config.NOTIFICATION_ID, notification_inferieur_oreo);
        }*/

        //Toast.makeText(context," "+id_user_we_receved,Toast.LENGTH_LONG).show();

    }

    public void MessageCorps(Resources res,String message) {
        Drawable bossdraw = res.getDrawable(R.drawable.rounded_corner);
        Drawable bossdraw2 = res.getDrawable(R.drawable.rounded_corner1);
        data_recyclerview.add(new Conversationprivateitem(photo,R.drawable.arrow_bg1,
                message,bossdraw,context_messageconstitution,R.drawable.arrow_bg2,
                photo,message,bossdraw2,true,false));



        if (etat_du_boss == 1 && ID_USER_ONFOCUS == id_user_we_receved) {
            smoothScroller.setTargetPosition((data_recyclerview.size()-1));
            layoutManager.startSmoothScroll(smoothScroller);
            allUsersAdapter.notifyDataSetChanged();
        }


    }
}
