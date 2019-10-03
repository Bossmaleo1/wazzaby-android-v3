package com.wazzaby.android.wazzaby.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.broadcast.MyReceiver;
import com.wazzaby.android.wazzaby.model.Config;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.Profil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.facebook.imagepipeline.nativecode.NativeJpegTranscoder.TAG;

public class Accueil extends Fragment {

    public static BottomNavigationView navigation;
    private Resources res;
    private Menu menu;
    private SpannableString annonce_title_text;
    private SpannableString recherche_title_text;
    private SpannableString notification_title_text;
    private Drawable Icon_notification;
    private Drawable Icon_recherche;
    private Drawable Icon_annonce;
    private JSONObject reponse;
    public static int mCartItemCount = 0;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String Keypush = null;
    private SessionManager session;
    private DatabaseHandler database;
    private Profil user;
    public static BadgeDrawable badge;

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
        //Icon_recherche.setColorFilter(getResources().getColor(R.color.primarygraydark), PorterDuff.Mode.SRC_IN);

        //Shall we make gray color on our other icon and text title
        Icon_annonce.setColorFilter(getResources().getColor(R.color.gray_strong), PorterDuff.Mode.SRC_IN);
        Icon_notification.setColorFilter(getResources().getColor(R.color.gray_strong), PorterDuff.Mode.SRC_IN);

        //here we are set our icon and text title to update it
        menu.findItem(R.id.conversationpublic).setIcon(Icon_recherche);
        menu.findItem(R.id.conversationpublic).setTitle(recherche_title_text);

        menu.findItem(R.id.conversationprivee).setIcon(Icon_annonce);
        menu.findItem(R.id.conversationprivee).setTitle(annonce_title_text);

        menu.findItem(R.id.notification).setIcon(Icon_notification);
        menu.findItem(R.id.notification).setTitle(notification_title_text);

        loadFragment(new Conversationspublic());
        BadgeDrawable badge2 = navigation.showBadge(R.id.conversationprivee);
        badge2.setNumber(5);
        badge2.setBadgeTextColor(Color.WHITE);
        database = new DatabaseHandler(getActivity());
        session = new SessionManager(getActivity());
        res = getResources();
        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));

        /*IntentFilter filter = new IntentFilter("com.wazzaby.android.wazzaby.broadcast");

        MyReceiver receiver = new MyReceiver(navigation,mCartItemCount);
        registerReceiver(receiver, filter);*/

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("message");
                Toast.makeText(getActivity(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                mCartItemCount++;
                badge = navigation.showBadge(R.id.notification);
                badge.setNumber(mCartItemCount);
                badge.setBadgeTextColor(Color.WHITE);

                Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
            }
        };

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        Log.d(TAG, token);
                        Keypush = token;
                        Connexion();
                    }
                });

        ConnexionCountNotification();

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

                    Icon_notification.setColorFilter(getResources().getColor(R.color.gray_strong), PorterDuff.Mode.SRC_IN);
                    Icon_annonce.setColorFilter(getResources().getColor(R.color.gray_strong), PorterDuff.Mode.SRC_IN);

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

                    Icon_notification.setColorFilter(getResources().getColor(R.color.gray_strong), PorterDuff.Mode.SRC_IN);
                    Icon_recherche.setColorFilter(getResources().getColor(R.color.gray_strong), PorterDuff.Mode.SRC_IN);

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

                    Icon_annonce.setColorFilter(getResources().getColor(R.color.gray_strong), PorterDuff.Mode.SRC_IN);
                    Icon_recherche.setColorFilter(getResources().getColor(R.color.gray_strong), PorterDuff.Mode.SRC_IN);


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

    @Override
    public void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onStop();
    }

    private void Connexion()
    {
        String url_sendkey = Const.dns.concat("/WazzabyApi/public/api/UpdateKeyPush?ID=").concat(String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getID())).concat("&PUSHKEY=").concat(Keypush);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_sendkey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //database
                        database.UpdateKeyPush(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getID(),Keypush);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void ConnexionCountNotification() {
        String count_notification_url = Const.dns
                .concat("/WazzabyApi/public/api/CountNotification?id_recepteur=")
                .concat(String.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, count_notification_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(getActivity(),String.valueOf(response),Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //Toast.makeText(getActivity(),String.valueOf(jsonObject.getInt("count")),Toast.LENGTH_LONG).show();
                            mCartItemCount = jsonObject.getInt("count");
                            /*BadgeDrawable badge = navigation.showBadge(R.id.notification);
                            badge.setNumber(mCartItemCount);
                            badge.setBadgeTextColor(Color.WHITE);*/
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                        if(mCartItemCount == 0){
                            navigation.removeBadge(R.id.notification);
                        }else {
                            BadgeDrawable badge = navigation.showBadge(R.id.notification);
                            badge.setNumber(mCartItemCount);
                            badge.setBadgeTextColor(Color.WHITE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


}
