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
import com.wazzaby.android.wazzaby.model.data.ConversationItem;

import java.util.Collections;
import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.MyViewHolder> {

    List<ConversationItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public ConversationsAdapter(Context context,List<ConversationItem> data)
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
            Uri uri2 = Uri.parse("https://wazaby939393.000webhostapp.com/Images/" + current.getImageID());
            holder.icon.setImageURI(uri2);
        }else
        {
            holder.icon.setImageResource(R.drawable.ic_profile_colorier);
        }

        holder.icononline.setImageResource(current.getOnlinestatus());
        holder.title1.setText(current.getContenu());
        holder.icononline.setColorFilter(context.getResources().getColor(current.getColor1()));
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

        public MyViewHolder(View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            title1 = itemView.findViewById(R.id.title1);
            icon = itemView.findViewById(R.id.icon);
            icononline = itemView.findViewById(R.id.icononline);
        }
    }

}
