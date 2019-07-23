package com.wazzaby.android.wazzaby.fragments;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wazzaby.android.wazzaby.R;

import org.json.JSONObject;

public class Accueil extends Fragment {

    private BottomNavigationView navigation;
    private Resources res;
    private Menu menu;
    private SpannableString annonce_title_text;
    private SpannableString recherche_title_text;
    private SpannableString notification_title_text;
    private Drawable Icon_notification;
    private Drawable Icon_recherche;
    private Drawable Icon_annonce;
    private JSONObject reponse;
    private static int mCartItemCount = 0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.accueil, container, false);
        res = getResources();
        navigation =  inflatedView.findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        menu = navigation.getMenu();
        Icon_notification = res.getDrawable(R.drawable.ic_notifications_black_24dp);
        Icon_recherche = res.getDrawable(R.drawable.ic_question_answer_black_24dp);
        Icon_annonce = res.getDrawable(R.drawable.baseline_chat_bubble_black_24);

        annonce_title_text = new SpannableString("Chat Privee");
        recherche_title_text = new SpannableString("Chat Public");
        notification_title_text = new SpannableString("Notifications");

        Icon_recherche.mutate().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        recherche_title_text.setSpan(new ForegroundColorSpan(res.getColor(R.color.colorPrimary)),0,recherche_title_text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        menu.findItem(R.id.conversationpublic).setIcon(Icon_recherche);
        menu.findItem(R.id.conversationpublic).setTitle(recherche_title_text);

        loadFragment(new Conversationspublic());

        BadgeDrawable badge1 = navigation.showBadge(R.id.notification);
        badge1.setNumber(10);
        badge1.setBadgeTextColor(Color.WHITE);

        BadgeDrawable badge2 = navigation.showBadge(R.id.conversationprivee);
        badge2.setNumber(5);
        badge2.setBadgeTextColor(Color.WHITE);

        return inflatedView;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.conversationpublic:
                    Icon_notification = res.getDrawable(R.drawable.ic_notifications_black_24dp);
                    Icon_recherche = res.getDrawable(R.drawable.ic_question_answer_black_24dp);
                    Icon_annonce = res.getDrawable(R.drawable.baseline_chat_bubble_black_24);
                    annonce_title_text = new SpannableString("Chat Privee");
                    recherche_title_text = new SpannableString("Chat Public");
                    notification_title_text = new SpannableString("Notifications");

                    Icon_recherche.mutate().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    recherche_title_text.setSpan(new ForegroundColorSpan(res.getColor(R.color.colorPrimary)),0,recherche_title_text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    menu.findItem(R.id.conversationpublic).setIcon(Icon_recherche);
                    menu.findItem(R.id.notification).setIcon(Icon_notification);
                    menu.findItem(R.id.conversationprivee).setIcon(Icon_annonce);

                    menu.findItem(R.id.conversationpublic).setTitle(recherche_title_text);
                    menu.findItem(R.id.notification).setTitle(notification_title_text);
                    menu.findItem(R.id.conversationprivee).setTitle(annonce_title_text);
                    fragment = new Conversationspublic();
                    loadFragment(fragment);
                    return true;
                case R.id.conversationprivee:
                    Icon_notification = res.getDrawable(R.drawable.ic_notifications_black_24dp);
                    Icon_recherche = res.getDrawable(R.drawable.ic_question_answer_black_24dp);
                    Icon_annonce = res.getDrawable(R.drawable.baseline_chat_bubble_black_24);
                    annonce_title_text = new SpannableString("Chat Privee");
                    recherche_title_text = new SpannableString("Chat Public");
                    notification_title_text = new SpannableString("Notifications");

                    Icon_annonce.mutate().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    annonce_title_text.setSpan(new ForegroundColorSpan(res.getColor(R.color.colorPrimary)),0,annonce_title_text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    menu.findItem(R.id.conversationprivee).setIcon(Icon_annonce);
                    menu.findItem(R.id.notification).setIcon(Icon_notification);
                    menu.findItem(R.id.conversationpublic).setIcon(Icon_recherche);


                    menu.findItem(R.id.notification).setTitle(notification_title_text);
                    menu.findItem(R.id.conversationpublic).setTitle(recherche_title_text);
                    menu.findItem(R.id.conversationprivee).setTitle(annonce_title_text);
                    fragment = new Conversationsprivee();
                    loadFragment(fragment);
                    navigation.removeBadge(R.id.conversationprivee);
                    return true;
                case R.id.notification:
                    Icon_notification = res.getDrawable(R.drawable.ic_notifications_black_24dp);
                    Icon_recherche = res.getDrawable(R.drawable.ic_question_answer_black_24dp);
                    Icon_annonce = res.getDrawable(R.drawable.baseline_chat_bubble_black_24);
                    annonce_title_text = new SpannableString("Chat Privee");
                    recherche_title_text = new SpannableString("Chat Public");
                    notification_title_text = new SpannableString("Notifications");

                    Icon_notification.mutate().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    notification_title_text.setSpan(new ForegroundColorSpan(res.getColor(R.color.colorPrimary)),0,notification_title_text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    menu.findItem(R.id.notification).setIcon(Icon_notification);
                    menu.findItem(R.id.conversationpublic).setIcon(Icon_recherche);
                    menu.findItem(R.id.conversationprivee).setIcon(Icon_annonce);

                    menu.findItem(R.id.notification).setTitle(notification_title_text);
                    menu.findItem(R.id.conversationprivee).setTitle(annonce_title_text);
                    menu.findItem(R.id.conversationpublic).setTitle(recherche_title_text);

                    fragment = new Notifications();
                    loadFragment(fragment);
                    navigation.removeBadge(R.id.notification);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
