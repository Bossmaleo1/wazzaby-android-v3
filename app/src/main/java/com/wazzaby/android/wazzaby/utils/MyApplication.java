package com.wazzaby.android.wazzaby.utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.wazzaby.android.wazzaby.R;

import net.gotev.uploadservice.BuildConfig;
import net.gotev.uploadservice.UploadService;

public class MyApplication extends Application {

    public static  final String CHANNEL_1_ID = "channel1";
    public static  final String CHANNEL_2_ID = "channel2";

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        FontRequest fontRequest = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                R.array.com_google_android_gms_fonts_certs);
        EmojiCompat.Config config = new FontRequestEmojiCompatConfig(this, fontRequest);
        EmojiCompat.init(config);

        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;

        CreateNotificationChannels();
    }


    private void CreateNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_2_ID,"channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("this is channel 1");

            NotificationChannel channel2 = new NotificationChannel(CHANNEL_1_ID,"channel 2", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("this is channel 2");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }
}
