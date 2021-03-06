package com.wazzaby.android.wazzaby.appviews;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import com.google.android.material.snackbar.Snackbar;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.adapter.displaycommentaryadapter;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.Profil;
import com.wazzaby.android.wazzaby.model.data.displaycommentary;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import developer.semojis.Helper.EmojiconEditText;
import developer.semojis.actions.EmojIconActions;

public class AfficheCommentairePublic extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {

    private Toolbar toolbar;
    private Resources res;
    private DatabaseHandler database;
    private SessionManager session;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Snackbar snackbar;
    private JSONObject object;
    private CoordinatorLayout coordinatorLayout;
    private Context context;
    private List<displaycommentary> data = new ArrayList<>();
    private Intent intent;
    private displaycommentaryadapter allUsersAdapter;
    private Profil user;
    private String Libelle = null;
    private ImageView submitcomment;
    private EmojiconEditText editcomment;
    private LinearLayout block_affichage_error;
    private TextView Error_text_message;

    private int countcommentitem;
    private String dateitem;
    private int commentitem_id;
    private String libelleitem;
    private String dark_mode_item = null;
    private RelativeLayout block_messagerie_edit_text;
    private ImageView add_emoji;
    private EmojIconActions emojIcon;
    private View rootView;


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
        setContentView(R.layout.affichemessagepublic);
        res = getResources();
        database = new DatabaseHandler(this);
        session = new SessionManager(getApplicationContext());
        toolbar =  findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressbar);
        submitcomment = findViewById(R.id.submitcomment);
        rootView = findViewById(R.id.appbarlayout);
        block_messagerie_edit_text = findViewById(R.id.block_messagerie_edit_text);
        add_emoji = findViewById(R.id.add_emoji);
        editcomment = findViewById(R.id.editcomment);
        block_affichage_error = findViewById(R.id.materialcardview);
        Error_text_message = findViewById(R.id.text_error_message);
        intent = getIntent();
        context = this;
        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
        setSupportActionBar(toolbar);
        Drawable maleoIcon = res.getDrawable(R.drawable.ic_close_black_24dp);
        maleoIcon.mutate().setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.SRC_IN);
        this.getSupportActionBar().setHomeAsUpIndicator(maleoIcon);
        getSupportActionBar().setTitle(res.getString(R.string.commentaire));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allUsersAdapter = new displaycommentaryadapter(this,data);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(allUsersAdapter);

        this.Connexion();
        block_affichage_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                block_affichage_error.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                Connexion();
            }
        });

        recyclerView.addOnItemTouchListener(new AfficheCommentairePublic.RecyclerTouchListener(getApplicationContext(), recyclerView, new AfficheCommentairePublic.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                PopupMenu dropDownMenu = new PopupMenu(context, view);
                dropDownMenu.getMenuInflater().inflate(R.menu.drop_down_menu, dropDownMenu.getMenu());
                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Toast.makeText(context, "You have clicked " + menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
                dropDownMenu.show();
            }
        }));

        submitcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editcomment.getText().toString().isEmpty()) {

                    displaycommentary commentary;
                    if(Integer.valueOf(user.getETAT()) == 1) {
                        commentary = new displaycommentary("http://wazzaby.com/uploads/photo_de_profil/" + user.getPHOTO(), context, R.drawable.ic_done_black_18dp
                                , "A l'instant", R.color.greencolor, 0, editcomment.getText().toString(),
                                "Utilisateur" + " " + "Anonyme",0);
                    } else {
                        commentary = new displaycommentary("http://wazzaby.com/uploads/photo_de_profil/" + user.getPHOTO(), context, R.drawable.ic_done_black_18dp
                                , "A l'instant", R.color.greencolor, 0, editcomment.getText().toString(),
                                user.getPRENOM() + " " + user.getNOM(),0);
                    }

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

        //si le dark mode est activé
        if (dark_mode_item.equals("1"))
        {
            block_messagerie_edit_text.setBackgroundColor(res.getColor(android.R.color.white));
        }

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

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private AfficheCommentairePublic.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final AfficheCommentairePublic.ClickListener clickListener) {
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

    private void Connexion()
    {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Const.dns+"/WazzabyApi/public/api/displayComment?id_messagepublic="+String.valueOf(intent.getIntExtra("nom",0)),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray reponse = new JSONArray(response);
                            /*for(int i = 0;i<reponse.length();i++)
                            {*/

                            if  (reponse.length()>0) {
                                object = reponse.getJSONObject(0);
                                displaycommentary commentary = new displaycommentary(object.getString("user_photo"),context,R.drawable.ic_done_all_black_18dp
                                        ,object.getString("updated"),R.color.greencolor,2,object.getString("status_text_content"),
                                        object.getString("name"),0);
                                data.add(commentary);

                                countcommentitem = object.getInt("countcomment");
                                dateitem = object.getJSONObject("date").getString("date");
                                commentitem_id = object.getInt("id");
                                libelleitem = object.getString("status_text_content");

                                if (countcommentitem>1) {
                                    getSupportActionBar().setTitle("Commentaires");
                                }

                                ConnexionCommentItem();
                            }


                            if (reponse.length() == 0) {
                                Error_text_message.setText(" Aucun commentaire");
                                block_affichage_error.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);
                        allUsersAdapter.notifyDataSetChanged();
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
                                            Connexion();
                                        }
                                    });

                            snackbar.show();
                            progressBar.setVisibility(View.GONE);
                        }else if(error instanceof NetworkError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_servererror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Connexion();
                                        }
                                    });

                            snackbar.show();
                            progressBar.setVisibility(View.GONE);
                        }else if(error instanceof AuthFailureError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_servererror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Connexion();
                                        }
                                    });

                            snackbar.show();
                            progressBar.setVisibility(View.GONE);
                        }else if(error instanceof ParseError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_servererror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Connexion();
                                        }
                                    });

                            snackbar.show();
                            progressBar.setVisibility(View.GONE);
                        }else if(error instanceof NoConnectionError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_noconnectionerror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Connexion();
                                        }
                                    });

                            snackbar.show();
                            progressBar.setVisibility(View.GONE);
                        }else if(error instanceof TimeoutError)
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_timeouterror), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Connexion();
                                        }
                                    });
                            snackbar.show();
                            progressBar.setVisibility(View.GONE);
                        }else
                        {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, res.getString(R.string.error_volley_error), Snackbar.LENGTH_LONG)
                                    .setAction(res.getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Connexion();
                                        }
                                    });

                            snackbar.show();
                            progressBar.setVisibility(View.GONE);
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
        String url = Const.dns+"/WazzabyApi/public/api/addComment?id_user="+String.valueOf(user.getID())+"&id_messagepublic="
                +String.valueOf(intent.getIntExtra("nom",0))+"&libelle_comment="+Libelle+"&anonymous="+String.valueOf(user.getETAT());
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        data.get((data.size()-1)).setIcononline(R.drawable.ic_done_all_black_18dp);
                        allUsersAdapter.notifyDataSetChanged();

                        if (!String.valueOf(intent.getIntExtra("id_recepteur",0)).equals(String.valueOf(user.getID()))) {

                            Connexion_Insert_Notification();
                            SendCommentaryPushNotification();

                        }
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

    public void Connexion_Insert_Notification() {
        String message;
        if (Integer.valueOf(user.getETAT()) == 1) {
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
                .concat("&id_recepteur=").concat(String.valueOf(intent.getIntExtra("id_recepteur",0)))
                .concat("&anonymous=").concat(String.valueOf(user.getETAT()));

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
    }


    public void ConnexionCommentItem() {

        String url_displaycommentitem = Const.dns
                .concat("/WazzabyApi/public/api/displayCommentItem?id_messagepublic=")
                .concat(String.valueOf(intent.getIntExtra("nom",0)))
                .concat("&date=").concat(dateitem)
                .concat("&libelle=").concat(libelleitem)
                .concat("&comment_id=").concat(String.valueOf(commentitem_id));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_displaycommentitem,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray reponse = new JSONArray(response);

                            if (reponse.length()==1) {

                                int tempcountitem = Integer.valueOf(countcommentitem - data.size());

                                if(tempcountitem >= 1) {

                                    object = reponse.getJSONObject(0);
                                    displaycommentary commentary = new displaycommentary(object.getString("user_photo"), context, R.drawable.ic_done_all_black_18dp
                                            , object.getString("updated"), R.color.greencolor, 2, object.getString("status_text_content"),
                                            object.getString("name"),1);

                                    //countcommentitem = object.getInt("countcomment");
                                    dateitem = object.getJSONObject("date").getString("date");
                                    commentitem_id = object.getInt("id");
                                    libelleitem = object.getString("status_text_content");

                                    //shall we test if our data array is not empty
                                    if(data.size()>0) {
                                        //we block our previous shimmer the shimmer of our last item
                                        data.get((data.size()-1)).setState_shimmer(0);
                                    }
                                    data.add(commentary);



                                    ConnexionCommentItem();
                                }

                                if (tempcountitem == 1) {
                                    data.get((data.size() - 1)).setState_shimmer(0);
                                }


                            }

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                        allUsersAdapter.notifyDataSetChanged();

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

    //Cette méthode est le service HTTP pour l'envoie d'une pushnotification du commentaire de l'utilisateur
    //Cette méthode est le service HTTP pour l'envoie d'une pushnotification du commentaire de l'utilisateur
    public void SendCommentaryPushNotification() {
        String message;
        if (Integer.valueOf(user.getETAT()) == 1) {
            message = "Votre message public vient d'etre commenter par un Utilisateur Anonyme";
        } else {
            message = "Votre message public vient d'etre commenter par "
                    .concat(user.getPRENOM()).concat(" ")
                    .concat(user.getNOM());
        }

        //On construit l'url de la pushnotification
        String pushnotification_url = Const.dns.concat("/Apifcm/apiFCMmessagerie.php?message=").concat(message).concat("&title=Wazzaby")
                .concat("&regId=").concat(String.valueOf(intent.getStringExtra("pushkeynotification")))
                .concat("&ID=").concat(String.valueOf(user.getID()))
                .concat("&succes=0")
                .concat("&name=").concat(user.getPRENOM()+" "+user.getNOM())
                .concat("&nom=Wazzaby")
                .concat("&phoro=").concat(user.getPHOTO());
        ConnexionToServer(pushnotification_url);

    }

    //This volley method allow us the ability to insert in our commentary pushnotification
    public void ConnexionToServer(String url_notification) {

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
    }

}
