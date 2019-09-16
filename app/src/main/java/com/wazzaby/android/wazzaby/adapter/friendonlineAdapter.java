package com.wazzaby.android.wazzaby.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.Profil;
import com.wazzaby.android.wazzaby.model.data.friendProbItem;

import java.util.Collections;
import java.util.List;

public class friendonlineAdapter extends RecyclerView.Adapter<friendonlineAdapter.MyViewHolder> {

    List<friendProbItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private DatabaseHandler database;
    private SessionManager session;
    private Profil user;

    public friendonlineAdapter(Context context, List<friendProbItem> data)
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
    public friendonlineAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.adapter_friendonline,parent,false);
        friendonlineAdapter.MyViewHolder holder = new friendonlineAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(friendonlineAdapter.MyViewHolder holder, int position) {
        friendProbItem current = data.get(position);
        holder.title.setText(current.getFriendLibelle());
        if(current.getImageID().length()>0) {
            Uri uri = Uri.parse(Const.dns+"/uploads/photo_de_profil/" +current.getImageID());
            holder.icon.setImageURI(uri);
        }else
        {
            holder.icon.setImageResource(R.drawable.ic_profile_colorier);
        }
        holder.icononline.setImageResource(current.getOnlinestatus());
        holder.icononline.setColorFilter(context.getResources().getColor(current.getColor1()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        SimpleDraweeView icon;
        ImageView icononline;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            icon = itemView.findViewById(R.id.icon);
            icononline = itemView.findViewById(R.id.icononline);
        }
    }
}
