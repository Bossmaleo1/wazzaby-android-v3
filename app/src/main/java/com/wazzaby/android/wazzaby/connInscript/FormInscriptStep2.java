package com.wazzaby.android.wazzaby.connInscript;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wazzaby.android.wazzaby.R;

public class FormInscriptStep2 extends AppCompatActivity {

    private Toolbar toolbar;
    private Resources res;
    private MaterialButton send;
    private String sexe;
    private TextInputEditText nom;
    private TextInputEditText prenom;
    private Intent intent;
    private RadioGroup monsexeradioGroup;
    private CoordinatorLayout coordinatorLayout;
    private TextInputLayout error_nom;
    private TextInputLayout error_prenom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forminscript2);
        res = getResources();
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        Animation anim = AnimationUtils.loadAnimation(this,R.anim.slide_in);
        findViewById(R.id.coordinatorLayout).startAnimation(anim);
        toolbar =  findViewById(R.id.toolbar);
        send = findViewById(R.id.send);
        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        monsexeradioGroup =  findViewById(R.id.monsexe);
        intent = getIntent();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(res.getString(R.string.inscript1_2));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        error_nom = findViewById(R.id.text_input_layout_nom);
        error_prenom = findViewById(R.id.text_input_layout_prenom);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()==true) {
                    int radioButtonID = monsexeradioGroup.getCheckedRadioButtonId();
                    View radioButton = monsexeradioGroup.findViewById(radioButtonID);
                    int idx = monsexeradioGroup.indexOfChild(radioButton);
                    RadioButton r = (RadioButton) monsexeradioGroup.getChildAt(idx);
                    String selectedtext = r.getText().toString();
                    if (selectedtext.trim().equals("Masculin")) {
                        sexe = "H";
                    }else if (selectedtext.trim().equals("Feminin")) {
                        sexe = "F";
                    }
                    Intent intent1 = new Intent(getApplicationContext(),FormInscriptStep3.class);
                    intent1.putExtra("sexe",sexe);
                    intent1.putExtra("prenom",String.valueOf(prenom.getText()));
                    intent1.putExtra("nom",String.valueOf(nom.getText()));
                    intent1.putExtra("code",intent.getStringExtra("code"));
                    intent1.putExtra("email",intent.getStringExtra("email"));
                    startActivity(intent1);
                }

            }
        });


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
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }


    public boolean validate() {
        boolean valid = true;
        String _nom  = nom.getText().toString();
        String _prenom = prenom.getText().toString();

        int radioButtonID = monsexeradioGroup.getCheckedRadioButtonId();
        View radioButton = monsexeradioGroup.findViewById(radioButtonID);
        int idx = monsexeradioGroup.indexOfChild(radioButton);
        RadioButton r = (RadioButton) monsexeradioGroup.getChildAt(idx);
        String selectedtext = r.getText().toString();

        if (selectedtext.trim().isEmpty()) {
            Toast.makeText(FormInscriptStep2.this,res.getString(R.string.inscript_sexe),Toast.LENGTH_LONG).show();
            valid = false;
        }

        if (_nom.isEmpty()) {
            error_nom.setError(res.getString(R.string.nom_error));
            valid = false;
        }else {
            error_nom.setError(null);
        }

        if (_prenom.isEmpty()) {
            error_prenom.setError(res.getString(R.string.prenom_error));
            valid = false;
        }else {
            error_prenom.setError(null);
        }


        return valid;
    }





}
