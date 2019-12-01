package com.wazzaby.android.wazzaby.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.DrawerItem;

import java.util.Collections;
import java.util.List;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {

    List<DrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private DatabaseHandler database;
    private SessionManager session;
    private String dark_mode_item = null;
    private Resources res;

    public NavigationDrawerAdapter(Context context,List<DrawerItem> data)
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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.drawer_row,parent,false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());
        holder.icon.setImageResource(current.getImageID());

        dark_mode_item = database.getDARKMODE();
        if (dark_mode_item.equals("1"))
        {
            holder.block_fragment_drawer.setBackground(res.getDrawable(R.drawable.selector_row_dark_mode_drawer));
            //holder.title.setTextColor(res.getColor(android.R.color.white));
            //holder.icon.setColorFilter(res.getColor(android.R.color.white));
        }else if (dark_mode_item.equals("0")) {

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        ImageView icon;
        CardView user_first_name;
        RelativeLayout block_fragment_drawer;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            icon = itemView.findViewById(R.id.icon);
            user_first_name = itemView.findViewById(R.id.user_first_name);
            block_fragment_drawer = itemView.findViewById(R.id.block_fragment_drawer);
        }
    }
}
