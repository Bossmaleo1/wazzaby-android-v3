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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.appviews.AfficheCommentairePublic;
import com.wazzaby.android.wazzaby.connInscript.Connexion;
import com.wazzaby.android.wazzaby.fragments.Conversationspublic;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.ConversationPublicItem;

import org.json.JSONArray;
import org.json.JSONException;

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


    public ConversationspublicAdapter(Context context,List<ConversationPublicItem> data)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
        this.session = new SessionManager(context);
        this.database = new DatabaseHandler(context);
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
    public void onBindViewHolder(final ConversationspublicAdapter.MyViewHolder holder, int position) {
        final ConversationPublicItem current = data.get(position);
        res = holder.itemView.getContext().getResources();

        iconjaime = res.getDrawable(R.drawable.baseline_thumb_up_alt_black_24);
        iconjaimepas = res.getDrawable(R.drawable.baseline_thumb_down_alt_black_24);

        //Gestion de la coloration
        if (current.getCheckmention() == 1){
            this.booljaime = true;
            //holder.icon_jaime.setImageTintMode(res.getResourceName(R.color.colorPrimary));
            iconjaime.mutate().setColorFilter(Color.parseColor("#188dc8"), PorterDuff.Mode.SRC_IN);
        } else if (current.getCheckmention() == 2){
            this.booljaimepas = true;
            iconjaimepas.mutate().setColorFilter(Color.parseColor("#188dc8"), PorterDuff.Mode.SRC_IN);
        } else if (current.getCheckmention() == 0){
            this.booljaime = false;
            this.booljaimepas = false;
            //iconjaimepas.mutate().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
            iconjaime.mutate().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
        }




        holder.icon_jaime.setImageDrawable(iconjaime);
        holder.icon_jaimepas.setImageDrawable(iconjaimepas);

        holder.title.setText(current.getNameMembreProb());
        holder.title1.setText(current.getDatetime());
        holder.contenu.setText(current.getContenu());
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
        holder.icon_jaime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*int nombre_de_jaime = current.getCountjaime();
                nombre_de_jaime++;
                holder.nombre_de_jaime.setText(String.valueOf(nombre_de_jaime));*/
                if (current.getCheckmention() == 1) {

                }
            }
        });

        //La gestion du click je n'aime pas
        holder.icon_jaimepas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*int nombre_de_jaimepas = current.getCountjaimepas();
                nombre_de_jaimepas++;
                holder.nombre_de_jaimepas.setText(String.valueOf(nombre_de_jaimepas));*/
            }
        });
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
        TextView contenu;
        TextView commentnumber;
        SimpleDraweeView picture;
        ImageView commenticon;
        LinearLayout commentblock;
        ImageView menuderoulant;
        RelativeLayout photo_du_poste;
        ImageView monbackground;
        SimpleDraweeView photo_du_poste_background;
        ImageView icon_jaime;
        ImageView icon_jaimepas;
        TextView nombre_de_jaime;
        TextView nombre_de_jaimepas;

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

}
