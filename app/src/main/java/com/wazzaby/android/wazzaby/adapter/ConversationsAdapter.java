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
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.ConversationItem;

import java.util.Collections;
import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.MyViewHolder> {

    List<ConversationItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private String dark_mode_item = null;
    private DatabaseHandler database;
    private SessionManager session;
    private Resources res;

    public ConversationsAdapter(Context context,List<ConversationItem> data)
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
    public ConversationsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.adapterlastconversations,parent,false);
        ConversationsAdapter.MyViewHolder holder = new ConversationsAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ConversationsAdapter.MyViewHolder holder, int position) {
        ConversationItem current = data.get(position);
        holder.title.setText(current.getFriendLibelle());
        if(!current.getImageID().equals("null")) {
            Uri uri2 = Uri.parse(Const.dns+"/uploads/photo_de_profil/" + current.getImageID());
            holder.icon.setImageURI(uri2);
        }else
        {
            holder.icon.setImageResource(R.drawable.ic_profile_colorier);
        }

        holder.icononline.setImageResource(current.getOnlinestatus());
        holder.title1.setText(current.getContenu());
        holder.icononline.setColorFilter(context.getResources().getColor(current.getColor1()));

        dark_mode_item = database.getDARKMODE();
        if (dark_mode_item.equals("1"))
        {
            //holder.main_block.setBackgroundColor(res.getColor(R.color.darkprimary));
            //holder.second_block_main.setBackground(res.getDrawable(R.drawable.selector_row_dark_mode));
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
        TextView title1;
        SimpleDraweeView icon;
        ImageView icononline;
        RelativeLayout main_block;
        RelativeLayout second_block_main;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            title1 = itemView.findViewById(R.id.title1);
            icon = itemView.findViewById(R.id.icon);
            icononline = itemView.findViewById(R.id.icononline);
            main_block = itemView.findViewById(R.id.main_block);
            second_block_main = itemView.findViewById(R.id.main_block);
        }
    }

}
