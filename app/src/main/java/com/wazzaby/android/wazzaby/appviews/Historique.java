package com.wazzaby.android.wazzaby.appviews;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Historique extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private Resources res;
    //private ProgressBar progressBar;
    private JSONObject object;
    private Snackbar snackbar;
    private Context context;
    private DatabaseHandler database;
    private SessionManager session;
    private List<ConversationPublicItem> data = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout mShimmerViewContainer;
    public static RecyclerView recyclerView;
    private ConversationspublicAdapter allUsersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historique);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        res = getResources();
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(res.getString(R.string.history));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        res = getResources();
        session = new SessionManager(this);
        database = new DatabaseHandler(this);
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
        //progressBar.setVisibility(View.VISIBLE);
        //TestProgressBar();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Const.dns+"/WazzabyApi/public/api/HistoriqueMessagePublic?id_problematique="+String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getIDPROB())
                +"&id_user="+String.valueOf(session.getUserDetail().get(SessionManager.Key_ID)),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONArray reponse = null;
                        try {
                            reponse = new JSONArray(response);
                            for(int i = 0;i<reponse.length();i++)
                            {
                                object = reponse.getJSONObject(i);
                                String count = null;
                                String status_photo = null;
                                if(object.getString("countcomment").equals("0") || object.getString("countcomment").equals("1"))
                                {
                                    count = object.getString("countcomment")+" "+res.getString(R.string.convertpublic_inter);
                                }else
                                {
                                    count = object.getString("countcomment")+" "+res.getString(R.string.convertpublic_inter);
                                }

                                ConversationPublicItem conversationPublicItem = new ConversationPublicItem(context,object.getInt("user_id"),object.getInt("id")
                                        ,object.getString("status_text_content"),object.getString("name"),object.getString("updated")
                                        ,count,object.getString("user_photo"),R.drawable.baseline_add_comment_black_24
                                        ,object.getString("etat_photo_status"),object.getString("status_photo")
                                        ,object.getInt("anonymous"),object.getBoolean("visibility"),object.getInt("countjaime"),object.getInt("countjaimepas")
                                        ,object.getInt("id_recepteur"),object.getInt("checkmention"),object.getInt("id_checkmention"),object.getInt("id_photo"));
                                data.add(conversationPublicItem);
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
        Toast.makeText(getApplicationContext(),"Le machin vient de subir un refresh scarla!!!", Toast.LENGTH_LONG).show();
        swipeRefreshLayout.setRefreshing(false);
    }
}
