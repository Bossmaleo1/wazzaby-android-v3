package com.wazzaby.android.wazzaby.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.emoji.widget.EmojiTextView;
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
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.appviews.AfficheCommentairePublic;
import com.wazzaby.android.wazzaby.fragments.Conversationspublic;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.ConversationPublicItem;
import com.wazzaby.android.wazzaby.model.data.Profil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConversationspublicAdapter  extends RecyclerView.Adapter<ConversationspublicAdapter.MyViewHolder>  implements MenuItem.OnMenuItemClickListener {

    List<ConversationPublicItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private Boolean test_user_root;
    private DatabaseHandler database;
    private SessionManager session;
    private ProgressDialog pDialog;
    private int anonymous;
    private boolean booljaime;
    private boolean booljaimepas;
    private Resources res;
    private Drawable iconjaime;
    private Drawable iconjaimepas;
    private Profil user;
    private int temp_id_checkmention;
    private JSONObject reponse;
    private String dark_mode_item = null;



    public ConversationspublicAdapter(Context context,List<ConversationPublicItem> data)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
        this.session = new SessionManager(context);
        this.database = new DatabaseHandler(context);
        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
    }

    public void delete(int position)
    {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ConversationspublicAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.adapterconversationspublic,parent,false);
        ConversationspublicAdapter.MyViewHolder holder = new ConversationspublicAdapter.MyViewHolder(view);


        /**/
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ConversationspublicAdapter.MyViewHolder holder, final int position) {
        final ConversationPublicItem current = data.get(position);
        res = holder.itemView.getContext().getResources();

        iconjaime = res.getDrawable(R.drawable.baseline_thumb_up_alt_black_24);
        iconjaimepas = res.getDrawable(R.drawable.baseline_thumb_down_alt_black_24);


        //Gestion de la coloration
        if (current.getCheckmention() == 1){
            this.booljaime = true;
            iconjaime.mutate().setColorFilter(Color.parseColor("#188dc8"), PorterDuff.Mode.SRC_IN);
            iconjaimepas.mutate().setColorFilter(Color.parseColor("#9E9E9E"),PorterDuff.Mode.SRC_IN);
            holder.icon_jaime.setImageDrawable(iconjaime);
            holder.icon_jaimepas.setImageDrawable(iconjaimepas);
            holder.nombre_de_jaime.setText(String.valueOf(current.getCountjaime()));
            holder.nombre_de_jaimepas.setText(String.valueOf(current.getCountjaimepas()));
            holder.nombre_de_jaime.setTextColor(Color.parseColor("#188dc8"));
            holder.nombre_de_jaimepas.setTextColor(Color.parseColor("#9E9E9E"));
        } else if (current.getCheckmention() == 2){
            this.booljaimepas = true;
            iconjaimepas.mutate().setColorFilter(Color.parseColor("#188dc8"), PorterDuff.Mode.SRC_IN);
            iconjaime.mutate().setColorFilter(Color.parseColor("#9E9E9E"),PorterDuff.Mode.SRC_IN);
            holder.icon_jaime.setImageDrawable(iconjaime);
            holder.icon_jaimepas.setImageDrawable(iconjaimepas);
            holder.nombre_de_jaime.setText(String.valueOf(current.getCountjaime()));
            holder.nombre_de_jaimepas.setText(String.valueOf(current.getCountjaimepas()));
            holder.nombre_de_jaimepas.setTextColor(Color.parseColor("#188dc8"));
            holder.nombre_de_jaime.setTextColor(Color.parseColor("#9E9E9E"));
        } else if (current.getCheckmention() == 0){
            this.booljaime = false;
            this.booljaimepas = false;
            iconjaime.mutate().setColorFilter(Color.parseColor("#9E9E9E"), PorterDuff.Mode.SRC_IN);
            //iconjaimepas.mutate().setColorFilter(Color.parseColor("#188dc8"),PorterDuff.Mode.SRC_IN);
            holder.icon_jaime.setImageDrawable(iconjaime);
            holder.icon_jaimepas.setImageDrawable(iconjaimepas);
            holder.nombre_de_jaime.setText(String.valueOf(current.getCountjaime()));
            holder.nombre_de_jaimepas.setText(String.valueOf(current.getCountjaimepas()));
            holder.nombre_de_jaimepas.setTextColor(Color.parseColor("#9E9E9E"));
            holder.nombre_de_jaime.setTextColor(Color.parseColor("#9E9E9E"));
        }

        holder.title.setText(current.getNameMembreProb());
        holder.title1.setText(current.getDatetime());
        holder.contenu.setText(current.getContenu());
        holder.contenu.setTextColor(0xff000000);
        holder.commentnumber.setText(current.getCommentnumber());
        if(!current.getImageID().equals("null")) {
            Uri uri = Uri.parse(Const.dns+"/uploads/photo_de_profil/" + current.getImageID());
            holder.picture.setImageURI(uri);
        }else
        {
            holder.picture.setImageResource(R.drawable.ic_profile_colorier);
        }
        holder.commenticon.setImageResource(current.getIconComment());
        holder.commentblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AfficheCommentairePublic.class);
                intent.putExtra("nom",current.getID());
                intent.putExtra("anonymous",current.getAnonymous());
                intent.putExtra("id_recepteur",current.getId_recepteur());
                intent.putExtra("pushkeynotification",current.getPushkey_recepteur());
                context.startActivity(intent);
            }
        });

        holder.menuderoulant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu dropDownMenu = new PopupMenu(context, holder.menuderoulant);
                if (Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)) == current.getID_EME()) {
                    dropDownMenu.getMenuInflater().inflate(R.menu.drop_down_menu, dropDownMenu.getMenu());
                } else {
                    dropDownMenu.getMenuInflater().inflate(R.menu.drop_down_menu1, dropDownMenu.getMenu());
                }
                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getTitle().equals("Supprimer")) {

                            new MaterialAlertDialogBuilder(context)
                                    .setTitle("Supprimer")
                                    .setMessage("Voulez-vous supprimer ce message public ?")
                                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                            String urldeletephoto = Const.dns.concat("/uploads/removeuploadScript.php?nomdufichier=")
                                                    .concat(current.getImageID());

                                            String urldeletemessagepublic = Const.dns.concat("/WazzabyApi/public/api/deletephotomessagepublic?ID=")
                                                    .concat(String.valueOf(current.getID())).concat("&ID_photo=")
                                                    .concat(String.valueOf(current.getId_photo())).concat("&count=")
                                                    .concat(current.getCommentnumber());
                                            ConnexionDeletePhoto(urldeletephoto,current);
                                            ConnexionDeleteMessagePublic(urldeletemessagepublic);
                                        }
                                    })
                                    .setNegativeButton("Non",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if(current.getEtat_photo_status().equals("block")) {
                                                String urldeletephoto = Const.dns.concat("/uploads/removeuploadScript.php?nomdufichier=")
                                                        .concat(current.getImageID());

                                                String urldeletemessagepublic = Const.dns.concat("/WazzabyApi/public/api/deletephotomessagepublic?ID=")
                                                        .concat(String.valueOf(current.getID())).concat("&ID_photo=")
                                                        .concat(String.valueOf(current.getId_photo())).concat("&count=")
                                                        .concat(current.getCommentnumber());
                                                ConnexionDeletePhoto(urldeletephoto,current);
                                                ConnexionDeleteMessagePublic(urldeletemessagepublic);
                                            }

                                        }
                                    })
                                    .show();
                        }
                        return true;
                    }
                });
                dropDownMenu.show();
            }
        });

        //on test l'affichage ou non affichage des images des postes
        if (current.getEtat_photo_status().equals("block")) {
            holder.photo_du_poste.setVisibility(View.VISIBLE);
            holder.photo_du_poste.setMinimumWidth(1024);
            holder.photo_du_poste.setMinimumHeight(768);
            holder.photo_du_poste_background.setMinimumWidth(1024);
            holder.photo_du_poste_background.setMinimumHeight(768);
            holder.photo_du_poste_background.setImageResource(R.drawable.baseline_insert_photo_black_48);
            Uri uri = Uri.parse(current.getStatus_photo());
            holder.photo_du_poste_background.setImageURI(uri);
        }else if(current.getEtat_photo_status().equals("none")) {
            holder.photo_du_poste.setVisibility(View.GONE);
        }

        //La gestion du click j'aime
        holder.like_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int anonymous;
                String message;
                if (String.valueOf(user.getETAT()).equals("1")) {
                    anonymous = 1;
                    message = "Votre message public vient de faire reagir "
                            .concat("Utilisateur")
                            .concat(" ")
                            .concat("Anonyme");
                }else {
                    anonymous = 0;
                    message = "Votre message public vient de faire reagir "
                            .concat(user.getPRENOM())
                            .concat(" ")
                            .concat(user.getNOM());
                }


                String url_notification = Const.dns.concat("/WazzabyApi/public/api/InsertNotification?users_id=")
                        .concat(String.valueOf(user.getID()))
                        .concat("&libelle=").concat(message)
                        .concat("&id_type=").concat(String.valueOf(data.get(position).getID()))
                        .concat("&etat=0").concat("&id_recepteur=").concat(String.valueOf(data.get(position).getId_recepteur()))
                        .concat("&anonymous=").concat(String.valueOf(user.getETAT()));

                String  pushnotification_url = Const.dns.concat("/Apifcm/apiFCMmessagerie.php?message=")
                        .concat(message)
                        .concat("&title=Wazzaby")
                        .concat("&regId=").concat(data.get(position).getPushkey_recepteur())
                        .concat("&phoro=").concat(user.getPHOTO())
                        .concat("&ID=").concat(String.valueOf(user.getID()))
                        .concat("&name=").concat(user.getPRENOM()+" "+user.getNOM())
                        .concat("&nom=Wazzaby")
                        .concat("&succes=1");

                if(data.get(position).getCheckmention() == 1) {

                    Drawable iconjaime931 = res.getDrawable(R.drawable.baseline_thumb_up_alt_black_24);
                    String url = Const.dns.concat("/WazzabyApi/public/api/MentionsUpdate?id_etat=0")
                            .concat("&id_mention=")
                            .concat(String.valueOf(data.get(position).getId_checkmention()));

                    ConnexionToServer(url);
                    booljaime = false;
                    int jaime = data.get(position).getCountjaime();
                    jaime--;
                    holder.nombre_de_jaime.setText(String.valueOf(jaime));
                    holder.nombre_de_jaime.setTextColor(Color.parseColor("#9E9E9E"));
                    iconjaime931.mutate().setColorFilter(Color.parseColor("#9E9E9E"), PorterDuff.Mode.SRC_IN);
                    holder.icon_jaime.setImageDrawable(iconjaime931);
                    data.get(position).setCheckmention(0);
                    data.get(position).setCountjaime(jaime);
                    data.set(position,data.get(position));
                    holder.nombre_de_jaime.setText(String.valueOf(data.get(position).getCountjaime()));

                } else if (data.get(position).getCheckmention() == 0 && data.get(position).getId_checkmention() != 0) {

                    String url = Const.dns.concat("/WazzabyApi/public/api/MentionsUpdate?id_etat=1")
                            .concat("&id_mention=").concat(String.valueOf(data.get(position).getId_checkmention()));
                    ConnexionToServer(url);
                    booljaime = true;
                    booljaimepas = false;

                    Drawable iconjaime932 = res.getDrawable(R.drawable.baseline_thumb_up_alt_black_24);
                    Drawable iconjaimepas932 = res.getDrawable(R.drawable.baseline_thumb_down_alt_black_24);

                    holder.nombre_de_jaime.setTextColor(Color.parseColor("#188dc8"));
                    holder.nombre_de_jaimepas.setTextColor(Color.parseColor("#9E9E9E"));

                    iconjaime932.mutate().setColorFilter(Color.parseColor("#188dc8"), PorterDuff.Mode.SRC_IN);
                    iconjaimepas932.mutate().setColorFilter(Color.parseColor("#9E9E9E"), PorterDuff.Mode.SRC_IN);
                    holder.icon_jaime.setImageDrawable(iconjaime932);
                    holder.icon_jaimepas.setImageDrawable(iconjaimepas932);
                    int jaime = data.get(position).getCountjaime();
                    jaime++;
                    data.get(position).setCheckmention(1);
                    data.get(position).setCountjaime(jaime);

                    data.set(position,data.get(position));
                    holder.nombre_de_jaime.setText(String.valueOf(data.get(position).getCountjaime()));
                    if(data.get(position).getId_recepteur() != user.getID()) {
                        recordNotification(url_notification);
                        SendPushNotification(pushnotification_url);
                    }
                } else if (data.get(position).getId_checkmention() == 0 && data.get(position).getCheckmention() == 0) {
                    String url = Const.dns.concat("/WazzabyApi/public/api/Mentions?id_user=")
                            .concat(String.valueOf(user.getID())).concat("&id_libelle=").concat(String.valueOf(data.get(position).getID()))
                            .concat("&id_etat=1").concat("&mention=1");
                    ConnexionToServerWithPosition(url,position);

                    Drawable iconjaime933 = res.getDrawable(R.drawable.baseline_thumb_up_alt_black_24);
                    Drawable iconjaimepas933 = res.getDrawable(R.drawable.baseline_thumb_down_alt_black_24);


                    booljaime = true;
                    booljaimepas = false;
                    int jaime = data.get(position).getCountjaime();
                    jaime++;
                    data.get(position).setCountjaime(jaime);
                    //update of like color
                    holder.nombre_de_jaime.setTextColor(Color.parseColor("#188dc8"));
                    iconjaime933.mutate().setColorFilter(Color.parseColor("#188dc8"), PorterDuff.Mode.SRC_IN);
                    holder.icon_jaime.setImageDrawable(iconjaime933);
                    //mise à jour de la couleur du j'aime pas
                    holder.nombre_de_jaimepas.setTextColor(Color.parseColor("#9E9E9E"));
                    iconjaimepas933.mutate().setColorFilter(Color.parseColor("#9E9E9E"), PorterDuff.Mode.SRC_IN);
                    holder.icon_jaimepas.setImageDrawable(iconjaimepas933);
                    data.get(position).setCheckmention(1);
                    data.set(position,data.get(position));
                    holder.nombre_de_jaime.setText(String.valueOf(data.get(position).getCountjaime()));
                    if (data.get(position).getId_recepteur() != user.getID()) {
                        recordNotification(url_notification);
                        SendPushNotification(pushnotification_url);
                    }
                }else if (data.get(position).getCheckmention() == 2) {

                    String url = Const.dns.concat("/WazzabyApi/public/api/MentionsUpdate?id_etat=1")
                            .concat("&id_mention=")
                            .concat(String.valueOf(data.get(position).getId_checkmention()));

                    ConnexionToServerWithPosition(url,position);
                    booljaimepas = false;
                    booljaime = true;

                    Drawable iconjaime934 = res.getDrawable(R.drawable.baseline_thumb_up_alt_black_24);
                    Drawable iconjaimepas934 = res.getDrawable(R.drawable.baseline_thumb_down_alt_black_24);

                    holder.nombre_de_jaime.setTextColor(Color.parseColor("#188dc8"));
                    holder.nombre_de_jaimepas.setTextColor(Color.parseColor("#9E9E9E"));
                    iconjaime934.mutate().setColorFilter(Color.parseColor("#188dc8"), PorterDuff.Mode.SRC_IN);
                    iconjaimepas934.mutate().setColorFilter(Color.parseColor("#9E9E9E"), PorterDuff.Mode.SRC_IN);
                    holder.icon_jaime.setImageDrawable(iconjaime934);
                    holder.icon_jaimepas.setImageDrawable(iconjaimepas934);
                    int jaime = data.get(position).getCountjaime();
                    jaime++;
                    data.get(position).setCountjaime(jaime);
                    data.get(position).setCheckmention(1);
                    int jaimepas = data.get(position).getCountjaimepas();
                    jaimepas--;
                    data.get(position).setCountjaimepas(jaimepas);
                    data.set(position,data.get(position));
                    holder.nombre_de_jaime.setText(String.valueOf(data.get(position).getCountjaime()));
                    holder.nombre_de_jaimepas.setText(String.valueOf(data.get(position).getCountjaimepas()));
                    if (data.get(position).getId_recepteur() != user.getID()) {
                        recordNotification(url_notification);
                        SendPushNotification(pushnotification_url);
                    }
                }

            }
        });

        //La gestion du click je n'aime pas
        holder.dislike_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int anonymous;
                String message;
                if(String.valueOf(user.getETAT()).equals("1"))
                {
                    anonymous = 1;
                    message =  "Votre message public vient de faire reagir "
                            .concat("Utilisateur")
                            .concat(" ")
                            .concat("Anonyme");
                } else {
                    anonymous = 0;
                    message =  "Votre message public vient de faire reagir "
                            .concat(user.getPRENOM())
                            .concat(" ")
                            .concat(user.getNOM());
                }

                String url_notification = Const.dns.concat("/WazzabyApi/public/api/InsertNotification?users_id=")
                        .concat(String.valueOf(user.getID()))
                        .concat("&libelle=").concat(message)
                        .concat("&id_type=").concat(String.valueOf(data.get(position).getID()))
                        .concat("&etat=0").concat("&id_recepteur=").concat(String.valueOf(data.get(position).getId_recepteur()))
                        .concat("&anonymous=").concat(String.valueOf(user.getETAT()));

                String  pushnotification_url = Const.dns.concat("/Apifcm/apiFCMmessagerie.php?message=").concat(message).concat("&title=Wazzaby")
                        .concat("&regId=").concat(data.get(position).getPushkey_recepteur())
                        .concat("&phoro=").concat(user.getPHOTO())
                        .concat("&ID=").concat(String.valueOf(user.getID()))
                        .concat("&name=").concat(user.getPRENOM()+" "+user.getNOM())
                        .concat("&nom=Wazzaby")
                        .concat("&succes=1");

                if (data.get(position).getCheckmention() == 2) {

                    String url = Const.dns.concat("/WazzabyApi/public/api/MentionsUpdate?id_etat=0").concat("&id_mention=")
                            .concat(String.valueOf(data.get(position).getId_checkmention()));

                    Drawable iconjaimepas935 = res.getDrawable(R.drawable.baseline_thumb_down_alt_black_24);

                    ConnexionToServer(url);
                    booljaimepas = false;
                    holder.nombre_de_jaimepas.setTextColor(Color.parseColor("#9E9E9E"));
                    int jaimepas = data.get(position).getCountjaimepas();
                    jaimepas--;
                    data.get(position).setCountjaime(jaimepas);
                    data.get(position).setCheckmention(0);
                    iconjaimepas935.mutate().setColorFilter(Color.parseColor("#9E9E9E"), PorterDuff.Mode.SRC_IN);
                    holder.icon_jaimepas.setImageDrawable(iconjaimepas935);
                    data.set(position,data.get(position));
                    holder.nombre_de_jaimepas.setText(String.valueOf(data.get(position).getCountjaimepas()));


                }else if (data.get(position).getCheckmention() == 0 && data.get(position).getId_checkmention() != 0) {

                    String url = Const.dns.concat("/WazzabyApi/public/api/MentionsUpdate?id_etat=2")
                            .concat("&id_mention=").concat(String.valueOf(data.get(position).getId_checkmention()));
                    ConnexionToServer(url);
                    booljaime = false;
                    booljaimepas = true;
                    Drawable iconjaime936 = res.getDrawable(R.drawable.baseline_thumb_up_alt_black_24);
                    Drawable iconjaimepas936 = res.getDrawable(R.drawable.baseline_thumb_down_alt_black_24);
                    int jaimepas = data.get(position).getCountjaimepas();
                    jaimepas++;
                    data.get(position).setCheckmention(2);
                    data.get(position).setCountjaimepas(jaimepas);
                    holder.nombre_de_jaimepas.setTextColor(Color.parseColor("#188dc8"));
                    holder.nombre_de_jaime.setTextColor(Color.parseColor("#9E9E9E"));
                    iconjaime936.mutate().setColorFilter(Color.parseColor("#9E9E9E"), PorterDuff.Mode.SRC_IN);
                    iconjaimepas936.mutate().setColorFilter(Color.parseColor("#188dc8"), PorterDuff.Mode.SRC_IN);
                    holder.icon_jaime.setImageDrawable(iconjaime936);
                    holder.icon_jaimepas.setImageDrawable(iconjaimepas936);
                    data.set(position,data.get(position));
                    holder.nombre_de_jaimepas.setText(String.valueOf(data.get(position).getCountjaimepas()));
                    if(data.get(position).getId_recepteur() != user.getID()) {
                        recordNotification(url_notification);
                        SendPushNotification(pushnotification_url);
                    }
                }else if (data.get(position).getId_checkmention() == 0 && data.get(position).getCheckmention() == 0) {

                    String url = Const.dns.concat("/WazzabyApi/public/api/Mentions?id_user=").concat(String.valueOf(user.getID()))
                            .concat("&id_libelle=").concat(String.valueOf(data.get(position).getID()))
                            .concat("&id_etat=2").concat("&mention=2");

                    ConnexionToServerWithPosition(url,position);
                    booljaime = false;
                    booljaimepas = true;

                    Drawable iconjaime937 = res.getDrawable(R.drawable.baseline_thumb_up_alt_black_24);
                    Drawable iconjaimepas937 = res.getDrawable(R.drawable.baseline_thumb_down_alt_black_24);

                    int jaimepas = data.get(position).getCountjaimepas();
                    jaimepas++;
                    data.get(position).setCountjaimepas(jaimepas);
                    data.get(position).setCheckmention(2);
                    holder.nombre_de_jaimepas.setTextColor(Color.parseColor("#188dc8"));
                    holder.nombre_de_jaime.setTextColor(Color.parseColor("#9E9E9E"));
                    iconjaime937.mutate().setColorFilter(Color.parseColor("#9E9E9E"), PorterDuff.Mode.SRC_IN);
                    iconjaimepas937.mutate().setColorFilter(Color.parseColor("#188dc8"), PorterDuff.Mode.SRC_IN);
                    holder.icon_jaime.setImageDrawable(iconjaime937);
                    holder.icon_jaimepas.setImageDrawable(iconjaimepas937);
                    data.set(position,data.get(position));
                    holder.nombre_de_jaimepas.setText(String.valueOf(data.get(position).getCountjaimepas()));
                    if (data.get(position).getId_recepteur() != user.getID()) {
                        recordNotification(url_notification);
                        SendPushNotification(pushnotification_url);
                    }
                }else if (data.get(position).getCheckmention() == 1) {
                    String url = Const.dns.concat("/WazzabyApi/public/api/MentionsUpdate?id_etat=2")
                            .concat("&id_mention=").concat(String.valueOf(data.get(position).getId_checkmention()));
                    ConnexionToServerWithPosition(url,position);
                    booljaimepas = true;
                    booljaime = false;

                    Drawable iconjaime938 = res.getDrawable(R.drawable.baseline_thumb_up_alt_black_24);
                    Drawable iconjaimepas938 = res.getDrawable(R.drawable.baseline_thumb_down_alt_black_24);


                    holder.nombre_de_jaimepas.setTextColor(Color.parseColor("#188dc8"));
                    holder.nombre_de_jaime.setTextColor(Color.parseColor("#9E9E9E"));
                    int jaime = data.get(position).getCountjaime();
                    jaime--;
                    data.get(position).setCountjaime(jaime);
                    int jaimepas = data.get(position).getCountjaimepas();
                    jaimepas++;
                    data.get(position).setCountjaimepas(jaimepas);
                    data.get(position).setCheckmention(2);
                    iconjaime938.mutate().setColorFilter(Color.parseColor("#9E9E9E"), PorterDuff.Mode.SRC_IN);
                    iconjaimepas938.mutate().setColorFilter(Color.parseColor("#188dc8"), PorterDuff.Mode.SRC_IN);
                    holder.icon_jaime.setImageDrawable(iconjaime938);
                    holder.icon_jaimepas.setImageDrawable(iconjaimepas938);
                    data.set(position,data.get(position));
                    holder.nombre_de_jaimepas.setText(String.valueOf(data.get(position).getCountjaimepas()));
                    holder.nombre_de_jaime.setText(String.valueOf(data.get(position).getCountjaime()));
                    if (data.get(position).getId_recepteur() != user.getID()) {
                        recordNotification(url_notification);
                        SendPushNotification(pushnotification_url);
                    }
                }


            }
        });

        if (data.get(position).getState_shimmer() == 1) {
            holder.block_shimmer.setVisibility(View.VISIBLE);
            holder.mShimmerViewContainer.setVisibility(View.VISIBLE);
            holder.mShimmerViewContainer.startShimmer();
        } else if (data.get(position).getState_shimmer() == 0) {
            holder.block_shimmer.setVisibility(View.VISIBLE);
            holder.mShimmerViewContainer.setVisibility(View.GONE);
        }


        //La gestion du mode dark
        dark_mode_item = database.getDARKMODE();
        if (dark_mode_item.equals("1"))
        {
            holder.block_shimmer.setBackground(res.getDrawable(R.drawable.background_menu_message_public_mode_dark));
            holder.title.setTextColor(res.getColor(R.color.graycolor));
            holder.title1.setTextColor(res.getColor(R.color.graycolor));
            holder.commentnumber.setTextColor(res.getColor(R.color.graycolor));
            holder.contenu.setTextColor(res.getColor(R.color.graycolor));
            holder.block_globale.setBackgroundColor(res.getColor(R.color.darkprimary));
            holder.block_shimmer.setBackground(res.getDrawable(R.drawable.background_menu_message_public_mode_dark));
                    /*holder.block_shimmer.setBackground(res.getDrawable(R.drawable.background_menu_message_public_mode_dark));
                    holder.block_globale_notification.setBackgroundColor(res.getColor(R.color.darkprimarydark));
                    holder.user_first_name.setBackgroundColor(res.getColor(R.color.darkprimarydark));*/
        } else if (dark_mode_item.equals("0")) {

        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        TextView title1;
        EmojiTextView contenu;
        TextView commentnumber;
        SimpleDraweeView picture;
        ImageView commenticon;
        LinearLayout commentblock;
        ImageView menuderoulant;
        RelativeLayout photo_du_poste;
        SimpleDraweeView photo_du_poste_background;
        ImageView icon_jaime;
        ImageView icon_jaimepas;
        TextView nombre_de_jaime;
        TextView nombre_de_jaimepas;
        LinearLayout like_block;
        LinearLayout dislike_block;
        RelativeLayout block_shimmer;
        ShimmerFrameLayout mShimmerViewContainer;
        RelativeLayout block_globale;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            title1 = itemView.findViewById(R.id.title1);
            contenu =  itemView.findViewById(R.id.contenu);
            commentnumber =  itemView.findViewById(R.id.contenucomment);
            picture =  itemView.findViewById(R.id.icon);
            commenticon =  itemView.findViewById(R.id.commenticon);
            commentblock =  itemView.findViewById(R.id.commentblock);
            menuderoulant =  itemView.findViewById(R.id.menu_deroulant);
            photo_du_poste =  itemView.findViewById(R.id.photo_du_poste);
            photo_du_poste_background = itemView.findViewById(R.id.photo_du_poste_background);
            icon_jaime = itemView.findViewById(R.id.icon_jaime);
            icon_jaimepas = itemView.findViewById(R.id.icon_jaimepas);
            nombre_de_jaime = itemView.findViewById(R.id.nombre_de_jaime);
            nombre_de_jaimepas = itemView.findViewById(R.id.nombre_de_jaimepas);
            like_block = itemView.findViewById(R.id.like_block);
            dislike_block = itemView.findViewById(R.id.dislike_block);
            block_shimmer = itemView.findViewById(R.id.block_shimmer);
            mShimmerViewContainer = itemView.findViewById(R.id.shimmer_view_container);
            block_globale = itemView.findViewById(R.id.block_globale);

        }
    }

    private void ConnexionDeletePhoto(String urldeletephoto, final ConversationPublicItem current)
    {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Suppression en cours...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urldeletephoto,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        data.remove(current);
                        Conversationspublic.allUsersAdapter.notifyDataSetChanged();
                        pDialog.dismiss();
                        Toast.makeText(context,"Suppression effectuee avec succes !",Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error instanceof ServerError)
                        {
                        }else if(error instanceof NetworkError)
                        {
                        }else if(error instanceof AuthFailureError)
                        {
                        }else if(error instanceof ParseError)
                        {
                        }else if(error instanceof NoConnectionError)
                        {
                        }else if(error instanceof TimeoutError)
                        {
                        }else{}
                        pDialog.dismiss();
                        Toast.makeText(context,"Erreur reseau, votre suppression a echouer",Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void ConnexionDeleteMessagePublic(String urldeletemessagepublic)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urldeletemessagepublic,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error instanceof ServerError)
                        {
                        }else if(error instanceof NetworkError)
                        {
                        }else if(error instanceof AuthFailureError)
                        {
                        }else if(error instanceof ParseError)
                        {
                        }else if(error instanceof NoConnectionError)
                        {
                        }else if(error instanceof TimeoutError)
                        {
                        }else{}
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    //This volley method allow us the ability to insert in our database our notification
    public void ConnexionToServer(String url_notification) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_notification,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                        //temp_id_checkmention =
                        try {
                            reponse = new JSONObject(response);
                            temp_id_checkmention = reponse.getInt("succes");
                            //data.get(po)
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
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

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    //This volley request use position because we should give a precision with all
    //mention request
    public void ConnexionToServerWithPosition(String url_notification, final int position) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_notification,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                        //temp_id_checkmention =
                        try {
                            reponse = new JSONObject(response);
                            temp_id_checkmention = reponse.getInt("succes");
                            data.get(position).setId_checkmention(temp_id_checkmention);
                            data.set(position,data.get(position));
                            //data.get(po)
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
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

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void recordNotification(String url_notification) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_notification,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //temp_id_checkmention =
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

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    //this volley request who used to send our pushnotification
    //This volley method allow us the ability to insert in our database our notification
    public void SendPushNotification(String url_pushnotification) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_pushnotification,
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

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


}
