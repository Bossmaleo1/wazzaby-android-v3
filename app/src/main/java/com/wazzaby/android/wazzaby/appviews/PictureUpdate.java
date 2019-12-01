package com.wazzaby.android.wazzaby.appviews;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.connInscript.ImagePickerActivity;
import com.wazzaby.android.wazzaby.fragments.BottomSheetFragment;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;

public class PictureUpdate extends AppCompatActivity {

    private Toolbar toolbar;
    private Resources res;
    private ImageView icon_edit_photo;
    private View view;
    private String dark_mode_item = null;
    private DatabaseHandler database;
    private SessionManager session;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

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
        setContentView(R.layout.pictureupdate);
        res = getResources();
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        icon_edit_photo = findViewById(R.id.icon_edit_photo);





        icon_edit_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                view = getLayoutInflater().inflate(R.layout.bottom_sheet_picture, null);

            }
        });


        ImagePickerActivity.clearCache(this);


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

    public void DarkMode(Drawable maleoIcon) {
        //toolbar.setBackgroundColor(getResources().getColor(R.color.darkprimary));
    }

    public void LightMode(Drawable maleoIcon) {

    }


}
