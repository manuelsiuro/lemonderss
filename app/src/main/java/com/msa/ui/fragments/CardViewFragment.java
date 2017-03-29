package com.msa.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.msa.ui.Constants;
import com.msa.ui.R;
import com.msa.ui.adapters.RssItem;
import com.msa.ui.adapters.RssItemsAdapter;
import com.msa.ui.parser.RssXmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class CardViewFragment extends Fragment {

    private String rssURL;
    private RecyclerView recyclerView;
    private RssItemsAdapter adapter;
    private List<RssItem> rssItemList;
    private SwipeRefreshLayout swipeRefreshLayout;

    public CardViewFragment() {
    }

    public String getRssURL() {
        return rssURL;
    }

    public void setRssURL(String rssURL) {
        this.rssURL = rssURL;
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
        swipeRefreshLayout  = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        String rssURL = this.getArguments().getString(Constants.BUNDLE.RSS_URL);
        setRssURL(rssURL);
        httpRequest(rssURL);

        initSwipeRefreshLayout();

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initSwipeRefreshLayout(){

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        rssItemList.clear();
                        adapter.notifyDataSetChanged();
                        httpRequest(getRssURL());
                    }
                }
        );
    }

    public void stopLoaderRefreshLayout(){
        swipeRefreshLayout.setRefreshing(false);
    }

    public void httpRequest(String urlString) {

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest request = new StringRequest(Request.Method.GET, urlString,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        RssXmlParser rssXmlParser = new RssXmlParser();
                        List<RssXmlParser.Item> items;

                        try {

                            items = rssXmlParser.parse(response);

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
                            stopLoaderRefreshLayout();

                        } catch (XmlPullParserException | IOException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        /**
                         * 404 not found for fun
                         * To try modify final URL ;)
                         * */

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.FRENCH);
                        String strDate = dateFormat.format(new Date());

                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.FRENCH);
                        String strTime = timeFormat.format(new Date());

                        RssItem a = new RssItem(
                                getString(R.string.page_not_found),
                                "http://www.geeksleague.be/wp-content/uploads/2013/11/exemple-page-erreur-404-620x320.png",
                                strDate,
                                strTime,
                                getString(R.string.page_not_found_desc),
                                "https://s3.amazonaws.com/wp-ag/wp-content/uploads/sites/72/2015/05/chuck-norris-approves.gif");

                        rssItemList.add(a);
                        adapter.notifyDataSetChanged();
                        stopLoaderRefreshLayout();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/rss+xml; charset=utf-8");
                params.put("Content-Type", "text/xml;charset=UTF-8");
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                System.out.println(response.headers);

                try {
                    System.out.println(response.headers.toString().toLowerCase().indexOf("utf-8"));
                    //System.out.println(new String( response.data));
                    System.out.println(response.statusCode);


                    String stringRequest = new String( response.data, HttpHeaderParser.parseCharset(response.headers));

                    if(response.headers.toString().toLowerCase().indexOf("utf-8") <= 0){

                        return Response.success(new String(response.data, "UTF-8"), HttpHeaderParser.parseCacheHeaders(response));
                    } else {
                        return Response.success(stringRequest, HttpHeaderParser.parseCacheHeaders(response));
                    }

                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (Exception je) {
                    return Response.error(new ParseError(je));
                }
            }
        };

        queue.add(request);
        request.setShouldCache(false);
    }
}
