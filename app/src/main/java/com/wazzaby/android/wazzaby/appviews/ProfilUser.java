package com.wazzaby.android.wazzaby.appviews;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.Profil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.wazzaby.android.wazzaby.appviews.Home.titlehome;

public class ProfilUser extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CoordinatorLayout coordinatorLayout;
    private Resources res;
    private Intent intent;
    private ImageView pictureuser;
    private DatabaseHandler database;
    private SessionManager session;
    private Profil user;
    private SimpleDraweeView draweeView;
    private TextView edit_name;
    private TextView edit_problematique;
    private RelativeLayout problematique_block;
    private RelativeLayout langue_block;
    private TextView langue;
    private SwitchMaterial edit_modenuit;
    private String dark_mode_item = null;
    private String dark_mode_item_temp = null;
    private RelativeLayout global_block;
    private RelativeLayout name_block;
    private RelativeLayout modenuit_block;
    private Menu menu_main;
    private MenuItem edit_profil_menu;
    private Drawable edit_profil_icon;
    private MenuInflater main_inflater;




    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        res = getResources();
        intent = getIntent();
        database = new DatabaseHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        dark_mode_item = database.getDARKMODE();
        dark_mode_item_temp = database.getDARKMODE();
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
        setContentView(R.layout.profil);
        toolbar = findViewById(R.id.toolbar);
        edit_name = findViewById(R.id.edit_name);
        edit_problematique = findViewById(R.id.edit_problematique);
        edit_modenuit = findViewById(R.id.edit_modenuit);
        name_block = findViewById(R.id.name_block);
        global_block = findViewById(R.id.global_block);
        modenuit_block = findViewById(R.id.modenuit_block);
        langue = findViewById(R.id.langue_text);
        langue.setText(Locale.getDefault().getDisplayLanguage());
        coordinatorLayout = findViewById(R.id.coordinatorLayout);



        /*if(String.valueOf(session.getUserDetail().get(SessionManager.Key_dark_mode)).equals("1")) {
            edit_modenuit.setChecked(true);
        } else if(String.valueOf(session.getUserDetail().get(SessionManager.Key_dark_mode)).equals("0")) {
            edit_modenuit.setChecked(false);
        }*/

        //Toast.makeText(getApplicationContext(), " Dark Mode Item : "+dark_mode_item , Toast.LENGTH_LONG).show();

        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        draweeView = findViewById(R.id.my_image_view);
        edit_name.setText(user.getPRENOM()+" "+user.getNOM());
        edit_problematique.setText(user.getLibelle_prob());
        collapsingToolbarLayout.setTitle(" ");
        collapsingToolbarLayout.setContentScrimColor(res.getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        final Drawable maleoIcon = res.getDrawable(R.drawable.ic_arrow_back_black_24dp);
        maleoIcon.mutate().setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.SRC_IN);
        this.getSupportActionBar().setHomeAsUpIndicator(maleoIcon);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(!user.getPHOTO().equals("null")) {
            Uri uri = Uri.parse(Const.dns+"/uploads/photo_de_profil/" + user.getPHOTO());
            draweeView.setImageURI(uri);
        }else
        {
            pictureuser.setImageResource(R.drawable.ic_profile_colorier);
        }

        //shall we get the problematique block
        problematique_block = findViewById(R.id.problematique_block);
        //shall we get the langue_block
        langue_block = findViewById(R.id.langue_block);

        dark_mode_item = database.getDARKMODE();
        //si le dark mode est activé
        if (dark_mode_item.equals("1"))
        {
            //setTheme(R.style.AppDarkTheme);
            edit_modenuit.setChecked(true);
            DarkMode(maleoIcon);
        } else if (dark_mode_item.equals("0")) {
            edit_modenuit.setChecked(false);
            LightMode(maleoIcon,savedInstanceState);
        }
        //shall we get the click event
        problematique_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Problematique.class);
                startActivity(intent);

            }
        });
        //shall we get the click event
        //shall we get the click event
        langue_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LanguageList.class);
                startActivity(intent);

            }
        });

        edit_modenuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dark_mode_item = database.getDARKMODE();
                if (((SwitchMaterial) view).isChecked()) {
                    if(dark_mode_item.isEmpty())
                    {
                        //Toast.makeText(getApplicationContext(), " C'est vide !!! " , Toast.LENGTH_LONG).show();
                        database.addDARKMODE("1");
                    } else if (dark_mode_item.equals("0")) {
                        database.updateDARKMODE("1");
                    }
                    DarkMode(maleoIcon);
                } else {

                    database.updateDARKMODE("0");
                    LightMode(maleoIcon,savedInstanceState);
                }
            }
        });

        this.ConnexionSynchronizationProblematique();

    }

    @Override
    public void onBackPressed() {
        dark_mode_item = database.getDARKMODE();
        if(dark_mode_item.equals(dark_mode_item_temp)) {
            Intent i = new Intent();
            setResult(RESULT_OK, i);
            finish();
        } else if (!dark_mode_item.equals(dark_mode_item_temp)) {
            Intent intent = new Intent(getApplicationContext(),Home.class);
            startActivity(intent);
        }
        /*if(dark_mode_item.equals("1")) {

        } else if (dark_mode_item.equals("0")) {

        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        main_inflater = getMenuInflater();
        main_inflater.inflate(R.menu.menu_profil, menu);
        menu_main = menu;
        edit_profil_menu = menu.findItem(R.id.profil_user);
        edit_profil_icon = edit_profil_menu.getIcon();
        edit_profil_icon.mutate().setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.SRC_IN);
        edit_profil_menu.setIcon(edit_profil_icon);
        if(dark_mode_item.equals("1")) {
            edit_profil_icon.mutate().setColorFilter(getResources().getColor(R.color.darkprimary), PorterDuff.Mode.SRC_IN);
            edit_profil_menu.setIcon(edit_profil_icon);
        } else if (dark_mode_item.equals("0")){
            edit_profil_icon.mutate().setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.SRC_IN);
            edit_profil_menu.setIcon(edit_profil_icon);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // User chose the "Settings" item, show the app settings UI...
                /*Intent i = new Intent();
                setResult(RESULT_OK, i);*/
                dark_mode_item = database.getDARKMODE();
                if(dark_mode_item.equals(dark_mode_item_temp)) {
                    Intent i = new Intent();
                    setResult(RESULT_OK, i);
                    finish();
                } else if (!dark_mode_item.equals(dark_mode_item_temp)) {
                    Intent intent = new Intent(getApplicationContext(),Home.class);
                    startActivity(intent);
                }
                finish();
                return true;

            case R.id.profil_user:
                Intent intent = new Intent(getApplicationContext(),PictureUpdate.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

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

    public void DarkMode(Drawable maleoIcon) {
        coordinatorLayout.setBackgroundColor(getResources().getColor(R.color.darkprimary));
        name_block.setBackground(getResources().getDrawable(R.drawable.background_menu_message_public_mode_dark));
        problematique_block.setBackground(getResources().getDrawable(R.drawable.background_menu_message_public_mode_dark));
        langue_block.setBackground(getResources().getDrawable(R.drawable.background_menu_message_public_mode_dark));
        modenuit_block.setBackground(getResources().getDrawable(R.drawable.background_menu_message_public_mode_dark));
        //global_block.setBackground(R.color.darkprimarydark);
        global_block.setBackgroundColor(getResources().getColor(R.color.darkprimary));

        //on change la couleur de l'icone du back
        maleoIcon.mutate().setColorFilter(getResources().getColor(R.color.darkprimary), PorterDuff.Mode.SRC_IN);
        this.getSupportActionBar().setHomeAsUpIndicator(maleoIcon);

        setTheme(R.style.AppDarkTheme);

        /*MenuItem testmenuitem = menu_main.findItem(R.id.profil_user);
        main_inflater*/

       /* edit_profil_icon.mutate().setColorFilter(getResources().getColor(R.color.darkprimary), PorterDuff.Mode.SRC_IN);
        edit_profil_menu.setIcon(edit_profil_icon);*/

        /*Drawable edit_profil_icon = res.getDrawable(R.drawable.baseline_camera_alt_black_24);
        maleoIcon.mutate().setColorFilter(getResources().getColor(R.color.darkprimary), PorterDuff.Mode.SRC_IN);

        edit_profil_icon = edit_profil_menu.getIcon();
        edit_profil_icon.mutate().setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.SRC_IN);
        edit_profil_menu.setIcon(edit_profil_icon);*/
    }

    public void LightMode(Drawable maleoIcon,Bundle savedInstanceState) {
        coordinatorLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        name_block.setBackground(getResources().getDrawable(R.drawable.background_menu_message_public));
        problematique_block.setBackground(getResources().getDrawable(R.drawable.background_menu_message_public));
        langue_block.setBackground(getResources().getDrawable(R.drawable.background_menu_message_public));
        modenuit_block.setBackground(getResources().getDrawable(R.drawable.background_menu_message_public));
        //global_block.setBackground(R.color.darkprimarydark);
        global_block.setBackgroundColor(getResources().getColor(R.color.graycolor));
        //on change la couleur de l'icone du back
        maleoIcon.mutate().setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.SRC_IN);
        this.getSupportActionBar().setHomeAsUpIndicator(maleoIcon);

        setTheme(R.style.AppTheme);

        //MenuItem testmenuitem = menu_main.findItem(R.id.profil_user);

        /*edit_profil_icon.mutate().setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.SRC_IN);
        edit_profil_menu.setIcon(edit_profil_icon);*/
    }

}
