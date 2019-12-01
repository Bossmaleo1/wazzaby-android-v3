package com.wazzaby.android.wazzaby.appviews;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.adapter.ConversationspublicAdapter;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.ConversationPublicItem;
import com.wazzaby.android.wazzaby.model.data.Profil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wazzaby.android.wazzaby.appviews.Home.titlehome;

public class Historique extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private Resources res;
    private JSONObject object;
    private Snackbar snackbar;
    private Context context;
    private DatabaseHandler database;
    private SessionManager session;
    private List<ConversationPublicItem> data = new ArrayList<>();
    private List<ConversationPublicItem> dataswiperefresh = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout mShimmerViewContainer;
    public static RecyclerView recyclerView;
    private ConversationspublicAdapter allUsersAdapter;

    private String dark_mode_item = null;

    /*cette variable est vrai lorsque le chargement et passe a false lorsque le chargement est sur le swiperefreshlayout*/
    private boolean swipestart = true;
    //On crée un compteur pour stabiliser le swiperefresh
    private int compteur_swiperefresh = 0;




    private String dateitem;
    private int countitem;
    private int publicconvert_id;
    private String libelleitem;
    private Profil user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        session = new SessionManager(this);
        database = new DatabaseHandler(this);
        res = getResources();
        dark_mode_item = database.getDARKMODE();
        //si le dark mode est activé
        if (dark_mode_item.equals("1"))
        {
            setTheme(R.style.AppDarkTheme);
            //edit_modenuit.setChecked(true);
        } else if (dark_mode_item.equals("0")) {
            setTheme(R.style.AppTheme);
            //edit_modenuit.setChecked(false);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.historique);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(res.getString(R.string.history));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;

        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        //progressBar = (ProgressBar) bossmaleo.findViewById(R.id.progressbar);
        coordinatorLayout =  findViewById(R.id.coordinatorLayout);
        recyclerView = findViewById(R.id.my_recycler_view);
        swipeRefreshLayout =  findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allUsersAdapter = new ConversationspublicAdapter(this,data);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(allUsersAdapter);
        ConnexionHistorique();
        this.ConnexionSynchronizationProblematique();
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

    private void ConnexionHistorique()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Const.dns+"/WazzabyApi/public/api/HistoriqueMessagePublic?id_problematique="+String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getIDPROB())+"&id_user="+String.valueOf(session.getUserDetail().get(SessionManager.Key_ID)),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        swipestart = true;
                        JSONArray reponse = null;
                        try {
                            reponse = new JSONArray(response);
                            object = reponse.getJSONObject(0);
                            String count = null;
                            if(object.getString("countcomment").equals("0") || object.getString("countcomment").equals("1"))
                            {
                                    count = object.getString("countcomment")+" "+res.getString(R.string.convertpublic_inter);
                            }else {
                                    count = object.getString("countcomment")+" "+res.getString(R.string.convertpublic_inter);
                            }


                            ConversationPublicItem conversationPublicItem = new ConversationPublicItem(context,object.getInt("user_id"),object.getInt("id")
                                        ,object.getString("status_text_content"),object.getString("name"),object.getString("updated")
                                        ,count,object.getString("user_photo"),R.drawable.baseline_add_comment_black_24
                                        ,object.getString("etat_photo_status"),object.getString("status_photo")
                                        ,0,object.getBoolean("visibility"),object.getInt("countjaime"),object.getInt("countjaimepas")
                                        ,object.getInt("id_recepteur"),object.getInt("checkmention"),object.getInt("id_checkmention"),object.getInt("id_photo"),"mettre la pushkey ici",0);
                            dateitem = object.getJSONObject("date").getString("date");
                            countitem = object.getInt("countmessagepublichistorique");
                            publicconvert_id = object.getInt("id");
                            libelleitem = object.getString("status_text_content");

                           /*- if(i == 2) {
                                    conversationPublicItem.setState_shimmer(1);
                            }*/
                             data.add(conversationPublicItem);
                            if(swipestart) {
                                ConnexionHistoriqueItem();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //progressBar.setVisibility(View.GONE);
                        allUsersAdapter.notifyDataSetChanged();
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error instanceof ServerError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_servererror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionHistorique();
                                        }
                                    });

                            snackbar.show();
                            //progressBar.setVisibility(View.GONE);
                        }else if(error instanceof NetworkError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_servererror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionHistorique();
                                        }
                                    });

                            snackbar.show();
                            //progressBar.setVisibility(View.GONE);
                        }else if(error instanceof AuthFailureError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_servererror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionHistorique();
                                        }
                                    });

                            snackbar.show();
                            //progressBar.setVisibility(View.GONE);
                        }else if(error instanceof ParseError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_servererror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionHistorique();
                                        }
                                    });

                            snackbar.show();
                            //progressBar.setVisibility(View.GONE);
                        }else if(error instanceof NoConnectionError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_noconnectionerror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionHistorique();
                                        }
                                    });

                            snackbar.show();
                            //progressBar.setVisibility(View.GONE);
                        }else if(error instanceof TimeoutError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_timeouterror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionHistorique();
                                        }
                                    });
                            snackbar.show();
                            //progressBar.setVisibility(View.GONE);
                        }else
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_error), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionHistorique();
                                        }
                                    });

                            snackbar.show();
                            //progressBar.setVisibility(View.GONE);
                        }
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                /*params.put("IDProb",String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getIDPROB()));
                params.put("web","0");*/
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void ConnexionHistoriqueItem() {
        String url_lazy_loading = Const.dns.concat("/WazzabyApi/public/api/HistoriqueMessagePublicItem?id_problematique=")
                .concat(String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getIDPROB()))
                .concat("&id_user=").concat(String.valueOf(session.getUserDetail().get(SessionManager.Key_ID)))
                .concat("&publicconvert_id=").concat(String.valueOf(publicconvert_id))
                .concat("&date=").concat(String.valueOf(dateitem))
                .concat("&libelle=").concat(String.valueOf(libelleitem));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_lazy_loading,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray reponse = new JSONArray(response);
                            object = reponse.getJSONObject(0);
                            int temp_countitem = Integer.valueOf(countitem - data.size());
                            if (temp_countitem >= 1) {
                                String count = null;
                                if(object.getString("countcomment").equals("0") || object.getString("countcomment").equals("1"))
                                {
                                    count = object.getString("countcomment")+" "+res.getString(R.string.convertpublic_inter);
                                }else {
                                    count = object.getString("countcomment")+" "+res.getString(R.string.convertpublic_inter);
                                }

                                ConversationPublicItem conversationPublicItem = new ConversationPublicItem(context,object.getInt("user_id"),object.getInt("id")
                                        ,object.getString("status_text_content"),object.getString("name"),object.getString("updated")
                                        ,count,object.getString("user_photo"),R.drawable.baseline_add_comment_black_24
                                        ,object.getString("etat_photo_status"),object.getString("status_photo")
                                        ,0,object.getBoolean("visibility"),object.getInt("countjaime"),object.getInt("countjaimepas")
                                        ,object.getInt("id_recepteur"),object.getInt("checkmention"),object.getInt("id_checkmention"),object.getInt("id_photo"),"mettre la pushkey ici",0);

                                dateitem = object.getJSONObject("date").getString("date");
                                publicconvert_id = object.getInt("id");
                                libelleitem = object.getString("status_text_content");
                                //shall we test if our data array is not empty
                                if(data.size()>0) {
                                    //we block our previous shimmer the shimmer of our last item
                                    data.get((data.size()-1)).setState_shimmer(0);
                                }

                                //we set our current shimmer to display the shimmer in our item
                                conversationPublicItem.setState_shimmer(1);


                                data.add(conversationPublicItem);
                                if(swipestart){
                                    ConnexionHistoriqueItem();
                                }
                            }

                            if (temp_countitem == 1) {
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
                        /*error_message.setText(" Erreur reseaux, veuillez reessayer svp !");
                        materialCardView.setVisibility(View.VISIBLE);
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
                        snackbar.show();*/
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRefresh() {
        compteur_swiperefresh++;
        swipeRefreshLayout.setRefreshing(false);
        //On efface les deux listes de conversations publiques
        data.clear();
        dataswiperefresh.clear();
        //on test si le compteur est paire
        if (compteur_swiperefresh%2 == 0) {
            swipestart = true;
            allUsersAdapter = new ConversationspublicAdapter(this,data);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(allUsersAdapter);
            allUsersAdapter.notifyDataSetChanged();
            mShimmerViewContainer.setVisibility(View.VISIBLE);
            ConnexionHistorique();
        } else {
            swipestart = false;
            allUsersAdapter = new ConversationspublicAdapter(this,dataswiperefresh);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(allUsersAdapter);
            allUsersAdapter.notifyDataSetChanged();
            mShimmerViewContainer.setVisibility(View.VISIBLE);
            ConnexionHistoriqueSwipeRefresh();
        }
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void ConnexionHistoriqueSwipeRefresh()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Const.dns+"/WazzabyApi/public/api/HistoriqueMessagePublic?id_problematique="+String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getIDPROB())+"&id_user="+String.valueOf(session.getUserDetail().get(SessionManager.Key_ID)),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        swipestart = false;
                        JSONArray reponse = null;
                        try {
                            reponse = new JSONArray(response);
                            object = reponse.getJSONObject(0);
                            String count = null;
                            if(object.getString("countcomment").equals("0") || object.getString("countcomment").equals("1"))
                            {
                                count = object.getString("countcomment")+" "+res.getString(R.string.convertpublic_inter);
                            }else {
                                count = object.getString("countcomment")+" "+res.getString(R.string.convertpublic_inter);
                            }


                            ConversationPublicItem conversationPublicItem = new ConversationPublicItem(context,object.getInt("user_id"),object.getInt("id")
                                    ,object.getString("status_text_content"),object.getString("name"),object.getString("updated")
                                    ,count,object.getString("user_photo"),R.drawable.baseline_add_comment_black_24
                                    ,object.getString("etat_photo_status"),object.getString("status_photo")
                                    ,0,object.getBoolean("visibility"),object.getInt("countjaime"),object.getInt("countjaimepas")
                                    ,object.getInt("id_recepteur"),object.getInt("checkmention"),object.getInt("id_checkmention"),object.getInt("id_photo"),"mettre la pushkey ici",0);
                            dateitem = object.getJSONObject("date").getString("date");
                            countitem = object.getInt("countmessagepublichistorique");
                            publicconvert_id = object.getInt("id");
                            libelleitem = object.getString("status_text_content");

                           /*- if(i == 2) {
                                    conversationPublicItem.setState_shimmer(1);
                            }*/
                            dataswiperefresh.add(conversationPublicItem);
                            if(swipestart==false) {
                                ConnexionHistoriqueItemSwipeRefresh();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //progressBar.setVisibility(View.GONE);
                        allUsersAdapter.notifyDataSetChanged();
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error instanceof ServerError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_servererror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionHistoriqueSwipeRefresh();
                                        }
                                    });

                            snackbar.show();
                            //progressBar.setVisibility(View.GONE);
                        }else if(error instanceof NetworkError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_servererror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionHistoriqueSwipeRefresh();
                                        }
                                    });

                            snackbar.show();
                            //progressBar.setVisibility(View.GONE);
                        }else if(error instanceof AuthFailureError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_servererror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionHistoriqueSwipeRefresh();
                                        }
                                    });

                            snackbar.show();
                            //progressBar.setVisibility(View.GONE);
                        }else if(error instanceof ParseError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_servererror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionHistoriqueSwipeRefresh();
                                        }
                                    });

                            snackbar.show();
                            //progressBar.setVisibility(View.GONE);
                        }else if(error instanceof NoConnectionError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_noconnectionerror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionHistoriqueSwipeRefresh();
                                        }
                                    });

                            snackbar.show();
                            //progressBar.setVisibility(View.GONE);
                        }else if(error instanceof TimeoutError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_timeouterror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionHistoriqueSwipeRefresh();
                                        }
                                    });
                            snackbar.show();
                            //progressBar.setVisibility(View.GONE);
                        }else
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_error), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionHistoriqueSwipeRefresh();
                                        }
                                    });

                            snackbar.show();
                            //progressBar.setVisibility(View.GONE);
                        }
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                /*params.put("IDProb",String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getIDPROB()));
                params.put("web","0");*/
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void ConnexionHistoriqueItemSwipeRefresh() {
        String url_lazy_loading = Const.dns.concat("/WazzabyApi/public/api/HistoriqueMessagePublicItem?id_problematique=")
                .concat(String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getIDPROB()))
                .concat("&id_user=").concat(String.valueOf(session.getUserDetail().get(SessionManager.Key_ID)))
                .concat("&publicconvert_id=").concat(String.valueOf(publicconvert_id))
                .concat("&date=").concat(String.valueOf(dateitem))
                .concat("&libelle=").concat(String.valueOf(libelleitem));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_lazy_loading,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray reponse = new JSONArray(response);
                            object = reponse.getJSONObject(0);
                            int temp_countitem = Integer.valueOf(countitem - data.size());
                            if (temp_countitem >= 1) {
                                String count = null;
                                if(object.getString("countcomment").equals("0") || object.getString("countcomment").equals("1"))
                                {
                                    count = object.getString("countcomment")+" "+res.getString(R.string.convertpublic_inter);
                                }else {
                                    count = object.getString("countcomment")+" "+res.getString(R.string.convertpublic_inter);
                                }

                                ConversationPublicItem conversationPublicItem = new ConversationPublicItem(context,object.getInt("user_id"),object.getInt("id")
                                        ,object.getString("status_text_content"),object.getString("name"),object.getString("updated")
                                        ,count,object.getString("user_photo"),R.drawable.baseline_add_comment_black_24
                                        ,object.getString("etat_photo_status"),object.getString("status_photo")
                                        ,0,object.getBoolean("visibility"),object.getInt("countjaime"),object.getInt("countjaimepas")
                                        ,object.getInt("id_recepteur"),object.getInt("checkmention"),object.getInt("id_checkmention"),object.getInt("id_photo"),"mettre la pushkey ici",0);

                                dateitem = object.getJSONObject("date").getString("date");
                                publicconvert_id = object.getInt("id");
                                libelleitem = object.getString("status_text_content");
                                //shall we test if our data array is not empty
                                if(dataswiperefresh.size()>0) {
                                    //we block our previous shimmer the shimmer of our last item
                                    dataswiperefresh.get((dataswiperefresh.size()-1)).setState_shimmer(0);
                                }

                                //we set our current shimmer to display the shimmer in our item
                                conversationPublicItem.setState_shimmer(1);


                                dataswiperefresh.add(conversationPublicItem);
                                if(swipestart==false){
                                    ConnexionHistoriqueItemSwipeRefresh();
                                }
                            }

                            if (temp_countitem == 1) {
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
                        /*error_message.setText(" Erreur reseaux, veuillez reessayer svp !");
                        materialCardView.setVisibility(View.VISIBLE);
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
                        snackbar.show();*/
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




}
