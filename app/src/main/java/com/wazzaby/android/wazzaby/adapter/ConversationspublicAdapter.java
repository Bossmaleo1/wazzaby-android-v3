package com.wazzaby.android.wazzaby.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.emoji.widget.EmojiTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.appviews.AfficheCommentairePublic;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.data.ConversationPublicItem;

import java.util.Collections;
import java.util.List;

public class ConversationspublicAdapter  extends RecyclerView.Adapter<ConversationspublicAdapter.MyViewHolder>  implements MenuItem.OnMenuItemClickListener {

    List<ConversationPublicItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public ConversationspublicAdapter(Context context,List<ConversationPublicItem> data)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
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
        return holder;
    }

    @Override
    public void onBindViewHolder(final ConversationspublicAdapter.MyViewHolder holder, int position) {
        final ConversationPublicItem current = data.get(position);
        holder.title.setText(current.getNameMembreProb());
        holder.title1.setText(current.getDatetime());
        holder.contenu.setText(current.getContenu());//current.getContenu()
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
                //context.startActivities(intent);
                //startActivity(intent);
                //Toast.makeText(context,"Voici l'ID de ce comment : "+current.getID(),Toast.LENGTH_LONG).show();
            }
        });

        holder.menuderoulant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu dropDownMenu = new PopupMenu(context, holder.menuderoulant);
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
            //le code xxl pour setter l'image dans le background de mon relativelayout
            /*Glide.with(context).load(current.getStatus_photo()).asBitmap().into(new SimpleTarget<Bitmap>(200, 200) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Drawable drawable = new BitmapDrawable(context.getResources(), resource);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        holder.photo_du_poste.setMinimumWidth(1024);
                        holder.photo_du_poste.setMinimumHeight(768);
                        holder.photo_du_poste.setBackground(drawable);
                    }
                }
            });*/
        }else if(current.getEtat_photo_status().equals("none")) {
            holder.photo_du_poste.setVisibility(View.GONE);
        }
        //holder.icononline.setColorFilter(context.getResources().getColor(current.getColor1()));
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
        }
    }

}
