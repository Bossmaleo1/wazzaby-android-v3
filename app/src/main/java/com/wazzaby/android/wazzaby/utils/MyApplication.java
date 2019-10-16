package com.wazzaby.android.wazzaby.utils;

import android.app.Application;

import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;
import com.vanniktech.emoji.ios.IosEmojiProvider;
import com.vanniktech.emoji.twitter.TwitterEmojiProvider;
import com.wazzaby.android.wazzaby.R;

public class MyApplication extends Application {
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

        //EmojiManager.install(new IosEmojiProvider());
        EmojiManager.install(new GoogleEmojiProvider());
        //EmojiManager.install(new TwitterEmojiProvider());
    }
}
