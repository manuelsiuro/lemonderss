package com.msa.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.msa.ui.MainActivity;
import com.msa.ui.R;

import java.util.List;

public class RssItemsAdapter extends RecyclerView.Adapter<RssItemsAdapter.MyViewHolder> {

    private Context mContext;
    private List<RssItem> rssItemList;
    private final int FRAGMENT_RSS_DETAIL   = 1;

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title;
        TextView datetime;

        MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            title     = (TextView) view.findViewById(R.id.title);
            datetime  = (TextView) view.findViewById(R.id.datetime);
        }
    }

    public RssItemsAdapter(Context mContext, List<RssItem> rssItemList) {
        this.mContext = mContext;
        this.rssItemList = rssItemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.rss_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final RssItem rssItem = rssItemList.get(position);
        Glide.with(mContext).load(rssItem.getEnclosure()).into(holder.thumbnail);
        holder.title.setText(rssItem.getTitle());
        holder.datetime.setText(mContext.getString(R.string.rss_date_time, rssItem.getStrDate(), rssItem.getStrTime()));

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).setRssItem(rssItem);
                ((MainActivity)mContext).displayView(FRAGMENT_RSS_DETAIL);
            }
        });

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).setRssItem(rssItem);
                ((MainActivity)mContext).displayView(FRAGMENT_RSS_DETAIL);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rssItemList.size();
    }
}
