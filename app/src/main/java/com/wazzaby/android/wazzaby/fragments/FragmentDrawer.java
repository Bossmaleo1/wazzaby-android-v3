package com.wazzaby.android.wazzaby.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.adapter.NavigationDrawerAdapter;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.DrawerItem;
import com.wazzaby.android.wazzaby.model.data.Profil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentDrawer extends Fragment {

    private static String TAG = FragmentDrawer.class.getSimpleName();
    public static TextView nom;
    private DatabaseHandler database;
    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private View containerView;
    private static String[] titles = null;
    private FragmentDrawerListener drawerListener;
    private SessionManager session;
    public static Profil user;
    public static SimpleDraweeView imageView;
    public static SwitchMaterial switchforanonymousmode;
    private ProgressDialog pDialog;
    private Resources res;
    private RelativeLayout block_globale;

    private String dark_mode_item = null;

    private RelativeLayout nav_header_container;

    public FragmentDrawer() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public static List<DrawerItem> getData() {
        List<DrawerItem> data = new ArrayList<>();


        // preparing navigation drawer items
        for (int i = 0; i < titles.length; i++) {
            DrawerItem navItem = new DrawerItem();
            navItem.setTitle(titles[i]);
            if(i == 0)
            {
                navItem.setImageID(R.drawable.ic_home_black_18dp);
            }else if(i==1) {
                navItem.setImageID(R.drawable.ic_subject_black_18dp);

            }
            data.add(navItem);
        }
        return data;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // drawer labels
        titles = getActivity().getResources().getStringArray(R.array.nav_drawer_labels);
        database = new DatabaseHandler(getActivity());
        session = new SessionManager(getActivity());
        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
        res = getResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_drawer_navigation, container, false);
        recyclerView =  layout.findViewById(R.id.drawerList);
        nom = layout.findViewById(R.id.welcome_msg);
        imageView = layout.findViewById(R.id.ic_profile);
        switchforanonymousmode = layout.findViewById(R.id.compatSwitch);
        nav_header_container = layout.findViewById(R.id.nav_header_container);
        block_globale = layout.findViewById(R.id.block_globale);

        nom.setText(user.getPRENOM()+" "+user.getNOM());

        dark_mode_item = database.getDARKMODE();

        if(!user.getPHOTO().equals("null") && !user.getPHOTO().isEmpty()) {
            if(user.getPHOTO().equals("yo")) {
                imageView.setImageResource(R.drawable.ic_profile);
            }else {
                Uri uri = Uri.parse(Const.dns+"/uploads/photo_de_profil/" + user.getPHOTO());
                imageView.setImageURI(uri);
            }
        }else
        {
            imageView.setImageResource(R.drawable.ic_profile);
        }

        if (dark_mode_item.equals("1"))
        {
            //setTheme(R.style.AppDarkTheme);
            //edit_modenuit.setChecked(true);
            block_globale.setBackgroundColor(getResources().getColor(R.color.darkprimarydark));
            nav_header_container.setBackgroundColor(getResources().getColor(R.color.darkprimary));
        } else if (dark_mode_item.equals("0")) {
            //setTheme(R.style.AppTheme);
            //edit_modenuit.setChecked(false);
        }

        adapter = new NavigationDrawerAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position);
                mDrawerLayout.closeDrawer(containerView);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        switchforanonymousmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {



                /*if (!stabilasationanymousmode)
                {*/
                    //on active la barre de progression
                    /*pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage("Connexion en cours...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();
                    if (isChecked) {*/
                        //On active le mode anonymous si l'utilisateur accepte d'activer le mode anonyme
                        /*VolleyActivateAnonymousMode();
                    } else {*/
                        //On désactive le mode anonymous si l'utilisateur accepte de désactiver le mode anonyme
                       /* VolleyDisabledAnonymousMode();
                    }
                }*/


            }
        });

        //Mise en place de l'évenement permettant de gérer le mode anonymous
        switchforanonymousmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage(res.getString(R.string.loading));
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();

                if (((SwitchMaterial) view).isChecked()) {
                    //On active le mode anonymous si l'utilisateur accepte d'activer le mode anonyme
                    VolleyActivateAnonymousMode();
                } else {
                    //On désactive le mode anonymous si l'utilisateur accepte de désactiver le mode anonyme
                    VolleyDisabledAnonymousMode();
                }

            }

        });

        if (user.getETAT().equals("1")) {
            imageView.setImageResource(R.drawable.ic_profile_anonymous);
        }

        //Toast.makeText(getActivity()," "+user.getETAT(),Toast.LENGTH_LONG).show();

        return layout;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
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

    public interface FragmentDrawerListener {
        void onDrawerItemSelected(View view, int position);
    }

    //La requête HTTP pour activer le mode Anonymous
    public void VolleyActivateAnonymousMode() {

        String url_activateAnonymousMode = Const.dns+"/WazzabyApi/public/api/AnonymousModeActivation?user_id=" + String.valueOf(user.getID()) + "&etat=1";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_activateAnonymousMode,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getActivity(),res.getString(R.string.activation_mode_anonyme),Toast.LENGTH_LONG).show();
                        database.UpdateAnonymousMode(user.getID(),"Anonyme","Utilisateur","","1");
                        imageView.setImageResource(R.drawable.ic_profile_anonymous);
                        user.setPRENOM("Utilisateur");
                        user.setNOM("Anonyme");
                        user.setPHOTO("");
                        user.setETAT("1");
                        nom.setText(user.getPRENOM()+" "+user.getNOM());
                        //setUserObjectInHomeNotificationProblematiqueANDConversationpublic(user.getNOM(),user.getPRENOM(),user.getETAT(),user.getPHOTO());
                        switchforanonymousmode.setChecked(true);
                        pDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),res.getString(R.string.desactivation_echouer),Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                        switchforanonymousmode.setChecked(false);
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

    //La requête HTTP pour désactiver le mode Anonymous
    public void VolleyDisabledAnonymousMode() {
        String url_disabledAnonymousMode = Const.dns+"/WazzabyApi/public/api/AnonymousModeActivation?user_id=" + String.valueOf(user.getID()) + "&etat=0";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_disabledAnonymousMode,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject reponse = new JSONObject(response);
                            Toast.makeText(getActivity(),res.getString(R.string.activation_mode_anonyme),Toast.LENGTH_LONG).show();

                            database.UpdateAnonymousMode(user.getID(),"Anonyme","Utilisateur","","0");
                            imageView.setImageResource(R.drawable.ic_profile_anonymous);
                            user.setPRENOM(reponse.getString("prenom"));
                            user.setNOM(reponse.getString("nom"));
                            user.setPHOTO(reponse.getString("photo"));
                            user.setETAT("0");
                            nom.setText(user.getPRENOM()+" "+user.getNOM());
                            //setUserObjectInHomeNotificationProblematiqueANDConversationpublic(user.getNOM(),user.getPRENOM(),user.getETAT(),user.getPHOTO());
                            pDialog.dismiss();

                            //On actualise la photo de l'utilisateur
                            if(!user.getPHOTO().equals("null") && !user.getPHOTO().isEmpty()) {
                                if(user.getPHOTO().equals("yo")) {
                                    imageView.setImageResource(R.drawable.ic_profile);
                                }else {
                                    Uri uri = Uri.parse(Const.dns+"/uploads/photo_de_profil/" + user.getPHOTO());
                                    imageView.setImageURI(uri);
                                }
                            }else
                            {
                                imageView.setImageResource(R.drawable.ic_profile);
                            }

                            //on met le switch à jour suivant l'état du modeanonymous
                            switchforanonymousmode.setChecked(false);
                            //Home.MiseajourduSwitchSuivantLeModeAnonymous(user);

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),res.getString(R.string.desactivation_echouer),Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                        switchforanonymousmode.setChecked(true);
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
