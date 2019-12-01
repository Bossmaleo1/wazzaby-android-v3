package com.wazzaby.android.wazzaby.connInscript;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.appviews.Home;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.Profil;

public class MainActivity extends AppCompatActivity {

    private SessionManager session;
    private RelativeLayout block;
    private Profil user;
    private DatabaseHandler database;
    private String dark_mode_item = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        database = new DatabaseHandler(this);
        session = new SessionManager(this);

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
        setContentView(R.layout.activity_main);
        block = findViewById(R.id.block);
        if (dark_mode_item.equals("1"))
        {
            block.setBackgroundColor(getResources().getColor(R.color.darkprimary));
            //setTheme(R.style.AppDarkTheme);
            //edit_modenuit.setChecked(true);
        } else if (dark_mode_item.equals("0")) {
            //setTheme(R.style.AppTheme);
            //edit_modenuit.setChecked(false);
        }
        if(!String.valueOf(session.getUserDetail().get(SessionManager.Key_ID)).equals("null")) {
            user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
        }

        Thread background = new Thread() {
            public void run() {

                try {

                    sleep(50);
                    if(!session.IsLoggedIn()) {
                        Intent i = new Intent(getApplicationContext(), Connexion.class);
                        startActivity(i);
                    }else
                    {
                        if(user.getIDPROB().equals("yoyo"))
                        {
                            Intent i = new Intent(getApplicationContext(), ProblematiqueConnexion.class);
                            startActivity(i);
                        }else {
                            Intent i = new Intent(getApplicationContext(), Home.class);
                            startActivity(i);
                        }
                    }
                    finish();

                } catch (Exception e) {

                }
            }
        };

        background.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
