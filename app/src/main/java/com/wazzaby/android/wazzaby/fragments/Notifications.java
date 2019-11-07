package com.wazzaby.android.wazzaby.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import static com.wazzaby.android.wazzaby.appviews.Home.titlehome;

public class Notifications extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private SessionManager session;
    private DatabaseHandler database;
    private Resources res;
    private Profil user;
    private String url;
    private JSONObject object;
    private List<NotificationItem> data = new ArrayList<>();
    private List<NotificationItem> dataswiperefresh = new ArrayList<>();
    private NotificationAdapter allUsersAdapter;
    //private ProgressBar progressBar;
    private LinearLayout materialCardView;
    private RecyclerView recyclerView;
    private Snackbar snackbar;
    private TextView error_message;
    private CoordinatorLayout coordinatorLayout;
    private ShimmerFrameLayout mShimmerViewContainer;
    private String date;
    private int Countnotification;
    private int notification_id;
    private RelativeLayout notification_main_shimmer;
    /*cette variable est vrai lorsque le chargement et passe a false lorsque le chargement est sur le swiperefreshlayout*/
    private boolean swipestart = true;
    //On crée un compteur pour stabiliser le swiperefresh
    private int compteur_swiperefresh = 0;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        materialCardView = bossmaleo.findViewById(R.id.materialcardview);
        recyclerView = bossmaleo.findViewById(R.id.my_recycler_view);
        error_message = bossmaleo.findViewById(R.id.text_error_message);
        coordinatorLayout =  bossmaleo.findViewById(R.id.coordinatorLayout);
        notification_main_shimmer = bossmaleo.findViewById(R.id.notification_main_shimmer);

        database = new DatabaseHandler(getActivity());
        session = new SessionManager(getActivity());
        res = getResources();
        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
        url = Const.dns.concat("/WazzabyApi/public/api/displayNotification?id_recepteur=")
                .concat(String.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));


        mShimmerViewContainer = bossmaleo.findViewById(R.id.shimmer_view_container);
        swipeRefreshLayout =  bossmaleo.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        allUsersAdapter = new NotificationAdapter(getActivity(),data);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(allUsersAdapter);
        recyclerView.addOnItemTouchListener(new Notifications.RecyclerTouchListener(getActivity(), recyclerView, new Notifications.ClickListener() {
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

        this.ConnexionSynchronizationProblematique();

        ConnexionNotification();

        materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialCardView.setVisibility(View.GONE);
                ConnexionNotification();
            }
        });

        this.ConnexionSynchronizationProblematique();

        return bossmaleo;
    }

    public void ConnexionNotification() {

        //progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressBar.setVisibility(View.GONE);

                        swipestart = true;

                        try {
                            JSONArray reponse = new JSONArray(response);
                            if (reponse.length() == 0) {
                                error_message.setText(" Vous avez aucune notification");
                                materialCardView.setVisibility(View.VISIBLE);
                                notification_main_shimmer.setVisibility(View.GONE);
                            } else {

                                    object = reponse.getJSONObject(0);
                                    notification_main_shimmer.setVisibility(View.GONE);

                                    NotificationItem notificationItem
                                            = new NotificationItem(
                                            object.getInt("id_messagepublic"), object.getString("updated_messagepublic"), object.getString("libelle"), object.getString("updated"),
                                            object.getInt("etat"), object.getInt("id_type"), object.getInt("expediteur_id"), object.getInt("notification_id"), object.getInt("id_libelle"),
                                            object.getString("name_messagepublic"), object.getString("nom"), object.getString("prenom"), object.getString("photo"), object.getInt("countjaime"),
                                            object.getInt("countjaimepas"), object.getInt("checkmention"), object.getInt("id_checkmention"), object.getString("user_photo_messagepublic")
                                            , object.getString("status_text_content_messagepublic"), object.getString("etat_photo_status_messagepublic"),
                                            object.getString("status_photo_messagepublic"),0);
                                    data.add(notificationItem);

                                date = reponse.getJSONObject(0).getJSONObject("date").getString("date");
                                Countnotification = reponse.getJSONObject(0).getInt("countnotification");
                                notification_id = reponse.getJSONObject(0).getInt("notification_id");
                                if(swipestart) {
                                    ConnexionItemNotification();
                                }

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

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onRefresh() {

        /*mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();*/
        compteur_swiperefresh++;
        swipeRefreshLayout.setRefreshing(false);
        //On efface les deux listes de conversations publiques
        data.clear();
        dataswiperefresh.clear();
        materialCardView.setVisibility(View.GONE);
        notification_main_shimmer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        //on test si le compteur est paire
        if (compteur_swiperefresh%2 == 0) {
            swipestart = true;
            allUsersAdapter = new NotificationAdapter(getActivity(),data);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(allUsersAdapter);
            allUsersAdapter.notifyDataSetChanged();
            mShimmerViewContainer.setVisibility(View.VISIBLE);
            mShimmerViewContainer.startShimmer();
            ConnexionNotification();
        } else {
            swipestart = false;
            allUsersAdapter = new NotificationAdapter(getActivity(),dataswiperefresh);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(allUsersAdapter);
            allUsersAdapter.notifyDataSetChanged();
            mShimmerViewContainer.setVisibility(View.VISIBLE);
            mShimmerViewContainer.startShimmer();
            ConnexionNotificationSwipeRefresh();
        }

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
                                        object.getString("status_photo_messagepublic"),0);

                                //shall we test if our data array is not empty
                                if(data.size()>0) {
                                    //we block our previous shimmer the shimmer of our last item
                                    data.get((data.size()-1)).setState_shimmer(0);
                                }

                                //we set our current shimmer to display the shimmer in our item
                                notificationItem.setState_shimmer(1);
                                data.add(notificationItem);
                                date = reponse.getJSONObject(0).getJSONObject("date").getString("date");
                                notification_id = reponse.getJSONObject(0).getInt("notification_id");
                                if(swipestart) {
                                    ConnexionItemNotification();
                                }

                            }

                            if (tempcountitemp == 1) {
                                data.get((data.size() - 1)).setState_shimmer(0);
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

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }



    //Cette methode assure la synchronization après une mise à jour de problématique
    public void ConnexionSynchronizationProblematique() {
        String url_sendkey = Const.dns.concat("/WazzabyApi/public/api/SynchronizationProblematique?user_id=").concat(String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getID()));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_sendkey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject problematique = new JSONObject(response);
                            String problematique_libelle = problematique.getString("problematique_libelle");
                            int id_prob = problematique.getInt("problematique_id");
                            user.setLibelle_prob(problematique_libelle);
                            user.setIDPROB(String.valueOf(id_prob));

                            database.UpdateIDPROB(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getID(),Integer.valueOf(user.getIDPROB()),user.getLibelle_prob());
                            titlehome.setTitle(user.getLibelle_prob());

                        }catch (JSONException e){
                            e.printStackTrace();
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

    /**/

    public void ConnexionNotificationSwipeRefresh() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        swipestart = false;

                        try {
                            JSONArray reponse = new JSONArray(response);
                            if (reponse.length() == 0) {
                                error_message.setText(" Vous avez aucune notification");
                                materialCardView.setVisibility(View.VISIBLE);
                                notification_main_shimmer.setVisibility(View.GONE);
                                mShimmerViewContainer.setVisibility(View.GONE);
                            } else {

                                object = reponse.getJSONObject(0);



                                NotificationItem notificationItem
                                        = new NotificationItem(
                                        object.getInt("id_messagepublic"), object.getString("updated_messagepublic"), object.getString("libelle"), object.getString("updated"),
                                        object.getInt("etat"), object.getInt("id_type"), object.getInt("expediteur_id"), object.getInt("notification_id"), object.getInt("id_libelle"),
                                        object.getString("name_messagepublic"), object.getString("nom"), object.getString("prenom"), object.getString("photo"), object.getInt("countjaime"),
                                        object.getInt("countjaimepas"), object.getInt("checkmention"), object.getInt("id_checkmention"), object.getString("user_photo_messagepublic")
                                        , object.getString("status_text_content_messagepublic"), object.getString("etat_photo_status_messagepublic"),
                                        object.getString("status_photo_messagepublic"),0);
                                dataswiperefresh.add(notificationItem);

                                date = reponse.getJSONObject(0).getJSONObject("date").getString("date");
                                Countnotification = reponse.getJSONObject(0).getInt("countnotification");
                                notification_id = reponse.getJSONObject(0).getInt("notification_id");
                                notification_main_shimmer.setVisibility(View.GONE);
                                mShimmerViewContainer.setVisibility(View.GONE);
                                if(swipestart==false) {
                                    ConnexionItemNotificationSwipeRefresh();
                                }

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
                                        //ConnexionNotification();
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

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void ConnexionItemNotificationSwipeRefresh() {
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
                                        object.getString("status_photo_messagepublic"),0);

                                notification_main_shimmer.setVisibility(View.GONE);
                                mShimmerViewContainer.setVisibility(View.GONE);

                                //shall we test if our data array is not empty
                                if(dataswiperefresh.size()>0) {
                                    //we block our previous shimmer the shimmer of our last item
                                    dataswiperefresh.get((dataswiperefresh.size()-1)).setState_shimmer(0);
                                }

                                //we set our current shimmer to display the shimmer in our item
                                notificationItem.setState_shimmer(1);
                                dataswiperefresh.add(notificationItem);
                                date = reponse.getJSONObject(0).getJSONObject("date").getString("date");
                                notification_id = reponse.getJSONObject(0).getInt("notification_id");
                                if(swipestart==false) {
                                    ConnexionItemNotificationSwipeRefresh();
                                }

                            }

                            if (tempcountitemp == 1) {
                                dataswiperefresh.get((dataswiperefresh.size() - 1)).setState_shimmer(0);
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
                                        //ConnexionNotification();
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

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
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
