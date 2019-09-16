package com.wazzaby.android.wazzaby.appviews;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.adapter.displaycommentaryadapter;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.NotificationItem;
import com.wazzaby.android.wazzaby.model.data.Profil;
import com.wazzaby.android.wazzaby.model.data.displaycommentary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsDetails extends AppCompatActivity {

    private DatabaseHandler database;
    private SessionManager session;
    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private Resources res;
    private NotificationItem notificationItem;
    private Intent intent;
    private SimpleDraweeView icon;
    private TextView title;
    private TextView title1;
    private SimpleDraweeView photo_du_poste_background;
    private TextView contenu;
    private TextView contenucomment;
    private TextView nombre_de_jaime;
    private TextView nombre_de_jaimepas;
    private boolean booljaime;
    private boolean booljaimepas;
    private ImageView icon_jaime;
    private ImageView icon_jaimepas;
    private ImageView menuderoulant;
    private RelativeLayout photo_du_poste;
    private TextView commentnumber;
    private Drawable iconjaime;
    private Drawable iconjaimepas;
    private RecyclerView recyclerView;
    private EditText editcomment;
    private displaycommentaryadapter allUsersAdapter;
    private Profil user;
    private List<displaycommentary> data = new ArrayList<>();
    private String Libelle = null;
    private ImageView submitcomment;
    private Context context;
    private JSONObject object;
    private ProgressBar progressbar;
    private LinearLayout block_affichage_error;
    private TextView Error_text_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationsdetails);
        res = getResources();
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Message Public");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionManager(this);
        database = new DatabaseHandler(this);
        intent = getIntent();
        context = this;
        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
        notificationItem = intent.getParcelableExtra("notificationitem");
        icon = findViewById(R.id.icon);
        title = findViewById(R.id.title);
        title1 = findViewById(R.id.title1);
        photo_du_poste_background = findViewById(R.id.photo_du_poste_background);
        contenu = findViewById(R.id.contenu);
        contenucomment = findViewById(R.id.contenucomment);
        nombre_de_jaime = findViewById(R.id.nombre_de_jaime);
        nombre_de_jaimepas = findViewById(R.id.nombre_de_jaimepas);
        icon_jaime = findViewById(R.id.icon_jaime);
        icon_jaimepas = findViewById(R.id.icon_jaimepas);
        photo_du_poste =  findViewById(R.id.photo_du_poste);
        progressbar = findViewById(R.id.progressbar);
        toolbar =  findViewById(R.id.toolbar);
        submitcomment = findViewById(R.id.submitcomment);
        editcomment = findViewById(R.id.editcomment);
        block_affichage_error = findViewById(R.id.materialcardview);
        Error_text_message = findViewById(R.id.text_error_message);
        iconjaime = res.getDrawable(R.drawable.baseline_thumb_up_alt_black_24);
        iconjaimepas = res.getDrawable(R.drawable.baseline_thumb_down_alt_black_24);
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allUsersAdapter = new displaycommentaryadapter(this,data);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(allUsersAdapter);
        this.Connexion_DisplayComment();
        block_affichage_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                block_affichage_error.setVisibility(View.GONE);
                progressbar.setVisibility(View.VISIBLE);
                Connexion_DisplayComment();
            }
        });

        //Gestion de la coloration
        if (notificationItem.getCheckmention() == 1){
            this.booljaime = true;
            iconjaime.mutate().setColorFilter(Color.parseColor("#188dc8"), PorterDuff.Mode.SRC_IN);
            icon_jaime.setImageDrawable(iconjaime);
            icon_jaimepas.setImageDrawable(iconjaimepas);
            nombre_de_jaime.setText(String.valueOf(notificationItem.getCountjaime()));
            nombre_de_jaimepas.setText(String.valueOf(notificationItem.getCountjaimepas()));
            nombre_de_jaime.setTextColor(Color.parseColor("#188dc8"));
            nombre_de_jaimepas.setTextColor(Color.parseColor("#E0E0E0"));
        } else if (notificationItem.getCheckmention() == 2){
            this.booljaimepas = true;
            iconjaimepas.mutate().setColorFilter(Color.parseColor("#188dc8"), PorterDuff.Mode.SRC_IN);
            iconjaimepas.mutate().setColorFilter(Color.parseColor("#188dc8"), PorterDuff.Mode.SRC_IN);
            icon_jaime.setImageDrawable(iconjaime);
            icon_jaimepas.setImageDrawable(iconjaimepas);
            nombre_de_jaime.setText(String.valueOf(notificationItem.getCountjaime()));
            nombre_de_jaimepas.setText(String.valueOf(notificationItem.getCountjaimepas()));
            nombre_de_jaimepas.setTextColor(Color.parseColor("#188dc8"));
            nombre_de_jaime.setTextColor(Color.parseColor("#E0E0E0"));
        } else if (notificationItem.getCheckmention() == 0){
            this.booljaime = false;
            this.booljaimepas = false;
            iconjaime.mutate().setColorFilter(Color.parseColor("#9E9E9E"), PorterDuff.Mode.SRC_IN);
            icon_jaime.setImageDrawable(iconjaime);
            icon_jaimepas.setImageDrawable(iconjaimepas);
            //holder.nombre_de_jaime.setTextColor(Color.parseColor("#188dc8"));
            nombre_de_jaime.setText(String.valueOf(notificationItem.getCountjaime()));
            nombre_de_jaimepas.setText(String.valueOf(notificationItem.getCountjaimepas()));
            nombre_de_jaimepas.setTextColor(Color.parseColor("#E0E0E0"));
            nombre_de_jaime.setTextColor(Color.parseColor("#E0E0E0"));
        }

        title.setText(notificationItem.getName_messagepublic());
        title1.setText(notificationItem.getUpdated());
        contenu.setText(notificationItem.getStatus_text_content_messagepublic());
        nombre_de_jaime.setText(String.valueOf(notificationItem.getCountjaime()));
        nombre_de_jaimepas.setText(String.valueOf(notificationItem.getCountjaimepas()));
        //commentnumber.setText();
        if(!notificationItem.getUser_photo_messagepublic().equals("null")) {
            Uri uri = Uri.parse(Const.dns+"/uploads/photo_de_profil/" + notificationItem.getUser_photo_messagepublic());
            icon.setImageURI(uri);
        }else
        {
            icon.setImageResource(R.drawable.ic_profile_colorier);
        }

        //Toast.makeText(getApplicationContext(),"I am Glad Happy !!! "+String.valueOf(notificationItem.getName_messagepublic()),Toast.LENGTH_LONG).show();

        if (notificationItem.getEtat_photo_status_messagepublic().equals("block")) {
            photo_du_poste.setVisibility(View.VISIBLE);
            photo_du_poste.setMinimumWidth(1024);
            photo_du_poste.setMinimumHeight(768);
            photo_du_poste_background.setMinimumWidth(1024);
            photo_du_poste_background.setMinimumHeight(768);
            photo_du_poste_background.setImageResource(R.drawable.baseline_insert_photo_black_48);
            Uri uri = Uri.parse(notificationItem.getStatus_photo_messagepublic());
            photo_du_poste_background.setImageURI(uri);
        }else if(notificationItem.getEtat_photo_status_messagepublic().equals("none")) {
            photo_du_poste.setVisibility(View.GONE);
        }
        /*icon_jaime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current.getCheckmention() == 1) {

                }
            }
        });*/

        //La gestion du click je n'aime pas
        /*icon_jaimepas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });*/

        //On appelle le web service pour marquer comme lu
        Connexion_marquercommelu();
        submitcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editcomment.getText().toString().isEmpty()) {
                    displaycommentary commentary = new displaycommentary("http://wazzaby.com/uploads/photo_de_profil/" + user.getPHOTO(), context, R.drawable.ic_done_black_18dp
                            , "A l'instant", R.color.greencolor, 0, editcomment.getText().toString(),
                            user.getPRENOM() + " " + user.getNOM());

                    block_affichage_error.setVisibility(View.GONE);
                    /*Ligne de code permettant de
                     * faire une insertion du message en tete
                     * */
                    List<displaycommentary> datatemp = new ArrayList<>();
                    datatemp.add(commentary);
                    for(int i = data.size();i>0;i--) {
                        datatemp.add(data.get(i-1));
                    }
                    data.clear();
                    data.addAll(datatemp);
                    //fin de l'ajout multiple
                    allUsersAdapter.notifyDataSetChanged();
                    Libelle = editcomment.getText().toString();
                    SENDCommentary();
                    editcomment.setText("");
                } else {
                    Toast.makeText(getApplicationContext(),"Veuillez ecrire votre commentaire",Toast.LENGTH_LONG).show();
                }
            }
        });
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

    private void Connexion_marquercommelu()
    {
        String url_marquercommelu = Const.dns.concat("/WazzabyApi/public/api/MarquerNotificationCommeLu?id_notification=").concat(String.valueOf(notificationItem.getNotification_id()));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_marquercommelu,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //database
                       //database.UpdateKeyPush(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getID(),Keypush);
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

    private void Connexion_DisplayComment()
    {
        //progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Const.dns+"/WazzabyApi/public/api/displayComment?id_messagepublic="+String.valueOf(notificationItem.getId_messagepublic()),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray reponse = new JSONArray(response);
                            for(int i = 0;i<reponse.length();i++)
                            {
                                object = reponse.getJSONObject(i);
                                displaycommentary commentary = new displaycommentary(object.getString("user_photo"),context,R.drawable.ic_done_all_black_18dp
                                        ,object.getString("updated"),R.color.greencolor,2,object.getString("status_text_content"),
                                        object.getString("name"));
                                data.add(commentary);

                            }

                            if (reponse.length() == 0) {
                                Error_text_message.setText(" Aucun commentaire");
                                block_affichage_error.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressbar.setVisibility(View.GONE);
                        allUsersAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressbar.setVisibility(View.GONE);
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

                        Error_text_message.setText(" Erreur reseaux, veuillez reessayer svp !");
                        block_affichage_error.setVisibility(View.VISIBLE);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                //params.put("ID_MESSAGE",String.valueOf(intent.getIntExtra("nom",0)));
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void SENDCommentary()
    {
        String url = Const.dns+"/WazzabyApi/public/api/addComment?id_user="+String.valueOf(user.getID())+"&id_messagepublic="+String.valueOf(notificationItem.getId_messagepublic())
                +"&libelle_comment="+Libelle+"&anonymous=0";
        //progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(),url,Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        data.get((data.size()-1)).setIcononline(R.drawable.ic_done_all_black_18dp);
                        allUsersAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"Une erreure s'est produite votre commentaire n'a pas ete ajoute",Toast.LENGTH_LONG).show();
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

    /*public void Connexion_Insert_Notification() {
        String message;
        int etat;
        if(user.getETAT().isEmpty()) {
            etat = 0;
        } else {
            etat =Integer.valueOf(user.getETAT());
        }
        if (user.getETAT().equals('1')) {
            message = "Votre message public vient d'etre commenter par un Utilisateur Anonyme";
        } else {
            message = "Votre message public vient d'etre commenter par "
                    .concat(user.getPRENOM()).concat(" ")
                    .concat(user.getNOM());
        }
        String url_notification = Const.dns.concat("/WazzabyApi/public/api/InsertNotification?users_id=")
                .concat(String.valueOf(user.getID()))
                .concat("&libelle=").concat(message)
                .concat("&id_type=").concat(String.valueOf(intent.getIntExtra("nom",0))).concat("&etat=0")
                .concat("&id_recepteur=").concat(String.valueOf(notificationItem.getI))
                .concat("&anonymous=").concat(String.valueOf(etat));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_notification,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
    }*/



}
