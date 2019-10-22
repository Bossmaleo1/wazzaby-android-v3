package com.wazzaby.android.wazzaby.appviews;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.adapter.NotificationAdapter;
import com.wazzaby.android.wazzaby.fragments.Conversationspublic;
import com.wazzaby.android.wazzaby.fragments.Notifications;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.NotificationItem;
import com.wazzaby.android.wazzaby.model.data.Profil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationIntent extends AppCompatActivity {

    private Toolbar toolbar;
    private ShimmerFrameLayout mShimmerViewContainer;
    private SessionManager session;
    private DatabaseHandler database;
    private Resources res;
    private Profil user;
    private String url;
    private JSONObject object;
    private List<NotificationItem> data = new ArrayList<>();
    private NotificationAdapter allUsersAdapter;
    //private ProgressBar progressBar;
    private LinearLayout materialCardView;
    private RecyclerView recyclerView;
    private Snackbar snackbar;
    private TextView error_message;
    private CoordinatorLayout coordinatorLayout;
    private String date;
    private int Countnotification;
    private int notification_id;
    private RelativeLayout notification_main_shimmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationintent);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notifications");
        materialCardView = findViewById(R.id.materialcardview);
        recyclerView = findViewById(R.id.my_recycler_view);
        error_message = findViewById(R.id.text_error_message);
        coordinatorLayout =  findViewById(R.id.coordinatorLayout);
        notification_main_shimmer = findViewById(R.id.notification_main_shimmer);

        database = new DatabaseHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        res = getResources();
        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
        url = Const.dns.concat("/WazzabyApi/public/api/displayNotification?id_recepteur=")
                .concat(String.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));


        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        allUsersAdapter = new NotificationAdapter(this,data);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(allUsersAdapter);
        recyclerView.addOnItemTouchListener(new NotificationIntent.RecyclerTouchListener(getApplicationContext(), recyclerView, new NotificationIntent.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext(), NotificationsDetails.class);
                intent.putExtra("notificationitem",data.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        ConnexionNotification();

        materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialCardView.setVisibility(View.GONE);
                //progressBar.setVisibility(View.VISIBLE);
                ConnexionNotification();
            }
        });
    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private NotificationIntent.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final NotificationIntent.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }


    }


    public void ConnexionNotification() {

        //progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressBar.setVisibility(View.GONE);

                        try {
                            JSONArray reponse = new JSONArray(response);
                            if (reponse.length() == 0) {
                                //progressBar.setVisibility(View.GONE);
                                error_message.setText(" Vous avez aucune notification");
                                materialCardView.setVisibility(View.VISIBLE);
                                //progressBar.setVisibility(View.GONE);
                            } else {

                                //for (int i = 0; i < reponse.length(); i++) {
                                object = reponse.getJSONObject(0);



                                NotificationItem notificationItem
                                        = new NotificationItem(
                                        object.getInt("id_messagepublic"), object.getString("updated_messagepublic"), object.getString("libelle"), object.getString("updated"),
                                        object.getInt("etat"), object.getInt("id_type"), object.getInt("expediteur_id"), object.getInt("notification_id"), object.getInt("id_libelle"),
                                        object.getString("name_messagepublic"), object.getString("nom"), object.getString("prenom"), object.getString("photo"), object.getInt("countjaime"),
                                        object.getInt("countjaimepas"), object.getInt("checkmention"), object.getInt("id_checkmention"), object.getString("user_photo_messagepublic")
                                        , object.getString("status_text_content_messagepublic"), object.getString("etat_photo_status_messagepublic"),
                                        object.getString("status_photo_messagepublic"));
                                data.add(notificationItem);
                                //progressBar.setVisibility(View.GONE);
                                //}

                                date = reponse.getJSONObject(0).getJSONObject("date").getString("date");
                                Countnotification = reponse.getJSONObject(0).getInt("countnotification");
                                notification_id = reponse.getJSONObject(0).getInt("notification_id");
                                ConnexionItemNotification();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                        allUsersAdapter.notifyDataSetChanged();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error_message.setText(" Erreur reseaux, veuillez reessayer svp !");
                        materialCardView.setVisibility(View.VISIBLE);
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        notification_main_shimmer.setVisibility(View.GONE);
                        //progressBar.setVisibility(View.GONE);
                        snackbar = Snackbar
                                .make(coordinatorLayout, res.getString(R.string.error_volley_timeouterror), Snackbar.LENGTH_LONG)
                                .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        materialCardView.setVisibility(View.GONE);
                                        //progressBar.setVisibility(View.GONE);
                                        ConnexionNotification();
                                    }
                                });
                        snackbar.show();
                        //progressBar.setVisibility(View.GONE);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    //this web api help use to make our lazzy loading
    private void ConnexionItemNotification() {
        String url_lazy_loading = Const.dns.concat("/WazzabyApi/public/api/displayNotificationItem?id_recepteur=")
                .concat(String.valueOf(session.getUserDetail().get(SessionManager.Key_ID)))
                .concat("&date=").concat(String.valueOf(date))
                .concat("&notification_id=").concat(String.valueOf(notification_id));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_lazy_loading,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray reponse = new JSONArray(response);


                            object = reponse.getJSONObject(0);
                            int tempcountitemp = Integer.valueOf(Countnotification - data.size());

                            if (tempcountitemp >= 1) {
                                NotificationItem notificationItem
                                        = new NotificationItem(
                                        object.getInt("id_messagepublic"), object.getString("updated_messagepublic"), object.getString("libelle"), object.getString("updated"),
                                        object.getInt("etat"), object.getInt("id_type"), object.getInt("expediteur_id"), object.getInt("notification_id"), object.getInt("id_libelle"),
                                        object.getString("name_messagepublic"), object.getString("nom"), object.getString("prenom"), object.getString("photo"), object.getInt("countjaime"),
                                        object.getInt("countjaimepas"), object.getInt("checkmention"), object.getInt("id_checkmention"), object.getString("user_photo_messagepublic")
                                        , object.getString("status_text_content_messagepublic"), object.getString("etat_photo_status_messagepublic"),
                                        object.getString("status_photo_messagepublic"));
                                data.add(notificationItem);
                                date = reponse.getJSONObject(0).getJSONObject("date").getString("date");
                                notification_id = reponse.getJSONObject(0).getInt("notification_id");
                                ConnexionItemNotification();
                            }

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                        allUsersAdapter.notifyDataSetChanged();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error_message.setText(" Erreur reseaux, veuillez reessayer svp !");
                        materialCardView.setVisibility(View.VISIBLE);
                        //progressBar.setVisibility(View.GONE);
                        snackbar = Snackbar
                                .make(coordinatorLayout, res.getString(R.string.error_volley_timeouterror), Snackbar.LENGTH_LONG)
                                .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        materialCardView.setVisibility(View.GONE);
                                        //progressBar.setVisibility(View.GONE);
                                        ConnexionNotification();
                                    }
                                });
                        snackbar.show();
                        //progressBar.setVisibility(View.GONE);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                finish();
                return true;



            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmer();
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmer();
        super.onPause();
    }


}
