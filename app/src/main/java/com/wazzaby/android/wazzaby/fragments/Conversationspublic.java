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
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
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
import com.google.android.material.card.MaterialCardView;
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
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout mShimmerViewContainer;
    private MaterialCardView materialcardview;
    private Profil user;
    private static int anonymous;

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
        //progressBar = (ProgressBar) bossmaleo.findViewById(R.id.progressbar);
        coordinatorLayout =  bossmaleo.findViewById(R.id.coordinatorLayout);
        recyclerView = bossmaleo.findViewById(R.id.my_recycler_view);
        swipeRefreshLayout =  bossmaleo.findViewById(R.id.swipe_refresh_layout);
        materialcardview = bossmaleo.findViewById(R.id.materialcardview);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        allUsersAdapter = new ConversationspublicAdapter(getActivity(),data);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(allUsersAdapter);
        ConnexionConversationsPublic();

        materialcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialcardview.setVisibility(View.GONE);
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

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Const.dns+"/WazzabyApi/public/api/displayPublicMessage?id_problematique="+String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getIDPROB()),
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
                        materialcardview.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(),"Une erreur reseau vient de se produire veuillez reessayer !!",Toast.LENGTH_LONG).show();
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
        Toast.makeText(getContext(),"Le machin vient de subir un refresh scarla!!!", Toast.LENGTH_LONG).show();
        swipeRefreshLayout.setRefreshing(false);
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

    public void ConnexionInsertNotification(int users_id, String libelle, int id_type,int id_recepteur,int anonymous)
    {
        String message = "Votre message public vient de faire reagir ".concat(user.getPRENOM()+" "+user.getNOM());
        String url_notification = Const.dns.concat("/WazzabyApi/public/api/InsertNotification?users_id=").concat(String.valueOf(session.getUserDetail().get(SessionManager.Key_ID)))
               .concat("&libelle=").concat(message).concat("&id_type=").concat(String.valueOf(id_type))
                .concat("&etat=0").concat("&id_recepteur=").concat(String.valueOf(id_recepteur)).concat("&anonymous=").concat(String.valueOf(anonymous));
    }

}
