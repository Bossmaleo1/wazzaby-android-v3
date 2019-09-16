package com.wazzaby.android.wazzaby.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.wazzaby.android.wazzaby.R;

import static com.wazzaby.android.wazzaby.fragments.Accueil.mCartItemCount;
import static com.wazzaby.android.wazzaby.fragments.Accueil.navigation;

public class MyReceiver extends BroadcastReceiver {

    /*private BottomNavigationView navigation;
    private int mCartcount;*/

    public MyReceiver() {
        /*this.navigation = navigation;
        this.mCartcount = mcartcount;*/
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,intent.getStringExtra("message"),Toast.LENGTH_LONG).show();
        /*int nombre = mCartItemCount;
        nombre++;*/
        mCartItemCount++;
        //mCartcount++;
        BadgeDrawable badge = navigation.showBadge(R.id.notification);
        badge.setNumber(mCartItemCount);
        badge.setBadgeTextColor(Color.WHITE);
        // Implement code here to be performed when
        // broadcast is detected
    }
}