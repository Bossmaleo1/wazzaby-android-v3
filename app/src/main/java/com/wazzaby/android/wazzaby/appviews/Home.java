package com.wazzaby.android.wazzaby.appviews;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.broadcast.receiverMessage.MyReceiverMessagerie;
import com.wazzaby.android.wazzaby.broadcast.receiverNotification.MyReceiver;
import com.wazzaby.android.wazzaby.connInscript.MainActivity;
import com.wazzaby.android.wazzaby.fragments.Accueil;
import com.wazzaby.android.wazzaby.fragments.FragmentDrawer;
import com.wazzaby.android.wazzaby.fragments.Problematique;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.Profil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.wazzaby.android.wazzaby.appviews.MessageConstitution.allUsersAdapter;
import static com.wazzaby.android.wazzaby.fragments.FragmentDrawer.imageView;

public class Home extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener{

    private LayoutInflater inflater;
    private SessionManager session;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private FragmentDrawer drawerFragment;
    private Toolbar toolbar;
    private Resources res;
    private DatabaseHandler database;
    public static Profil user;
    private static final String TAG = Home.class.getSimpleName();
    private String Keypush = null;
    public static Context context;
    private Intent intent;
    public static CoordinatorLayout coordinatorLayout;
    public static  androidx.appcompat.app.ActionBar titlehome;

    public static boolean stabilasationanymousmode = false;
    private String dark_mode_item = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        res = getResources();
        intent = getIntent();
        database = new DatabaseHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
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
        setContentView(R.layout.home);

        database = new DatabaseHandler(this);
        session = new SessionManager(this);
        res = getResources();
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        titlehome = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerFragment = (FragmentDrawer)getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout),toolbar);
        drawerFragment.setDrawerListener(this);
        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
        titlehome.setTitle(user.getLibelle_prob());
        context = getApplicationContext();
        // display the first navigation drawer view on app launch
        displayView(0);
        intent = getIntent();
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
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

        //Ici on appel la reception de la pushnotification du chat
        IntentFilter filter = new IntentFilter("com.wazzaby.android.wazzaby.broadcast.receiverMessage");
        IntentFilter filter2 = new IntentFilter("com.wazzaby.android.wazzaby.broadcast.receiverNotification");

        MyReceiverMessagerie receiver = new MyReceiverMessagerie();
        registerReceiver(receiver, filter);

        MyReceiver receiver2 = new MyReceiver();
        registerReceiver(receiver2, filter2);

        ConnexionSynchronizationModeAnonymous();


        //on lance ce service pour assurer la synchronisation de changement de problematique
        //this.ConnexionSynchronizationProblematique();

        //on met le switch à jour suivant l'état du modeanonymous
        MiseajourduSwitchSuivantLeModeAnonymous(user);

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {

            case 0:
                fragment = new Accueil();
                title = user.getLibelle_prob();
                break;

            case 1:
                fragment = new Problematique();
                title = res.getString(R.string.title_prob);
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.deco:
                session.logoutUser();
                Intent i1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i1);
                return true;
            case R.id.profil:
                Intent i2 = new Intent(getApplicationContext(), ProfilUser.class);
                startActivity(i2);
                return true;
            case R.id.history:
                Intent i3 = new Intent(getApplicationContext(),Historique.class);
                startActivity(i3);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //Mise en place de la synchronisation du mode anonymous
    public void ConnexionSynchronizationModeAnonymous() {

        String url_synchronisation_anonymous_mode = Const.dns.concat("/WazzabyApi/public/api/DisplayUserAnonymousState?user_id=").concat(String.valueOf(user.getID()));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_synchronisation_anonymous_mode,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject reponse = new JSONObject(response);
                            stabilasationanymousmode = true;
                            if (reponse.getInt("user_etat") == 1) {

                                database.UpdateAnonymousMode(user.getID(),"Anonyme","Utilisateur","","1");
                                imageView.setImageResource(R.drawable.ic_profile_anonymous);
                                user.setPRENOM("Utilisateur");
                                user.setNOM("Anonyme");
                                user.setPHOTO("");
                                FragmentDrawer.user.setPRENOM("Utilisateur");
                                FragmentDrawer.user.setNOM("Anonyme");
                                FragmentDrawer.user.setPHOTO("");
                                FragmentDrawer.nom.setText(user.getPRENOM()+" "+user.getNOM());

                                //on met le switch à jour suivant l'état du modeanonymous
                                MiseajourduSwitchSuivantLeModeAnonymous(user);

                            } else if (reponse.getInt("user_etat") == 0) {

                                database.UpdateAnonymousMode(user.getID(),reponse.getString("nom"),reponse.getString("prenom"),reponse.getString("photo"),"0");
                                //imageView.setImageResource(R.drawable.ic_profile_anonymous);
                                user.setPRENOM(reponse.getString("prenom"));
                                user.setNOM(reponse.getString("nom"));
                                user.setPHOTO(reponse.getString("photo"));
                                FragmentDrawer.user.setPRENOM(reponse.getString("prenom"));
                                FragmentDrawer.user.setNOM(reponse.getString("nom"));
                                FragmentDrawer.user.setPHOTO(reponse.getString("photo"));
                                FragmentDrawer.nom.setText(user.getPRENOM()+" "+user.getNOM());

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
                                MiseajourduSwitchSuivantLeModeAnonymous(user);

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //on met le switch à jour suivant l'état du modeanonymous
                        MiseajourduSwitchSuivantLeModeAnonymous(user);
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


    //Cette fonction détecter l'état du mode anonymous
    public static void MiseajourduSwitchSuivantLeModeAnonymous(Profil user) {
        if (user.getETAT().equals("1")) {
            FragmentDrawer.switchforanonymousmode.setChecked(true);
        } else {
            FragmentDrawer.switchforanonymousmode.setChecked(false);
        }
    }

}
