package com.msa.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.msa.ui.MainActivity;
import com.msa.ui.R;
import com.msa.ui.adapters.RssItem;

public class WebViewFragment extends Fragment {

    private static final String WEB_URL     = "www.lemonde.fr";
    private static final String MOBILE_URL  = "mobile.lemonde.fr";
    private WebView mWebView;

    public WebViewFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView     = inflater.inflate(R.layout.fragment_webview, container, false);
        final RssItem rssItem   = ((MainActivity)getActivity()).getRssItem();
        mWebView                = (WebView) rootView.findViewById(R.id.webview);

        String url              = rssItem.getLink();
        String mobileURL        = url.replace(WEB_URL, MOBILE_URL);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.loadUrl(mobileURL);

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mWebView.loadUrl("about:blank");
        mWebView.destroy();
        mWebView = null;
    }
}