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

public class categorieProbAdapter  extends RecyclerView.Adapter<categorieProbAdapter.MyViewHolder>{

    List<Categorie_prob> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private DatabaseHandler database;
    private SessionManager session;
    private String dark_mode_item = null;
    private Resources res;

    public categorieProbAdapter(Context context, List<Categorie_prob> data)
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
    public categorieProbAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.categorieprobadapter,parent,false);
        categorieProbAdapter.MyViewHolder holder = new categorieProbAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(categorieProbAdapter.MyViewHolder holder, int position) {
        Categorie_prob current = data.get(position);
        holder.title.setText(current.getLibelle());
        if (current.getLang().equals("fr")) {
                if(current.getLibelle().equals("Sport")) {
                    holder.icon.setImageResource(R.drawable.baseline_sports_black_24);
                } else if (current.getLibelle().equals("Musique")) {
                    holder.icon.setImageResource(R.drawable.baseline_music_note_black_24);
                } else if (current.getLibelle().equals("Méloncolique")) {
                    holder.icon.setImageResource(R.drawable.baseline_mood_bad_black_24);
                } else if (current.getLibelle().equals("Fête/Céremonie/Evénement")) {
                    holder.icon.setImageResource(R.drawable.ic_event_black_24dp);
                } else if (current.getLibelle().equals("Business")) {
                    holder.icon.setImageResource(R.drawable.baseline_timeline_black_24);
                } else if (current.getLibelle().equals("Football")) {
                    holder.icon.setImageResource(R.drawable.baseline_sports_soccer_black_24);
                } else if (current.getLibelle().equals("Tennis")) {
                    holder.icon.setImageResource(R.drawable.baseline_sports_tennis_black_24);
                } else if (current.getLibelle().equals("Rugby")) {
                    holder.icon.setImageResource(R.drawable.baseline_sports_rugby_black_18);
                } else if (current.getLibelle().equals("Basketball")) {
                    holder.icon.setImageResource(R.drawable.baseline_sports_basketball_black_24);
                } else if (current.getLibelle().equals("Volley")) {
                     holder.icon.setImageResource(R.drawable.baseline_sports_volleyball_black_24);
                } else if (current.getLibelle().equals("MMA")) {
                     holder.icon.setImageResource(R.drawable.baseline_sports_mma_black_24);
                } else if (current.getLibelle().equals("Esport")) {
                    holder.icon.setImageResource(R.drawable.baseline_sports_esports_black_24);
                } else if (current.getLibelle().equals("Baseball")) {
                    holder.icon.setImageResource(R.drawable.ic_subject_black_24dp);
                } else if (current.getLibelle().equals("Judo")) {
                    holder.icon.setImageResource(R.drawable.baseline_sports_kabaddi_black_24);
                } else if (current.getLibelle().equals("Handball")) {
                    holder.icon.setImageResource(R.drawable.baseline_sports_handball_black_24);
                } else if (current.getLibelle().equals("Golf")) {
                    holder.icon.setImageResource(R.drawable.baseline_sports_golf_black_24);
                } else if (current.getLibelle().equals("Football Americain")) {
                     holder.icon.setImageResource(R.drawable.baseline_sports_football_black_24);
                } else if (current.getLibelle().equals("MotorSport")) {
                     holder.icon.setImageResource(R.drawable.baseline_sports_motorsports_black_24);
                } else if (current.getLibelle().equals("Natation")) {
                     holder.icon.setImageResource(R.drawable.baseline_pool_black_24);
                } else if (current.getLibelle().equals("Cyclisme")) {
                    holder.icon.setImageResource(R.drawable.ic_subject_black_24dp);
                } else if (current.getLibelle().equals("Cricket")) {
                    holder.icon.setImageResource(R.drawable.baseline_sports_cricket_black_24);
                }else if (current.getLibelle().equals("Hockey")) {
                    holder.icon.setImageResource(R.drawable.baseline_sports_hockey_black_24);
                }

                else {
                    holder.icon.setImageResource(R.drawable.ic_subject_black_24dp);
                }
        }

        dark_mode_item = database.getDARKMODE();
        if (dark_mode_item.equals("1"))
        {
            //holder.icon.setColorFilter(res.getColor(R.color.graycolor));
            //holder.title.setTextColor(res.getColor(R.color.graycolor));
            holder.block_globale.setBackground(res.getDrawable(R.drawable.selector_row_dark_mode));
        }
        //holder.icon.setImageResource(R.drawable.ic_subject_black_24dp);
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
