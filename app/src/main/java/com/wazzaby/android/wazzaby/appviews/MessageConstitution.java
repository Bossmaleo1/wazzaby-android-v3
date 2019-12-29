package com.wazzaby.android.wazzaby.appviews;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.net.Uri;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.adapter.ConversationspriveeAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.app.AppCompatActivity;
import com.wazzaby.android.wazzaby.R;
import java.util.ArrayList;
import java.util.List;
import com.wazzaby.android.wazzaby.model.data.Conversationprivateitem;
import com.wazzaby.android.wazzaby.model.data.Profil;
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

        //on met a jour la variable qui permet de distinguer le user online qui a le focus de ceux qui ne l'ont pas
        this.ID_USER_ONFOCUS = intent.getIntExtra("ID",0);

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allUsersAdapter = new ConversationspriveeAdapter(this,data_recyclerview);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(allUsersAdapter);
        rootView = findViewById(R.id.appbarlayout);
        add_emoji = findViewById(R.id.add_emoji);
        submitcomment = findViewById(R.id.submitcomment);
        back_button = findViewById(R.id.back_button);

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


        //Toast.makeText(getApplicationContext()," "+intent.getIntExtra("ID",0),Toast.LENGTH_LONG).show();

    }


    public void MessageCorps() {
        Drawable bossdraw = getResources().getDrawable(R.drawable.rounded_corner);
        Drawable bossdraw2 = getResources().getDrawable(R.drawable.rounded_corner1);
        //database.addConversation(new Conversation(String.valueOf(ID),message,"2"));
        data_recyclerview.add(new Conversationprivateitem(intent.getStringExtra("imageview"),R.drawable.arrow_bg1,
                editcomment.getText().toString(),bossdraw,context_messageconstitution,R.drawable.arrow_bg2,
                intent.getStringExtra("imageview"),editcomment.getText().toString(),bossdraw2,true,false));
        allUsersAdapter.notifyDataSetChanged();
        editcomment.getText().clear();
    }



}
