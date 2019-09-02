package com.wazzaby.android.wazzaby.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.data.NotificationItem;

import java.util.Collections;
import java.util.List;

public class NotificationAdapter  extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    List<NotificationItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public NotificationAdapter(Context context,List<NotificationItem> data)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void delete(int position)
    {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.notificationadapter,parent,false);
        NotificationAdapter.MyViewHolder holder = new NotificationAdapter.MyViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(NotificationAdapter.MyViewHolder holder, int position) {
        NotificationItem current = data.get(position);
        holder.name_user.setText(current.getPrenom()+" "+current.getNom());


        //Const.dns+"/uploads/photo_de_profil/"+current.getPhoto()
        if(!current.getPhoto().equals("null")) {
            Uri uri2 = Uri.parse(current.getPhoto());
            holder.photo_user.setImageURI(uri2);
        }else
        {
            holder.photo_user.setImageResource(R.drawable.ic_profile_colorier);
        }

        if (current.getEtat()==0) {
            holder.icononline.setImageResource(R.drawable.baseline_done_black_24);
        } else {
            holder.icononline.setImageResource(R.drawable.baseline_done_all_black_24);
        }

        holder.message.setText(current.getLibelle());
        holder.updated.setText(current.getUpdated());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView name_user;
        TextView message;
        TextView updated;
        SimpleDraweeView photo_user;
        ImageView icononline;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            name_user = itemView.findViewById(R.id.name_user);
            message = itemView.findViewById(R.id.message);
            photo_user = itemView.findViewById(R.id.icon);
            updated = itemView.findViewById(R.id.updated);
            icononline = itemView.findViewById(R.id.icononline);
        }
    }

}
