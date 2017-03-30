package com.msa.ui.fragments;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.msa.ui.Constants;
import com.msa.ui.R;
import com.msa.ui.adapters.RssItem;
import com.msa.ui.interfaces.FragmentCallBack;

import java.util.Locale;

public class DetailFragment extends Fragment implements TextToSpeech.OnInitListener {

    private static final String TAG = "==> DetailFragment : ";
    private TextToSpeech tts;
    private FragmentCallBack callback;
    private ImageView thumbnail;
    private TextView title;
    private TextView datetime;
    private TextView description;
    private TextView txt_open_link;
    private TextView txt_tts_link;

    private RssItem rssItem;

    private Boolean bTts = false;

    public DetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        callback = (FragmentCallBack)getActivity();

        thumbnail           = (ImageView) rootView.findViewById(R.id.thumbnail);
        title               = (TextView) rootView.findViewById(R.id.title);
        datetime            = (TextView) rootView.findViewById(R.id.datetime);
        description         = (TextView) rootView.findViewById(R.id.description);
        txt_open_link       = (TextView) rootView.findViewById(R.id.txt_open_link);
        txt_tts_link        = (TextView) rootView.findViewById(R.id.txt_tts_link);

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i(TAG, "onStart");

        if(rssItem.getEnclosure()!=null){
            Glide.with(getActivity()).load(rssItem.getEnclosure()).into(thumbnail);
        } else {
            thumbnail.setVisibility(View.GONE);
        }

        title.setText(rssItem.getTitle());
        datetime.setText(getString(R.string.rss_date_time, rssItem.getStrDate(), rssItem.getStrTime()));
        description.setText(rssItem.getDescription());

        txt_open_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.setRssItem(rssItem);
                callback.loadFragment(Constants.FRAGMENT.WEB_VIEW);
            }
        });

        txt_tts_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = title.getText() + ". " + description.getText() + ".";
                speak(message);
            }
        });
    }

    private void speak(String message){
        if(bTts){
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
        outState.putSerializable("rssItem", rssItem);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");
        if ((savedInstanceState != null) && (savedInstanceState.getSerializable("rssItem") != null)) {
            rssItem = (RssItem) savedInstanceState.getSerializable("rssItem");
        } else {
            rssItem = callback.getRssItem();
        }
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

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.FRENCH);
            if(result != TextToSpeech.LANG_NOT_SUPPORTED){
                bTts = true;
            }
        }
    }
}