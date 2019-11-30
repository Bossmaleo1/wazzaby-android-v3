package com.wazzaby.android.wazzaby.fragments;

import android.content.BroadcastReceiver;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.adapter.ConversationspublicAdapter;
import com.wazzaby.android.wazzaby.appviews.Sharepublicconversation;
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

public class Conversationspublic extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    public static RecyclerView recyclerView;
    public static ConversationspublicAdapter allUsersAdapter;
    private FloatingActionButton fab;
    private CoordinatorLayout coordinatorLayout;
    private JSONObject object;
    private Snackbar snackbar;
    private Resources res;
    private Context context;
    private DatabaseHandler database;
    private SessionManager session;
    private List<ConversationPublicItem> data = new ArrayList<>();
    private List<ConversationPublicItem> dataswiperefresh = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout mShimmerViewContainer;
    private LinearLayout materialCardView;
    public static Profil user;
    private TextView text_error_message;
    private static int anonymous;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    //shall we declare our item attribut
    private String dateitem;
    private int countitem;
    private String libelleitem;
    private String anonymeitem;
    private int publicconvertitem_id;

    /*cette variable est vrai lorsque le chargement et passe a false lorsque le chargement est sur le swiperefreshlayout*/
    private boolean swipestart = true;
    //On crée un compteur pour stabiliser le swiperefresh
    private int compteur_swiperefresh = 0;

    /* this attribute it used to help use to stop
    * our lazzy loading */

    //private boolean swipetest = true;

    public Conversationspublic() {
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
        View bossmaleo = inflater.inflate(R.layout.conversationspublic, container, false);
        fab = bossmaleo.findViewById(R.id.fab);
        context = getActivity();
        res = getResources();
        session = new SessionManager(getActivity());
        database = new DatabaseHandler(getActivity());
        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
        mShimmerViewContainer = bossmaleo.findViewById(R.id.shimmer_view_container);
        text_error_message = bossmaleo.findViewById(R.id.text_error_message);
        coordinatorLayout =  bossmaleo.findViewById(R.id.coordinatorLayout);
        recyclerView = bossmaleo.findViewById(R.id.my_recycler_view);
        swipeRefreshLayout =  bossmaleo.findViewById(R.id.swipe_refresh_layout);
        materialCardView = bossmaleo.findViewById(R.id.materialcardview);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        allUsersAdapter = new ConversationspublicAdapter(getActivity(),data);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(allUsersAdapter);

        this.ConnexionSynchronizationProblematique();
        this.ConnexionConversationsPublic();

        materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialCardView.setVisibility(View.GONE);
                mShimmerViewContainer.startShimmer();
                mShimmerViewContainer.setVisibility(View.VISIBLE);
                ConnexionConversationsPublic();
            }
        });


        recyclerView.addOnItemTouchListener(new Conversationspublic.RecyclerTouchListener(getActivity(), recyclerView, new Conversationspublic.ClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Sharepublicconversation.class);
                startActivity(intent);
            }
        });


        return bossmaleo;
    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private Conversationspublic.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final Conversationspublic.ClickListener clickListener) {
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


    private void ConnexionConversationsPublic()
    {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Const.dns+"/WazzabyApi/public/api/displayPublicMessage?id_problematique="
                +String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getIDPROB())
                +"&id_user="+String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getID()),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        swipestart = true;
                        fab.setVisibility(View.VISIBLE);
                        JSONArray reponse = null;
                        try {

                            reponse = new JSONArray(response);

                            if(reponse.length()==0) {
                                text_error_message.setText(res.getString(R.string.error_empty_public_chat));
                                materialCardView.setVisibility(View.VISIBLE);
                            }

                           // Toast.makeText(getActivity(),"  "+response,Toast.LENGTH_LONG).show();

                            object = reponse.getJSONObject(0);
                            String count = null;
                            String status_photo = null;

                            if(object.getString("countcomment").equals("0") || object.getString("countcomment").equals("1")) {
                               count = object.getString("countcomment")+" "+res.getString(R.string.convertpublic_inter);
                            }else {
                               count = object.getString("countcomment")+" "+res.getString(R.string.convertpublic_inter);
                            }

                            ConversationPublicItem conversationPublicItem = new ConversationPublicItem(context,object.getInt("user_id"),object.getInt("id")
                                        ,object.getString("status_text_content"),object.getString("name"),object.getString("updated")
                                        ,count,object.getString("user_photo"),R.drawable.baseline_add_comment_black_24
                                        ,object.getString("etat_photo_status"),object.getString("status_photo")
                                        ,object.getInt("anonymous"),object.getBoolean("visibility"),object.getInt("countjaime"),object.getInt("countjaimepas")
                                        ,object.getInt("id_recepteur"),object.getInt("checkmention"),object.getInt("id_checkmention"),object.getInt("id_photo")
                                        ,object.getString("pushkey_recepteur"),0);
                            //shall we set our shimmer to 1 to display our progressbar
                            //conversationPublicItem.setState_shimmer(1);
                            data.add(conversationPublicItem);

                            dateitem = reponse.getJSONObject(0).getJSONObject("date").getString("date");
                            countitem = reponse.getJSONObject(0).getInt("countmessagepublicitem");
                            libelleitem = reponse.getJSONObject(0).getString("status_text_content");
                            anonymeitem = reponse.getJSONObject(0).getString("anonymous");
                            publicconvertitem_id = reponse.getJSONObject(0).getInt("id");




                            if(swipestart) {
                                ConnexionItemMessagePublic(Integer.valueOf(user.getIDPROB()), user.getID(),
                                        reponse.getJSONObject(0).getJSONObject("date").getString("date"),
                                        reponse.getJSONObject(0).getInt("id")
                                        ,reponse.getJSONObject(0).getString("status_text_content")
                                        ,reponse.getJSONObject(0).getString("anonymous"));
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
                        }else if(error instanceof NetworkError)
                        {
                        }else if(error instanceof AuthFailureError)
                        {
                        }else if(error instanceof ParseError)
                        {
                        }else if(error instanceof NoConnectionError)
                        {
                        }else if(error instanceof TimeoutError)
                        {
                        }else
                        {

                        }

                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        materialCardView.setVisibility(View.VISIBLE);
                        //Toast.makeText(getActivity(),"Une erreur reseau vient de se produire veuillez reessayer !!",Toast.LENGTH_LONG).show();
                        snackbar = Snackbar
                                .make(coordinatorLayout, res.getString(R.string.network_error), Snackbar.LENGTH_LONG)
                                .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        materialCardView.setVisibility(View.GONE);
                                        mShimmerViewContainer.startShimmer();
                                        mShimmerViewContainer.setVisibility(View.VISIBLE);
                                        ConnexionConversationsPublic();
                                    }
                                });

                        snackbar.show();
                        fab.setVisibility(View.GONE);
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

    public interface FragmentDrawerListener {
        void onDrawerItemSelected(View view, int position);
    }




    @Override
    public void onRefresh() {
        compteur_swiperefresh++;
        swipeRefreshLayout.setRefreshing(false);
        //On efface les deux listes de conversations publiques
        data.clear();
        dataswiperefresh.clear();
        materialCardView.setVisibility(View.GONE);
        //on test si le compteur est paire
        if (compteur_swiperefresh%2 == 0) {
            swipestart = true;
            allUsersAdapter = new ConversationspublicAdapter(getActivity(),data);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(allUsersAdapter);
            allUsersAdapter.notifyDataSetChanged();
            mShimmerViewContainer.setVisibility(View.VISIBLE);
            ConnexionConversationsPublic();
        } else {
            swipestart = false;
            allUsersAdapter = new ConversationspublicAdapter(getActivity(),dataswiperefresh);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(allUsersAdapter);
            allUsersAdapter.notifyDataSetChanged();
            mShimmerViewContainer.setVisibility(View.VISIBLE);
            ConnexionConversationsPublicSwipeRefresh();
        }




    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmer();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmer();
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }


    public void ConnexionItemMessagePublic(int id_problematique, int id_user,  String date, int publicconvertitem_idnew, String libelle,  String anonyme) {

        String url_lazy_loading = Const.dns.concat("/WazzabyApi/public/api/displayMessagePublicItem?id_problematique=")
                .concat(String.valueOf(id_problematique)).concat("&id_user=")
                .concat(String.valueOf(id_user))
                .concat("&date=")
                .concat(String.valueOf(date))
                .concat("&publicconvert_id=").concat(String.valueOf(publicconvertitem_idnew))
                .concat("&libelle=").concat(libelle)
                .concat("&anonyme=").concat(anonyme);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_lazy_loading,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONArray reponse = null;

                        try{

                            reponse = new JSONArray(response);
                            int temp_countitem = Integer.valueOf(countitem - data.size());

                            //Toast.makeText(getActivity(),"  "+response,Toast.LENGTH_LONG).show();

                            if(temp_countitem >= 1) {

                                object = reponse.getJSONObject(0);
                                String count = null;
                                if(object.getString("countcomment").equals("0") || object.getString("countcomment").equals("1"))
                                {
                                    count = object.getString("countcomment")+" "+res.getString(R.string.convertpublic_inter);
                                } else {
                                    count = object.getString("countcomment")+" "+res.getString(R.string.convertpublic_inter);
                                }

                                ConversationPublicItem conversationPublicItem = new ConversationPublicItem(context,object.getInt("user_id"),object.getInt("id")
                                        ,object.getString("status_text_content"),object.getString("name"),object.getString("updated")
                                        ,count,object.getString("user_photo"),R.drawable.baseline_add_comment_black_24
                                        ,object.getString("etat_photo_status"),object.getString("status_photo")
                                        ,object.getInt("anonymous"),object.getBoolean("visibility"),object.getInt("countjaime"),object.getInt("countjaimepas")
                                        ,object.getInt("id_recepteur"),object.getInt("checkmention"),object.getInt("id_checkmention"),object.getInt("id_photo")
                                        ,object.getString("pushkey_recepteur"),0);

                                libelleitem = object.getString("status_text_content");
                                anonymeitem = object.getString("anonymous");
                                publicconvertitem_id = object.getInt("id");
                                dateitem = reponse.getJSONObject(0).getJSONObject("date").getString("date");

                                //shall we test if our data array is not empty
                                if(data.size()>0) {
                                    //we block our previous shimmer the shimmer of our last item
                                    data.get((data.size()-1)).setState_shimmer(0);
                                }

                                //we set our current shimmer to display the shimmer in our item
                                conversationPublicItem.setState_shimmer(1);
                                //we add and launch our item loading if the user don't launch the refresh event
                                //if (swipetest) {
                                 data.add(conversationPublicItem);
                                 if(swipestart) {
                                 ConnexionItemMessagePublic(Integer.valueOf(user.getIDPROB()), user.getID(), dateitem,publicconvertitem_id,libelleitem,anonymeitem);
                                }

                            }


                            if (temp_countitem == 1) {
                                data.get((data.size() - 1)).setState_shimmer(0);
                            }




                        }catch(JSONException e){
                            e.printStackTrace();
                        }



                        allUsersAdapter.notifyDataSetChanged();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),res.getString(R.string.network_error1),Toast.LENGTH_LONG).show();
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


    //On écrit une deuxième méthode pour éviter l'instabilité
    private void ConnexionConversationsPublicSwipeRefresh()
    {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Const.dns+"/WazzabyApi/public/api/displayPublicMessage?id_problematique="
                +String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getIDPROB())
                +"&id_user="+String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getID()),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        swipestart = false;
                        fab.setVisibility(View.VISIBLE);
                        JSONArray reponse = null;
                        try {

                            reponse = new JSONArray(response);

                            if(reponse.length()==0) {
                                text_error_message.setText(res.getString(R.string.error_empty_public_chat));
                                materialCardView.setVisibility(View.VISIBLE);
                            }

                            // Toast.makeText(getActivity(),"  "+response,Toast.LENGTH_LONG).show();

                            object = reponse.getJSONObject(0);
                            String count = null;
                            String status_photo = null;

                            if(object.getString("countcomment").equals("0") || object.getString("countcomment").equals("1")) {
                                count = object.getString("countcomment")+" "+res.getString(R.string.convertpublic_inter);
                            }else {
                                count = object.getString("countcomment")+" "+res.getString(R.string.convertpublic_inter);
                            }

                            ConversationPublicItem conversationPublicItem = new ConversationPublicItem(context,object.getInt("user_id"),object.getInt("id")
                                    ,object.getString("status_text_content"),object.getString("name"),object.getString("updated")
                                    ,count,object.getString("user_photo"),R.drawable.baseline_add_comment_black_24
                                    ,object.getString("etat_photo_status"),object.getString("status_photo")
                                    ,object.getInt("anonymous"),object.getBoolean("visibility"),object.getInt("countjaime"),object.getInt("countjaimepas")
                                    ,object.getInt("id_recepteur"),object.getInt("checkmention"),object.getInt("id_checkmention"),object.getInt("id_photo")
                                    ,object.getString("pushkey_recepteur"),0);
                            //shall we set our shimmer to 1 to display our progressbar
                            //conversationPublicItem.setState_shimmer(1);
                            dataswiperefresh.add(conversationPublicItem);

                            dateitem = reponse.getJSONObject(0).getJSONObject("date").getString("date");
                            countitem = reponse.getJSONObject(0).getInt("countmessagepublicitem");
                            libelleitem = reponse.getJSONObject(0).getString("status_text_content");
                            anonymeitem = reponse.getJSONObject(0).getString("anonymous");
                            publicconvertitem_id = reponse.getJSONObject(0).getInt("id");




                           if(swipestart == false) {
                                ConnexionItemMessagePublicSwipeRefresh(Integer.valueOf(user.getIDPROB()), user.getID(),
                                        reponse.getJSONObject(0).getJSONObject("date").getString("date"),
                                        reponse.getJSONObject(0).getInt("id")
                                        ,reponse.getJSONObject(0).getString("status_text_content")
                                        ,reponse.getJSONObject(0).getString("anonymous"));
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
                        }else if(error instanceof NetworkError)
                        {
                        }else if(error instanceof AuthFailureError)
                        {
                        }else if(error instanceof ParseError)
                        {
                        }else if(error instanceof NoConnectionError)
                        {
                        }else if(error instanceof TimeoutError)
                        {
                        }else
                        {

                        }

                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        materialCardView.setVisibility(View.VISIBLE);


                        snackbar = Snackbar
                                .make(coordinatorLayout, res.getString(R.string.network_error), Snackbar.LENGTH_LONG)
                                .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        materialCardView.setVisibility(View.GONE);
                                        mShimmerViewContainer.startShimmer();
                                        mShimmerViewContainer.setVisibility(View.VISIBLE);
                                        ConnexionConversationsPublic();
                                    }
                                });

                        snackbar.show();
                        fab.setVisibility(View.GONE);
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

    //On recrée une version de ConnexionItemMessagePublic pour les messages publics
    public void ConnexionItemMessagePublicSwipeRefresh(int id_problematique, int id_user,  String date, int publicconvertitem_idnew, String libelle,  String anonyme) {

        String url_lazy_loading = Const.dns.concat("/WazzabyApi/public/api/displayMessagePublicItem?id_problematique=")
                .concat(String.valueOf(id_problematique)).concat("&id_user=")
                .concat(String.valueOf(id_user))
                .concat("&date=")
                .concat(String.valueOf(date))
                .concat("&publicconvert_id=").concat(String.valueOf(publicconvertitem_idnew))
                .concat("&libelle=").concat(libelle)
                .concat("&anonyme=").concat(anonyme);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_lazy_loading,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONArray reponse = null;

                        try{

                            reponse = new JSONArray(response);
                            int temp_countitem = Integer.valueOf(countitem - data.size());

                            //Toast.makeText(getActivity(),"  "+response,Toast.LENGTH_LONG).show();

                            if(temp_countitem >= 1) {

                                object = reponse.getJSONObject(0);
                                String count = null;
                                if(object.getString("countcomment").equals("0") || object.getString("countcomment").equals("1"))
                                {
                                    count = object.getString("countcomment")+" "+res.getString(R.string.convertpublic_inter);
                                } else {
                                    count = object.getString("countcomment")+" "+res.getString(R.string.convertpublic_inter);
                                }

                                ConversationPublicItem conversationPublicItem = new ConversationPublicItem(context,object.getInt("user_id"),object.getInt("id")
                                        ,object.getString("status_text_content"),object.getString("name"),object.getString("updated")
                                        ,count,object.getString("user_photo"),R.drawable.baseline_add_comment_black_24
                                        ,object.getString("etat_photo_status"),object.getString("status_photo")
                                        ,object.getInt("anonymous"),object.getBoolean("visibility"),object.getInt("countjaime"),object.getInt("countjaimepas")
                                        ,object.getInt("id_recepteur"),object.getInt("checkmention"),object.getInt("id_checkmention"),object.getInt("id_photo")
                                        ,object.getString("pushkey_recepteur"),0);

                                libelleitem = object.getString("status_text_content");
                                anonymeitem = object.getString("anonymous");
                                publicconvertitem_id = object.getInt("id");
                                dateitem = reponse.getJSONObject(0).getJSONObject("date").getString("date");

                                //shall we test if our data array is not empty
                                if(dataswiperefresh.size()>0) {
                                    //we block our previous shimmer the shimmer of our last item
                                    dataswiperefresh.get((dataswiperefresh.size()-1)).setState_shimmer(0);
                                }

                                //we set our current shimmer to display the shimmer in our item
                                conversationPublicItem.setState_shimmer(1);
                                //we add and launch our item loading if the user don't launch the refresh event
                                //if (swipetest) {
                                dataswiperefresh.add(conversationPublicItem);
                                if(swipestart == false) {
                                    ConnexionItemMessagePublicSwipeRefresh(Integer.valueOf(user.getIDPROB()), user.getID(), dateitem,publicconvertitem_id,libelleitem,anonymeitem);
                                }

                            }


                            if (temp_countitem == 1) {
                                dataswiperefresh.get((dataswiperefresh.size() - 1)).setState_shimmer(0);
                            }




                        }catch(JSONException e){
                            e.printStackTrace();
                        }



                        allUsersAdapter.notifyDataSetChanged();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),res.getString(R.string.network_error1),Toast.LENGTH_LONG).show();
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
