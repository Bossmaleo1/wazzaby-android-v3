package com.wazzaby.android.wazzaby.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.snackbar.Snackbar;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.appviews.Home;
import com.wazzaby.android.wazzaby.appviews.NotificationIntent;
import com.wazzaby.android.wazzaby.appviews.Problematique;
import com.wazzaby.android.wazzaby.appviews.ProfilDetails;

import static com.wazzaby.android.wazzaby.fragments.Accueil.mCartItemCount;
import static com.wazzaby.android.wazzaby.fragments.Accueil.navigation;

public class MyReceiver extends BroadcastReceiver {


    private Snackbar snackbar;

    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        mCartItemCount++;
        BadgeDrawable badge = navigation.showBadge(R.id.notification);
        badge.setNumber(mCartItemCount);
        badge.setBadgeTextColor(Color.WHITE);

        Intent notifyIntent = new Intent(context, NotificationIntent.class);
// Set the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
// Create the PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );




        //Toast.makeText(context,intent.getStringExtra("message"),Toast.LENGTH_LONG).show();

        Notification notificationBuilder = new NotificationCompat.Builder(context, "channel_id")
                .setContentTitle("Wazzaby")
                .setContentText(intent.getStringExtra("message"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSmallIcon(R.drawable.wazaby)
                .setContentIntent(notifyPendingIntent)
                .setColor(Color.parseColor("#188dc8"))
                .setAutoCancel(true).build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        /*NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);*/

        notificationManager.notify(1, notificationBuilder);

        /*snackbar = Snackbar
                .make(Home.coordinatorLayout, intent.getStringExtra("message"), Snackbar.LENGTH_LONG)
                .setAction("Voir", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                    }
                });

        snackbar.show();*/
        // Implement code here to be performed when
        // broadcast is detected
    }

}