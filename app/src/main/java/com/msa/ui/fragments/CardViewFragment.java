package com.msa.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.msa.ui.MainActivity;
import com.msa.ui.R;
import com.msa.ui.adapters.RssItem;
import com.msa.ui.adapters.RssItemsAdapter;
import com.msa.ui.parser.RssXmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CardViewFragment extends Fragment {

    private static final String URL = "http://www.lemonde.fr/rss/une.xml";
    private RecyclerView recyclerView;
    private RssItemsAdapter adapter;
    private List<RssItem> rssItemList;
    private DownloadXmlTask downloadXmlTask;

    public CardViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cardview, container, false);

        rssItemList  = new ArrayList<>();
        adapter      = new RssItemsAdapter(getActivity(), rssItemList);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        downloadXmlTask = new DownloadXmlTask();

        if(downloadXmlTask.getStatus() == AsyncTask.Status.PENDING ) {
            downloadXmlTask.execute(URL);
        }

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void loadRSS(){

        if(downloadXmlTask.getStatus() != AsyncTask.Status.FINISHED ) {
            rssItemList.clear();
            adapter.notifyDataSetChanged();
            downloadXmlTask.execute(URL);
        } else {
            if(getActivity()!=null){
                ((MainActivity)getActivity()).stopLoaderRefreshLayout();
            }
        }
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
                return getResources().getString(R.string.connection_error);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if(getActivity()!=null){
                ((MainActivity)getActivity()).stopLoaderRefreshLayout();
            }
        }
    }

    private String downloadUrl(String urlString) throws IOException {

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        RssXmlParser rssXmlParser = new RssXmlParser();
                        List<RssXmlParser.Item> items;

                        try {
                            /**
                             * todo: problème encodage ponctuel ??? sur la réponse, le fix ne fonctionne pas toujours @arg#! ;(
                             * */
                            items = rssXmlParser.parse(new String(response.getBytes("ISO-8859-1"), "UTF-8"));
                            //items = rssXmlParser.parse(new String(response.getBytes("UTF-8"), "ISO-8859-1"));

                            for (RssXmlParser.Item entry : items) {

                                String strCurrentDate = entry.pubDate;
                                SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z", Locale.ENGLISH);
                                Date newDate = format.parse(strCurrentDate);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.FRENCH);
                                String strDate = dateFormat.format(newDate);

                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.FRENCH);
                                String strTime = timeFormat.format(newDate);

                                RssItem a = new RssItem(entry.title,
                                        entry.enclosure,
                                        strDate,
                                        strTime,
                                        entry.description,
                                        entry.link);
                                rssItemList.add(a);
                            }

                            adapter.notifyDataSetChanged();

                        } catch (XmlPullParserException | IOException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        /**
                         * todo faire la gestion avec une cardview et un message explicite
                         * */
                    }
                });

        requestQueue.add(stringRequest);

        return "";
    }
}
