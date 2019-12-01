package com.wazzaby.android.wazzaby.adapter;

import android.content.Context;
import android.content.res.Resources;
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
    private String dark_mode_item = null;
    private Resources res;

    public displaycommentaryadapter(Context context,List<displaycommentary> data)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        database = new DatabaseHandler(context);
        session = new SessionManager(context);
        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));
        res = context.getResources();
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
        holder.title.setTextColor(0xff000000);
        holder.title1.setText(current.getNOM());
        holder.date.setText(current.getDATETIME());
        holder.date.setTextColor(0xff000000);

        if (current.getNOM().equals("Utilisateur Anonyme")) {
            holder.icon.setImageResource(R.drawable.ic_profile_anonymous);
        } else {
            Uri uri = Uri.parse(current.getPHOTO());
            holder.icon.setImageURI(uri);
        }



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

        dark_mode_item = database.getDARKMODE();
        if (dark_mode_item.equals("1"))
        {

            holder.block_shimmer.setBackground(res.getDrawable(R.drawable.background_menu_message_public_mode_dark));
            holder.icononline.setColorFilter(res.getColor(android.R.color.white));
            holder.title.setTextColor(res.getColor(R.color.graycolor));
            holder.title1.setTextColor(res.getColor(R.color.graycolor));
            holder.date.setTextColor(res.getColor(R.color.graycolor));
            holder.global_block.setBackgroundColor(res.getColor(R.color.darkprimary));
            holder.notre_shimmer.setBackground(res.getDrawable(R.drawable.background_menu_message_public_mode_dark));
            holder.imageView.setColorFilter(res.getColor(R.color.darkmorelight));
            holder.icononline.setColorFilter(res.getColor(android.R.color.white));

        } else if (dark_mode_item.equals("0")) {

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
        ImageView imageView;
        TextView date;
        TextView title1;
        RelativeLayout block_shimmer;
        ShimmerFrameLayout mShimmerViewContainer;
        RelativeLayout notre_shimmer;
        RelativeLayout global_block;

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
            global_block = itemView.findViewById(R.id.global_block);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }


}

