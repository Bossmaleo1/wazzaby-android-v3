package com.wazzaby.android.wazzaby.appviews;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.net.Uri;

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
import com.wazzaby.android.wazzaby.connInscript.Connexion;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.adapter.ConversationspriveeAdapter;

import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.app.AppCompatActivity;
import com.wazzaby.android.wazzaby.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wazzaby.android.wazzaby.model.data.Conversationprivateitem;
import com.wazzaby.android.wazzaby.model.data.Profil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import developer.semojis.actions.EmojIconActions;


public class MessageConstitution extends AppCompatActivity {

    private Intent intent;
    private Resources res;
    private SimpleDraweeView profilimage;
    private SessionManager session;
    private DatabaseHandler database;
    private Toolbar toolbar;
    private TextView profiltextname;
    public static ConversationspriveeAdapter allUsersAdapter;
    private RecyclerView recyclerView;
    public static List<Conversationprivateitem> data_recyclerview = new ArrayList<>();

    private developer.semojis.Helper.EmojiconEditText editcomment;
    private EmojIconActions emojIcon;
    private View rootView;
    private ImageView add_emoji;
    private ImageView submitcomment;
    private TextView status_user;
    private ImageView back_button;
    private Profil user;
    public static Context context_messageconstitution;
    //cette variable nous permet de savoir si l'affichage doit être réactualiser ou pas
    public static int etat_du_boss = 0;
    public static int ID_USER_ONFOCUS = 0;
    public static  RecyclerView.SmoothScroller smoothScroller;
    public static LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messageconstitution);

        //on change la valeur de l'état
        this.etat_du_boss = 1;

        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        data_recyclerview.clear();


        session = new SessionManager(this);
        database = new DatabaseHandler(this);
        context_messageconstitution = this;
        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
        res = getResources();
        intent = getIntent();
        profiltextname = findViewById(R.id.toolbarText);
        profilimage = findViewById(R.id.imageToolBar);
        editcomment = findViewById(R.id.editcomment);
        status_user = findViewById(R.id.status_user);

        Uri uri = Uri.parse(Const.dns+"/uploads/photo_de_profil/" + intent.getStringExtra("imageview"));
        profilimage.setImageURI(uri);
        profiltextname.setText(intent.getStringExtra("name"));

        Toast.makeText(MessageConstitution.this,"Keypush : "+intent.getStringExtra("KeyPush"),Toast.LENGTH_LONG).show();

        //on met a jour la variable qui permet de distinguer le user online qui a le focus de ceux qui ne l'ont pas
        this.ID_USER_ONFOCUS = intent.getIntExtra("ID",0);

        recyclerView = findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        smoothScroller = new LinearSmoothScroller(this) {
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };


        allUsersAdapter = new ConversationspriveeAdapter(this,data_recyclerview);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(allUsersAdapter);



        rootView = findViewById(R.id.appbarlayout);
        add_emoji = findViewById(R.id.add_emoji);
        submitcomment = findViewById(R.id.submitcomment);
        back_button = findViewById(R.id.back_button);

        AfficherMessage();

        emojIcon= new EmojIconActions(this, rootView,  editcomment,
                add_emoji);
        emojIcon.ShowEmojIcon();

        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard","open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard","close");
            }
        });

        submitcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
                SaveMessage();
                MessageCorps();
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                finish();
            }
        });



    }


    public void MessageCorps() {
        Drawable bossdraw = getResources().getDrawable(R.drawable.rounded_corner);
        Drawable bossdraw2 = getResources().getDrawable(R.drawable.rounded_corner1);
        //database.addConversation(new Conversation(String.valueOf(ID),message,"2"));
        data_recyclerview.add(new Conversationprivateitem(intent.getStringExtra("imageview"),R.drawable.arrow_bg1,
                editcomment.getText().toString(),bossdraw,context_messageconstitution,R.drawable.arrow_bg2,
                user.getPHOTO(),editcomment.getText().toString(),bossdraw2,false,true));

        smoothScroller.setTargetPosition((data_recyclerview.size()-1));
        layoutManager.startSmoothScroll(smoothScroller);

        allUsersAdapter.notifyDataSetChanged();
        //smoothScroller.setTargetPosition((data_recyclerview.size()-1));
        editcomment.getText().clear();
    }


    public void SendMessage() {

        String url_fcm_send_messages = Const.dns
                .concat("/Apifcm/apiFCMmessagerie.php?message=")
                .concat(editcomment.getText().toString())
                .concat("&ID=").concat(String.valueOf(user.getID()))
                .concat("&phoro=").concat(user.getPHOTO())
                .concat("&succes=1")
                .concat("&name=").concat(user.getPRENOM()+" "+user.getNOM())
                .concat("&regId=").concat(intent.getStringExtra("KeyPush"))
                .concat("&nom=Wazzaby");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_fcm_send_messages,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MessageConstitution.this,"Erreur réseau !!",Toast.LENGTH_LONG).show();
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

    public void SaveMessage() {

        String url_save_message = Const.dns
                .concat("/WazzabyApi/public/api/InsertMessage?id_eme=")
                .concat(String.valueOf(user.getID()))
                .concat("&id_recept=").concat(String.valueOf(intent.getIntExtra("ID",0)))
                .concat("&id_prob=").concat(String.valueOf(user.getIDPROB()))
                .concat("&message=").concat(editcomment.getText().toString())
                .concat("&anonymous=").concat(String.valueOf(user.getETAT()))
                .concat("&anonymous_recept=").concat(intent.getStringExtra("anonymous_recept"));


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_save_message,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MessageConstitution.this,"Erreur réseau !!",Toast.LENGTH_LONG).show();
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


    public void AfficherMessage() {

        //Toast.makeText(MessageConstitution.this," "+String.valueOf(intent.getIntExtra("ID",0)),Toast.LENGTH_LONG).show();

        String url_display_message = Const.dns
                .concat("/WazzabyApi/public/api/DisplayMessage?id_user=")
                .concat(String.valueOf(user.getID()))
                .concat("&id_prob=").concat(String.valueOf(user.getIDPROB()))
                .concat("&id_recep=").concat(String.valueOf(intent.getIntExtra("ID",0)));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_display_message,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(MessageConstitution.this," "+response,Toast.LENGTH_LONG).show();
                        JSONDESENCODEMessage(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MessageConstitution.this,"Erreur réseau !!",Toast.LENGTH_LONG).show();
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

    public void JSONDESENCODEMessage(String response) {
        try {
            JSONArray reponse = new JSONArray(response);
            for(int i = (reponse.length()-1);i>=0;i--) {
                JSONObject object = reponse.getJSONObject(i);
                String message_libelle = object.getString("message");
                int id_recep = object.getInt("id_recep");
                int id_eme = object.getInt("id_eme");

                if (id_eme == user.getID()) {
                    Drawable bossdraw = getResources().getDrawable(R.drawable.rounded_corner);
                    Drawable bossdraw2 = getResources().getDrawable(R.drawable.rounded_corner1);
                    data_recyclerview.add(new Conversationprivateitem(intent.getStringExtra("imageview"),R.drawable.arrow_bg1,
                            message_libelle,bossdraw,context_messageconstitution,R.drawable.arrow_bg2,
                            user.getPHOTO(),message_libelle,bossdraw2,false,true));
                } else if (id_recep == user.getID()) {
                    Drawable bossdraw = getResources().getDrawable(R.drawable.rounded_corner);
                    Drawable bossdraw2 = getResources().getDrawable(R.drawable.rounded_corner1);
                    data_recyclerview.add(new Conversationprivateitem(intent.getStringExtra("imageview"),R.drawable.arrow_bg1,
                            message_libelle,bossdraw,context_messageconstitution,R.drawable.arrow_bg2,
                            user.getPHOTO(),message_libelle,bossdraw2,true,false));
                }

            }

        }catch (JSONException e) {
            e.printStackTrace();
        }

        if(data_recyclerview.size()>0) {
            smoothScroller.setTargetPosition((data_recyclerview.size()-1));
            layoutManager.startSmoothScroll(smoothScroller);
            allUsersAdapter.notifyDataSetChanged();
        }



    }







}
