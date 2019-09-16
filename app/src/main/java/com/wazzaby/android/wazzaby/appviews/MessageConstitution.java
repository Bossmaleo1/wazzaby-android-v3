package com.wazzaby.android.wazzaby.appviews;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.adapter.ConversationspriveeAdapter;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.Conversation;
import com.wazzaby.android.wazzaby.model.data.Conversationprivateitem;
import com.wazzaby.android.wazzaby.model.data.Profil;

import java.util.ArrayList;
import java.util.List;

public class MessageConstitution extends AppCompatActivity {

    private Toolbar toolbar;
    private Intent intent;
    private Resources res;
    private SimpleDraweeView profilimage;
    //private ImageView arrow_back;
    private TextView Profiltextname;
    private String maleodrawable;
    private RecyclerView recyclerView;
    private ConversationspriveeAdapter allUsersAdapter;
    private String KEYPUSH;
    private int ID;
    private String Nom;
    private String PHOTO;
    private ImageView sender;
    private EditText editvalue;
    private List<Conversationprivateitem> data = new ArrayList<>();
    private Context context;
    private DatabaseHandler database;
    private Profil user;
    private SessionManager session;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String Libelle = null;

    private List<Conversation> conversationList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messageconstitution);

        res = getResources();
        profilimage = findViewById(R.id.imageToolBar);
        toolbar =  findViewById(R.id.toolbar);
        intent = getIntent();
        session = new SessionManager(this);
        database = new DatabaseHandler(this);
        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
        Profiltextname = findViewById(R.id.toolbarText);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Profiltextname.setText(intent.getStringExtra("name"));
        ID = intent.getIntExtra("ID",0);
        conversationList = database.getAllConversation(String.valueOf(ID));
        KEYPUSH=intent.getStringExtra("KEYPUSH");
        PHOTO=intent.getStringExtra("PHOTO");
        Nom = intent.getStringExtra("name");
        sender = findViewById(R.id.sender);
        editvalue = findViewById(R.id.editvalue);
        maleodrawable= intent.getStringExtra("imageview");
        context = this;
        Uri uri = Uri.parse("https://wazaby939393.000webhostapp.com/Images/" + maleodrawable);
        profilimage.setImageURI(uri);
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allUsersAdapter = new ConversationspriveeAdapter(this,data);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(allUsersAdapter);
    }
}
