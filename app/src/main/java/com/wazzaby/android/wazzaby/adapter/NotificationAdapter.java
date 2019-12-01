package com.wazzaby.android.wazzaby.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.NotificationItem;

import java.util.Collections;
import java.util.List;

public class NotificationAdapter  extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    List<NotificationItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private SessionManager session;
    private DatabaseHandler database;
    private String dark_mode_item = null;
    private Resources res;

    public NotificationAdapter(Context context,List<NotificationItem> data)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        database = new DatabaseHandler(context);
        session = new SessionManager(context);
        res = context.getResources();
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


        if(current.getPhoto().equals("http://wazzaby.com/uploads/photo_de_profil/")) {
            holder.photo_user.setImageResource(R.drawable.ic_profile_colorier);
        }else
        {
            Uri uri2 = Uri.parse(current.getPhoto());
            holder.photo_user.setImageURI(uri2);
        }

        if (current.getEtat()==0) {
            holder.icononline.setImageResource(R.drawable.baseline_done_black_24);
        } else {
            holder.icononline.setImageResource(R.drawable.baseline_done_all_black_24);
        }

        holder.message.setText(current.getLibelle());
        holder.updated.setText(current.getUpdated());

        if (data.get(position).getState_shimmer() == 1) {
            holder.block_shimmer.setVisibility(View.VISIBLE);
            holder.mShimmerViewContainer.setVisibility(View.VISIBLE);
            holder.mShimmerViewContainer.startShimmer();
        } else if (data.get(position).getState_shimmer() == 0) {
            holder.block_shimmer.setVisibility(View.VISIBLE);
            holder.mShimmerViewContainer.setVisibility(View.GONE);
        }

        dark_mode_item = database.getDARKMODE();
        if (dark_mode_item.equals("1"))
        {
            holder.block_globale_notification.setBackgroundColor(res.getColor(R.color.darkprimary));
            holder.block_shimmer.setBackground(res.getDrawable(R.drawable.background_menu_message_public_mode_dark));
            holder.name_user.setTextColor(res.getColor(R.color.graycolor));
            holder.updated.setTextColor(res.getColor(R.color.graycolor));
            holder.message.setTextColor(res.getColor(R.color.graycolor));
            holder.icononline.setColorFilter(res.getColor(android.R.color.white));
            holder.block_shimmer_layout.setBackground(res.getDrawable(R.drawable.background_menu_message_public_mode_dark));
            holder.notification_main_shimmer.setBackgroundColor(res.getColor(R.color.darkprimary));
            //holder.block_globale_notification.setBackgroundColor(res.getColor(R.color.darkprimarydark));
            //holder.user_first_name.setBackgroundColor(res.getColor(R.color.darkprimarydark));
        } else if (dark_mode_item.equals("0")) {

        }
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
        RelativeLayout block_shimmer;
        ShimmerFrameLayout mShimmerViewContainer;
        RelativeLayout block_globale_notification;
        CardView user_first_name;
        RelativeLayout notification_main_shimmer;
        RelativeLayout block_shimmer_layout;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            name_user = itemView.findViewById(R.id.name_user);
            message = itemView.findViewById(R.id.message);
            photo_user = itemView.findViewById(R.id.icon);
            updated = itemView.findViewById(R.id.updated);
            icononline = itemView.findViewById(R.id.icononline);
            block_shimmer = itemView.findViewById(R.id.block_shimmer);
            mShimmerViewContainer = itemView.findViewById(R.id.shimmer_view_container);
            block_globale_notification = itemView.findViewById(R.id.block_globale_notification);
            user_first_name = itemView.findViewById(R.id.user_first_name);
            notification_main_shimmer = itemView.findViewById(R.id.notification_main_shimmer);
            block_shimmer_layout = itemView.findViewById(R.id.block_shimmer_layout);
        }
    }

}
