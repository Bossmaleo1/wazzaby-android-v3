package com.wazzaby.android.wazzaby.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
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
import com.google.android.material.snackbar.Snackbar;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.adapter.NotificationAdapter;
import com.wazzaby.android.wazzaby.appviews.NotificationsDetails;
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

public class Notifications extends Fragment {

    private SessionManager session;
    private DatabaseHandler database;
    private Resources res;
    private Profil user;
    private String url;
    private JSONObject object;
    private List<NotificationItem> data = new ArrayList<>();
    private NotificationAdapter allUsersAdapter;
    private ProgressBar progressBar;
    private LinearLayout materialCardView;
    private RecyclerView recyclerView;
    private Snackbar snackbar;
    private TextView error_message;
    private CoordinatorLayout coordinatorLayout;

    public Notifications() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View bossmaleo = inflater.inflate(R.layout.notifications, container, false);
        progressBar = bossmaleo.findViewById(R.id.progressbar);
        materialCardView = bossmaleo.findViewById(R.id.materialcardview);
        recyclerView = bossmaleo.findViewById(R.id.my_recycler_view);
        error_message = bossmaleo.findViewById(R.id.text_error_message);
        coordinatorLayout =  bossmaleo.findViewById(R.id.coordinatorLayout);

        database = new DatabaseHandler(getActivity());
        session = new SessionManager(getActivity());
        res = getResources();
        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
        url = Const.dns.concat("/WazzabyApi/public/api/displayNotification?id_recepteur=")
                .concat(String.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        allUsersAdapter = new NotificationAdapter(getActivity(),data);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(allUsersAdapter);
        recyclerView.addOnItemTouchListener(new Conversationspublic.RecyclerTouchListener(getActivity(), recyclerView, new Conversationspublic.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getActivity(), NotificationsDetails.class);
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
                progressBar.setVisibility(View.VISIBLE);
                ConnexionNotification();
            }
        });

        return bossmaleo;
    }

    public void ConnexionNotification() {

        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONArray reponse = new JSONArray(response);

                            if (reponse.length() == 0) {
                                progressBar.setVisibility(View.GONE);
                                error_message.setText(" Vous avez aucune notification");
                                materialCardView.setVisibility(View.VISIBLE);
                            }

                            for(int i = 0;i<reponse.length();i++)
                            {
                                object = reponse.getJSONObject(i);

                                NotificationItem notificationItem
                                        = new NotificationItem (
                                        object.getInt("id_messagepublic"),object.getString("updated_messagepublic"),object.getString("libelle"),object.getString("updated"),
                                        object.getInt("etat"),object.getInt("id_type"),object.getInt("expediteur_id"),object.getInt("notification_id"),object.getInt("id_libelle"),
                                        object.getString("name_messagepublic"),object.getString("nom"),object.getString("prenom"),object.getString("photo"),object.getInt("countjaime"),
                                        object.getInt("countjaimepas"),object.getInt("checkmention"),object.getInt("id_checkmention"),object.getString("user_photo_messagepublic")
                                        ,object.getString("status_text_content_messagepublic"),object.getString("etat_photo_status_messagepublic"),
                                        object.getString("status_photo_messagepublic"));
                                data.add(notificationItem);
                                progressBar.setVisibility(View.GONE);
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
                        progressBar.setVisibility(View.GONE);
                        snackbar = Snackbar
                                .make(coordinatorLayout, res.getString(R.string.error_volley_timeouterror), Snackbar.LENGTH_LONG)
                                .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        materialCardView.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.GONE);
                                        ConnexionNotification();
                                    }
                                });
                        snackbar.show();
                        progressBar.setVisibility(View.GONE);
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

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private Notifications.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final Notifications.ClickListener clickListener) {
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


}
