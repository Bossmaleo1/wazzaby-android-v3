package com.wazzaby.android.wazzaby.connInscript;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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
import com.google.android.material.textfield.TextInputLayout;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.model.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FormInscriptStep1 extends AppCompatActivity {

    private Resources res;
    private Toolbar toolbar;
    private com.google.android.material.button.MaterialButton send;
    private com.google.android.material.textfield.TextInputEditText email;
    private String code_de_verfication;
    private JSONObject reponse;
    private ProgressDialog pDialog;
    private com.google.android.material.textfield.TextInputEditText code_verification_edittext;
    private LinearLayout code_block;
    private LinearLayout email_block;
    private TextView message;
    private String getEmail;
    private String getCode_de_verfication;
    private CoordinatorLayout coordinatorLayout;
    private TextInputLayout email_error;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formincript3);
        res = getResources();
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(res.getString(R.string.inscript1_1));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        email = findViewById(R.id.email);
        send = findViewById(R.id.send);
        email_error = findViewById(R.id.text_input_layout_email);
        code_verification_edittext = findViewById(R.id.code_editext);
        message =  findViewById(R.id.message);
        email_block =  findViewById(R.id.email_block);
        code_block =  findViewById(R.id.code_block);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()==true) {
                    if (code_verification_edittext.getText().length()==0) {

                        pDialog = new ProgressDialog(FormInscriptStep1.this);
                        pDialog.setMessage("Connexion en cours...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(false);
                        pDialog.show();
                        getCode_de_verfication = code_de_verfication;
                        volley_de_verification_existence_adresse_email();

                        //volley_de_verification_de_email();
                    }else if(code_verification_edittext.getText().length()!=0)
                    {

                        if (String.valueOf(code_de_verfication).trim().equals(String.valueOf(code_verification_edittext.getText()).trim())) {
                            Intent intent = new Intent(getApplicationContext(), FormInscriptStep2.class);
                            intent.putExtra("email",getEmail);
                            intent.putExtra("code",String.valueOf(code_verification_edittext.getText()).trim());
                            startActivity(intent);
                        } else {
                            Toast.makeText(FormInscriptStep1.this,"Vous avez introduit le mauvais code de verification !!! ",Toast.LENGTH_LONG).show();
                        }
                    }
                }


            }
        });


    }

    public boolean validate() {
        boolean valid = true;

        String _email = email.getText().toString();

        if (_email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(_email).matches()) {
            email_error.setError(res.getString(R.string.email_error));
            valid = false;
        } else {
            email_error.setError(null);
        }

        return valid;
    }

    private void volley_de_verification_existence_adresse_email() {
        String url_verification_email = Const.dns+"/WazzabyApi/public/api/VerificationEmail?email="+String.valueOf(email.getText());
        this.getEmail = String.valueOf(email.getText());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_verification_email,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //pDialog.dismiss();
                        try {
                            reponse = new JSONObject(response);
                            int email_code_error = reponse.getInt("email");
                            if(email_code_error == 1) {
                                Toast.makeText(getApplicationContext(),"Cette adresse email est déjà occupée !!",Toast.LENGTH_LONG).show();
                                pDialog.dismiss();
                            }else if (email_code_error == 0) {
                                volley_de_verification_de_email();
                               // Toast.makeText(getApplicationContext(),"Cette adresse email est disponible !!",Toast.LENGTH_LONG).show();
                            }
                            /*code_de_verfication = reponse.getString("succes");
                            code_block.setVisibility(View.VISIBLE);
                            message.setVisibility(View.VISIBLE);
                            email_block.setVisibility(View.GONE);*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();

                        if(error instanceof ServerError)
                        {
                            Toast.makeText(FormInscriptStep1.this,"Une erreur au niveau du serveur viens de survenir ",Toast.LENGTH_LONG).show();
                        }else if(error instanceof NetworkError)
                        {
                            Toast.makeText(FormInscriptStep1.this,"Une erreur  du réseau viens de survenir ",Toast.LENGTH_LONG).show();
                        }else if(error instanceof AuthFailureError)
                        {
                            Toast.makeText(FormInscriptStep1.this,"Une erreur d'authentification réseau viens de survenir ",Toast.LENGTH_LONG).show();
                        }else if(error instanceof ParseError)
                        {
                            Toast.makeText(FormInscriptStep1.this,"Une erreur  du réseau viens de survenir ",Toast.LENGTH_LONG).show();
                        }else if(error instanceof NoConnectionError)
                        {
                            Toast.makeText(FormInscriptStep1.this,"Une erreur  du réseau viens de survenir, veuillez revoir votre connexion internet ",Toast.LENGTH_LONG).show();

                        }else if(error instanceof TimeoutError)
                        {
                            Toast.makeText(FormInscriptStep1.this,"Le delai d'attente viens d'expirer,veuillez revoir votre connexion internet ! ",Toast.LENGTH_LONG).show();
                        }else
                        {

                            Toast.makeText(FormInscriptStep1.this,"Une erreur  du réseau viens de survenir ",Toast.LENGTH_LONG).show();

                        }
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                /*params.put("email",email.getText().toString());
                params.put("password",password.getText().toString());*/
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void volley_de_verification_de_email()
    {
        String url_connexion = Const.dns+"/WazzabyApiOthers/send_mail.php?email="+String.valueOf(email.getText()) ;
        this.getEmail = String.valueOf(email.getText());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_connexion,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        try {
                            reponse = new JSONObject(response);
                            code_de_verfication = reponse.getString("succes");
                            code_block.setVisibility(View.VISIBLE);
                            message.setVisibility(View.VISIBLE);
                            email_block.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();

                        if(error instanceof ServerError)
                        {
                            Toast.makeText(FormInscriptStep1.this,"Une erreur au niveau du serveur viens de survenir ",Toast.LENGTH_LONG).show();
                            email.setText("");
                            //password.setText("");
                        }else if(error instanceof NetworkError)
                        {
                            Toast.makeText(FormInscriptStep1.this,"Une erreur  du réseau viens de survenir ",Toast.LENGTH_LONG).show();
                            email.setText("");
                            //password.setText("");
                        }else if(error instanceof AuthFailureError)
                        {
                            Toast.makeText(FormInscriptStep1.this,"Une erreur d'authentification réseau viens de survenir ",Toast.LENGTH_LONG).show();
                            //email.setText("");
                            //password.setText("");
                        }else if(error instanceof ParseError)
                        {
                            Toast.makeText(FormInscriptStep1.this,"Une erreur  du réseau viens de survenir ",Toast.LENGTH_LONG).show();
                            //email.setText("");
                            //password.setText("");
                        }else if(error instanceof NoConnectionError)
                        {
                            Toast.makeText(FormInscriptStep1.this,"Une erreur  du réseau viens de survenir, veuillez revoir votre connexion internet ",Toast.LENGTH_LONG).show();
                            //email.setText("");
                            //password.setText("");
                        }else if(error instanceof TimeoutError)
                        {
                            Toast.makeText(FormInscriptStep1.this,"Le delai d'attente viens d'expirer,veuillez revoir votre connexion internet ! ",Toast.LENGTH_LONG).show();
                            //email.setText("");
                            //password.setText("");
                        }else
                        {

                            Toast.makeText(FormInscriptStep1.this,"Une erreur  du réseau viens de survenir ",Toast.LENGTH_LONG).show();
                            //email.setText("");
                            //password.setText("");
                        }
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                /*params.put("email",email.getText().toString());
                params.put("password",password.getText().toString());*/
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // User chose the "Settings" item, show the app settings UI...
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

    @Override
    public void onBackPressed() {
        /*Intent _result = new Intent(getApplicationContext(),forminscript3.class);
        startActivity(_result);*/
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }




}
