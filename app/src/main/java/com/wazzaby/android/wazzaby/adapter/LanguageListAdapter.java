package com.wazzaby.android.wazzaby.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.Categorie_prob;

import java.util.Collections;
import java.util.List;

public class LanguageListAdapter extends RecyclerView.Adapter<LanguageListAdapter.MyViewHolder>{

    List<Categorie_prob> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private DatabaseHandler database;
    private SessionManager session;
    private String dark_mode_item = null;
    private Resources res;

    public LanguageListAdapter(Context context, List<Categorie_prob> data)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;

        this.session = new SessionManager(context);
        this.database = new DatabaseHandler(context);
        this.res = context.getResources();
    }

    public void delete(int position)
    {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public LanguageListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.categorieprobadapter,parent,false);
        LanguageListAdapter.MyViewHolder holder = new LanguageListAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(LanguageListAdapter.MyViewHolder holder, int position) {
        Categorie_prob current = data.get(position);
        holder.title.setText(current.getLibelle());
        holder.icon.setImageResource(R.drawable.baseline_translate_black_24);
        //holder.icon.setImageResource(R.drawable.ic_subject_black_24dp);
        dark_mode_item = database.getDARKMODE();
        if (dark_mode_item.equals("1"))
        {
            //holder.icon.setColorFilter(res.getColor(R.color.graycolor));
            //holder.title.setTextColor(res.getColor(R.color.graycolor));
            holder.block_globale.setBackground(res.getDrawable(R.drawable.selector_row_dark_mode));
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
        RelativeLayout block_globale;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            icon = itemView.findViewById(R.id.icon);
            block_globale = itemView.findViewById(R.id.block_globale);
        }
    }

}
