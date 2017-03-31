package com.msa.ui.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.text.Html;
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

import java.util.HashMap;
import java.util.Locale;

public class DetailFragment extends Fragment {

    private TextToSpeech tts;
    private FragmentCallBack callback;
    private ImageView thumbnail;
    private TextView title;
    private TextView datetime;
    private TextView description;
    private TextView txt_open_link;
    private TextView txt_tts_link;

    private RssItem rssItem;

    public DetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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

        if(rssItem.getEnclosure()!=null){
            thumbnail.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(rssItem.getEnclosure()).into(thumbnail);
        } else {
            thumbnail.setVisibility(View.GONE);
        }

        title.setText(rssItem.getTitle());
        datetime.setText(getString(R.string.rss_date_time, rssItem.getStrDate(), rssItem.getStrTime()));
        description.setText(formatDescription(rssItem.getDescription()));

        tts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.FRENCH);
                }
            }
        });

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

    private CharSequence formatDescription(String description){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return formatDescriptionGreater24(description);
        } else {
            return formatDescriptionUnder24(description);
        }
    }

    @SuppressWarnings("deprecation")
    private CharSequence formatDescriptionUnder24(String description){
        return Html.fromHtml(description);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private CharSequence formatDescriptionGreater24(String description){
        return Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT);
    }

    private void speak(String message){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(message);
        } else {
            ttsUnder20(message);
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId = this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("rssItem", rssItem);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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


}