package com.wazzaby.android.wazzaby.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.adapter.categorieProbAdapter;
import com.wazzaby.android.wazzaby.appviews.CategorieProblematique;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.Categorie_prob;
import com.wazzaby.android.wazzaby.model.data.Profil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wazzaby.android.wazzaby.appviews.Home.titlehome;

public class Problematique extends Fragment {

    private RecyclerView recyclerView;
    private categorieProbAdapter allUsersAdapter;
    private List<Categorie_prob> data = new ArrayList<>();
    private ProgressBar progressBar;
    private JSONObject object;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar snackbar;
    private Resources res;
    private DatabaseHandler database;
    private FloatingActionButton fab;
    private EditText searchview;
    private String max_request="0";
    private LinearLayout search_block;
    private LinearLayout materialcardview;
    private SessionManager session;
    private Profil user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.problematique, container, false);
        recyclerView =  rootView.findViewById(R.id.my_recycler_view);
        searchview = rootView.findViewById(R.id.searchview);
        progressBar =  rootView.findViewById(R.id.progressbar);
        coordinatorLayout =  rootView.findViewById(R.id.coordinatorLayout);
        search_block = rootView.findViewById(R.id.search_block);
        materialcardview = rootView.findViewById(R.id.materialcardview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        res = getResources();
        database = new DatabaseHandler(getActivity());
        session = new SessionManager(getActivity());
        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
        //data = database.getAllCATProb();
        // TestProgressBar();

        allUsersAdapter = new categorieProbAdapter(getActivity(), data);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(allUsersAdapter);
        fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getActivity(), AddProblematique.class);
                startActivity(intent);*/
            }
        });
        this.ConnexionSynchronizationProblematique();
        ConnexionProblematique();
        recyclerView.addOnItemTouchListener(new Problematique.RecyclerTouchListener(getActivity(), recyclerView, new Problematique.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getActivity(), CategorieProblematique.class);
                intent.putExtra("IDCAT", String.valueOf(data.get(position).getID()));
                intent.putExtra("TITLE", String.valueOf(data.get(position).getLibelle()));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        materialcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialcardview.setVisibility(View.GONE);
                ConnexionProblematique();
            }
        });




        // Inflate the layout for this fragment
        return rootView;
    }


    private void ConnexionProblematique()
    {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Const.dns+"/WazzabyApi/public/api/displayAllcatprob",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray reponse = new JSONArray(response);
                            for(int i = 0;i<reponse.length();i++)
                            {
                                object = reponse.getJSONObject(i);
                                max_request = String.valueOf(object.getInt("ID"));
                                data.add(new Categorie_prob(object.getInt("ID"), object.getString("Libelle"),object.getString("Lang")));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);
                        search_block.setVisibility(View.VISIBLE);
                        allUsersAdapter.notifyDataSetChanged();
                        //recursive();

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

                                            materialcardview.setVisibility(View.GONE);
                                            ConnexionProblematique();
                                        }
                                    });

                            snackbar.show();
                            progressBar.setVisibility(View.GONE);
                        }else if(error instanceof NetworkError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_servererror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            materialcardview.setVisibility(View.GONE);
                                            ConnexionProblematique();
                                        }
                                    });

                            snackbar.show();
                            progressBar.setVisibility(View.GONE);
                        }else if(error instanceof AuthFailureError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_servererror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            materialcardview.setVisibility(View.GONE);
                                            ConnexionProblematique();
                                        }
                                    });

                            snackbar.show();
                            progressBar.setVisibility(View.GONE);
                        }else if(error instanceof ParseError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_servererror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            materialcardview.setVisibility(View.GONE);
                                            ConnexionProblematique();
                                        }
                                    });

                            snackbar.show();
                            progressBar.setVisibility(View.GONE);
                        }else if(error instanceof NoConnectionError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_noconnectionerror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            materialcardview.setVisibility(View.GONE);
                                            ConnexionProblematique();
                                        }
                                    });

                            snackbar.show();
                            progressBar.setVisibility(View.GONE);
                        }else if(error instanceof TimeoutError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_timeouterror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            materialcardview.setVisibility(View.GONE);
                                            ConnexionProblematique();
                                        }
                                    });
                            snackbar.show();
                            progressBar.setVisibility(View.GONE);
                        }else
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_error), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            materialcardview.setVisibility(View.GONE);
                                            ConnexionProblematique();
                                        }
                                    });

                            snackbar.show();
                            progressBar.setVisibility(View.GONE);
                        }

                        materialcardview.setVisibility(View.VISIBLE);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("ID","0");
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private Problematique.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final Problematique.ClickListener clickListener) {
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

    public void recursive()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.URL_PROBLEMATIQUE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray reponse = new JSONArray(response);
                            for(int i = 0;i<reponse.length();i++)
                            {
                                object = reponse.getJSONObject(i);
                                max_request = String.valueOf(object.getInt("ID"));
                                data.add(new Categorie_prob(object.getInt("ID"), object.getString("LIBELLE"),object.getString("Lang")));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        searchview.setVisibility(View.VISIBLE);
                        allUsersAdapter.notifyDataSetChanged();

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
                                            ConnexionProblematique();
                                        }
                                    });

                            snackbar.show();
                        }else if(error instanceof NetworkError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_servererror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionProblematique();
                                        }
                                    });

                            snackbar.show();
                        }else if(error instanceof AuthFailureError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_servererror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionProblematique();
                                        }
                                    });

                            snackbar.show();
                        }else if(error instanceof ParseError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_servererror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionProblematique();
                                        }
                                    });

                            snackbar.show();
                        }else if(error instanceof NoConnectionError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_noconnectionerror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionProblematique();
                                        }
                                    });

                            snackbar.show();
                        }else if(error instanceof TimeoutError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_timeouterror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionProblematique();
                                        }
                                    });
                            snackbar.show();
                        }else
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_error), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ConnexionProblematique();
                                        }
                                    });

                            snackbar.show();
                        }
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("ID",max_request);
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


}
