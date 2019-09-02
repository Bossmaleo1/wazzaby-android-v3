package com.wazzaby.android.wazzaby.appviews;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.NotificationItem;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationsdetails);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Message Public");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionManager(this);
        database = new DatabaseHandler(this);
        intent = getIntent();
        notificationItem = intent.getParcelableExtra("notificationitem");

        icon = findViewById(R.id.icon);
        title = findViewById(R.id.title);
        title1 = findViewById(R.id.title1);
        photo_du_poste_background = findViewById(R.id.photo_du_poste_background);
        contenu = findViewById(R.id.contenu);
        contenucomment = findViewById(R.id.contenucomment);
        nombre_de_jaime = findViewById(R.id.nombre_de_jaime);
        nombre_de_jaimepas = findViewById(R.id.nombre_de_jaimepas);
        photo_du_poste =  findViewById(R.id.photo_du_poste);

        //Gestion de la coloration
        if (notificationItem.getCheckmention() == 1){
            this.booljaime = true;
        } else if (notificationItem.getCheckmention() == 2){
            this.booljaimepas = true;
        } else if (notificationItem.getCheckmention() == 0){
            this.booljaime = false;
            this.booljaimepas = false;
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



}
