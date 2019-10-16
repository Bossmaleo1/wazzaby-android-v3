package com.wazzaby.android.wazzaby.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.Profil;
import com.wazzaby.android.wazzaby.model.data.displaycommentary;

import java.util.Collections;
import java.util.List;

public class displaycommentaryadapter extends RecyclerView.Adapter<displaycommentaryadapter.MyViewHolder> {

    List<displaycommentary> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private DatabaseHandler database;
    private SessionManager session;
    private Profil user;

    public displaycommentaryadapter(Context context,List<displaycommentary> data)
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
    public displaycommentaryadapter .MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.adapterdisplaycommentary,parent,false);
        displaycommentaryadapter.MyViewHolder holder = new displaycommentaryadapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(displaycommentaryadapter.MyViewHolder holder, int position) {
        displaycommentary current = data.get(position);
        holder.title.setText(current.getTitle());
        holder.title1.setText(current.getNOM());
        holder.date.setText(current.getDATETIME());
        Uri uri = Uri.parse(current.getPHOTO());
        holder.icon.setImageURI(uri);

        holder.icononline.setImageResource(current.getIcononline());
        holder.icononline.setColorFilter(context.getResources().getColor(current.getColor1()));

        if (data.get(position).getState_shimmer() == 1) {
            holder.block_shimmer.setVisibility(View.VISIBLE);
            holder.mShimmerViewContainer.setVisibility(View.VISIBLE);
            holder.mShimmerViewContainer.startShimmer();
        } else if (data.get(position).getState_shimmer() == 0) {
            holder.block_shimmer.setVisibility(View.VISIBLE);
            holder.notre_shimmer.setVisibility(View.GONE);
            holder.mShimmerViewContainer.setVisibility(View.GONE);
        }
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
        TextView date;
        TextView title1;
        RelativeLayout block_shimmer;
        ShimmerFrameLayout mShimmerViewContainer;
        RelativeLayout notre_shimmer;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            title1 = itemView.findViewById(R.id.title1);
            icon = itemView.findViewById(R.id.icon);
            icononline = itemView.findViewById(R.id.icononline);
            date =  itemView.findViewById(R.id.date);
            block_shimmer = itemView.findViewById(R.id.block_shimmer);
            mShimmerViewContainer = itemView.findViewById(R.id.shimmer_view_container);
            notre_shimmer = itemView.findViewById(R.id.notre_shimmer);
        }
    }


}

