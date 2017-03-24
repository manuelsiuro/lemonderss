package com.msa.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.msa.ui.MainActivity;
import com.msa.ui.R;
import com.msa.ui.adapters.RssItem;

public class DetailFragment extends Fragment {

    private final int FRAGMENT_WEB_VIEW = 2;
    private Context mContext;

    public DetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mContext = getActivity();

        final RssItem rssItem   = ((MainActivity)getActivity()).getRssItem();
        ImageView thumbnail     = (ImageView) rootView.findViewById(R.id.thumbnail);
        TextView title          = (TextView) rootView.findViewById(R.id.title);
        TextView datetime       = (TextView) rootView.findViewById(R.id.datetime);
        TextView description    = (TextView) rootView.findViewById(R.id.description);
        TextView txt_open_link  = (TextView) rootView.findViewById(R.id.txt_open_link);

        Glide.with(getActivity()).load(rssItem.getEnclosure()).into(thumbnail);

        title.setText(rssItem.getTitle());
        datetime.setText(getString(R.string.rss_date_time, rssItem.getStrDate(), rssItem.getStrTime()));
        description.setText(rssItem.getDescription());

        txt_open_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).setRssItem(rssItem);
                ((MainActivity)mContext).displayView(FRAGMENT_WEB_VIEW);
            }
        });

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}