package com.wazzaby.android.wazzaby.appviews;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.Profil;

public class ProfilUser extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil);
        toolbar = findViewById(R.id.toolbar);
        edit_name = findViewById(R.id.edit_name);
        edit_problematique = findViewById(R.id.edit_problematique);
        res = getResources();
        intent = getIntent();
        database = new DatabaseHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        draweeView = findViewById(R.id.my_image_view);
        edit_name.setText(user.getPRENOM()+" "+user.getNOM());
        edit_problematique.setText(user.getLibelle_prob());
        collapsingToolbarLayout.setTitle(" ");
        collapsingToolbarLayout.setContentScrimColor(res.getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        Drawable maleoIcon = res.getDrawable(R.drawable.ic_arrow_back_black_24dp);
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

        //shall wd get the problematique block
        problematique_block = findViewById(R.id.problematique_block);
        //shall we get the click event
        problematique_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Problematique.class);
                startActivity(intent);

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profil, menu);
        MenuItem edit_profil_menu = menu.findItem(R.id.profil_user);
        Drawable edit_profil_icon = edit_profil_menu.getIcon();
        edit_profil_icon.mutate().setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.SRC_IN);
        edit_profil_menu.setIcon(edit_profil_icon);
        return true;
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

            case R.id.profil_user:
                /*Intent intentprofildetails = new Intent(getApplicationContext(),ProfilDetails.class);
                startActivity(intentprofildetails);*/
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
