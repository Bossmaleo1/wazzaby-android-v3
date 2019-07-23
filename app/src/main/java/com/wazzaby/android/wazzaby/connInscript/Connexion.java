package com.wazzaby.android.wazzaby.connInscript;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.appviews.Home;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.Profil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Connexion extends AppCompatActivity {

    private TextView about;
    private Resources res;
    private LinearLayout aboutblock;
    private Toolbar toolbar;
    private TextView passwordforget;
    private MaterialButton Connexion;
    private TextInputEditText email;
    private TextInputEditText password;
    private final int REQUEST_LOCATION = 200;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private ProgressDialog pDialog;
    private JSONObject reponse;
    private JSONObject data;
    private int succes;
    private SessionManager session;
    private DatabaseHandler database;
    private TextInputLayout email_error;
    private TextInputLayout password_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        res = getResources();
        about = findViewById(R.id.about);
        aboutblock = findViewById(R.id.aboutblock);
        passwordforget = findViewById(R.id.passforget);
        Connexion = findViewById(R.id.connexion);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        email_error = findViewById(R.id.text_input_layout_email);
        password_error = findViewById(R.id.text_input_layout_password);
        SpannableString content = new SpannableString(res.getString(R.string.about));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        about.setText(content);
        SpannableString content1 = new SpannableString(res.getString(R.string.passforget));
        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
        passwordforget.setText(content1);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(res.getString(R.string.app_name));
        session = new SessionManager(this);

        database = new DatabaseHandler(this);

        aboutblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),About.class);
                startActivity(intent);
            }
        });

        passwordforget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Passforget.class);
                startActivity(intent);
            }
        });

        Connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validate()==true){
                    pDialog = new ProgressDialog(Connexion.this);
                    pDialog.setMessage("Connexion en cours...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();
                    Connexion();
                }
            }
        });
    }


    public boolean validate() {
        boolean valid = true;

        String _email = email.getText().toString();
        String _password = password.getText().toString();

        if (_email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(_email).matches()) {
            email_error.setError("Format email incorrect");
            valid = false;
        } else {
            email_error.setError(null);
        }

        if (_password.isEmpty()) {
            password_error.setError("Renseigner votre Mot de passe vide");
            valid = false;
        } else {
            password_error.setError(null);
        }

        return valid;
    }


    private void Connexion()
    {
        String url_connexion = Const.dns+"/WazzabyApi/public/api/connexion?email=" + String.valueOf(email.getText().toString())+"&password="+ String.valueOf(password.getText().toString());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_connexion,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        showJSON(response);
                        if(succes!=1) {
                            email.setText("");
                            password.setText("");
                            Toast.makeText(Connexion.this, "votre mot de passe ou votre adresse e-mail est incorrecte" , Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();

                        if(error instanceof ServerError)
                        {
                            Toast.makeText(Connexion.this,"Une erreur au niveau du serveur viens de survenir ",Toast.LENGTH_LONG).show();
                            email.setText("");
                            password.setText("");
                        }else if(error instanceof NetworkError)
                        {
                            Toast.makeText(Connexion.this,"Une erreur  du réseau viens de survenir ",Toast.LENGTH_LONG).show();
                            email.setText("");
                            password.setText("");
                        }else if(error instanceof AuthFailureError)
                        {
                            Toast.makeText(Connexion.this,"Une erreur d'authentification réseau viens de survenir ",Toast.LENGTH_LONG).show();
                            email.setText("");
                            password.setText("");
                        }else if(error instanceof ParseError)
                        {
                            Toast.makeText(Connexion.this,"Une erreur  du réseau viens de survenir ",Toast.LENGTH_LONG).show();
                            email.setText("");
                            password.setText("");
                        }else if(error instanceof NoConnectionError)
                        {
                            Toast.makeText(Connexion.this,"Une erreur  du réseau viens de survenir, veuillez revoir votre connexion internet ",Toast.LENGTH_LONG).show();
                            email.setText("");
                            password.setText("");
                        }else if(error instanceof TimeoutError)
                        {
                            Toast.makeText(Connexion.this,"Le delai d'attente viens d'expirer,veuillez revoir votre connexion internet ! ",Toast.LENGTH_LONG).show();
                            email.setText("");
                            password.setText("");
                        }else
                        {

                            Toast.makeText(Connexion.this,"Une erreur  du réseau viens de survenir ", Toast.LENGTH_LONG).show();
                            email.setText("");
                            password.setText("");
                        }
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",email.getText().toString());
                params.put("password",password.getText().toString());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void showJSON(String response) {
        try {
            reponse = new JSONObject(response);
            succes = reponse.getInt("succes");
            if (succes == 1) {
                session.createLoginSession(reponse.getInt("id"));
                Profil profil = new Profil(reponse.getInt("id"), reponse.getString("nom")
                        , reponse.getString("prenom"), reponse.getString("datenaissance")
                        , reponse.getString("sexe"), reponse.getString("email"),
                        reponse.getString("photo"), reponse.getString("keypush")//ici c'est le keypush, il va falloir l'ajouter sur le script php et ajouter libelle prob
                        , reponse.getString("langue"), reponse.getString("etat")
                        , reponse.getString("pays"), reponse.getString("ville")
                        , reponse.getString("id_prob")
                        , reponse.getString("libelle_prob"));
                database.addUSER(profil);
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //gère le click sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.inscript:
                //ControleGPS();
                /*Intent intent = new Intent(getApplicationContext(),forminscript3.class);
                startActivity(intent);*/
                /*Intent intent = new Intent(getApplicationContext(),UploadImage.class);
                startActivity(intent);*/
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    }
