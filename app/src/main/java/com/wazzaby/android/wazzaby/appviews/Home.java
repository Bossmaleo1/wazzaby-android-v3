package com.wazzaby.android.wazzaby.appviews;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import com.wazzaby.android.wazzaby.broadcast.MyReceiver;
import com.wazzaby.android.wazzaby.connInscript.MainActivity;
import com.wazzaby.android.wazzaby.fragments.Accueil;
import com.wazzaby.android.wazzaby.fragments.FragmentDrawer;
import com.wazzaby.android.wazzaby.fragments.Problematique;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.Profil;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener{

    private LayoutInflater inflater;
    private SessionManager session;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private FragmentDrawer drawerFragment;
    private Toolbar toolbar;
    private Resources res;
    private DatabaseHandler database;
    private Profil user;
    private static final String TAG = Home.class.getSimpleName();
    private String Keypush = null;
    public static Context context;
    private Intent intent;
    public static CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        database = new DatabaseHandler(this);
        session = new SessionManager(this);
        res = getResources();
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerFragment = (FragmentDrawer)getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout),toolbar);
        drawerFragment.setDrawerListener(this);
        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
        getSupportActionBar().setTitle(user.getLibelle_prob());
        context = getApplicationContext();
        // display the first navigation drawer view on app launch
        displayView(0);
        intent = getIntent();

        //Toast.makeText(getApplicationContext(),intent.getStringExtra("test"),Toast.LENGTH_LONG).show();

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

        /*mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               String message = intent.getStringExtra("message");
            }
        };*/

        IntentFilter filter = new IntentFilter("com.wazzaby.android.wazzaby.broadcast");

        MyReceiver receiver = new MyReceiver();
        registerReceiver(receiver, filter);

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

}
