package com.msa.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.msa.ui.MainActivityDrawer;
import com.msa.ui.R;
import com.msa.ui.adapters.RssItem;

import java.util.Locale;

public class DetailFragment extends Fragment {

    private final int FRAGMENT_WEB_VIEW = 2;
    private Context mContext;
    private TextToSpeech tts;

    public DetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mContext = getActivity();

        final RssItem rssItem       = ((MainActivityDrawer)getActivity()).getRssItem();
        ImageView thumbnail         = (ImageView) rootView.findViewById(R.id.thumbnail);
        final TextView title        = (TextView) rootView.findViewById(R.id.title);
        TextView datetime           = (TextView) rootView.findViewById(R.id.datetime);
        final TextView description  = (TextView) rootView.findViewById(R.id.description);
        TextView txt_open_link      = (TextView) rootView.findViewById(R.id.txt_open_link);
        TextView txt_tts_link       = (TextView) rootView.findViewById(R.id.txt_tts_link);

        Glide.with(getActivity()).load(rssItem.getEnclosure()).into(thumbnail);

        title.setText(rssItem.getTitle());
        datetime.setText(getString(R.string.rss_date_time, rssItem.getStrDate(), rssItem.getStrTime()));
        description.setText(rssItem.getDescription());

        txt_open_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivityDrawer)mContext).setRssItem(rssItem);
                ((MainActivityDrawer)mContext).loadFragment(FRAGMENT_WEB_VIEW);
            }
        });

        tts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.FRENCH);
                }
            }
        });

        txt_tts_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = title.getText() + ". " + description.getText() + ".";
                tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(tts!=null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }
}