package com.msa.ui.fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.msa.ui.MainActivityDrawer;
import com.msa.ui.R;
import com.msa.ui.adapters.RssItem;

public class WebViewFragment extends Fragment {

    private static final String WEB_URL     = "www.lemonde.fr";
    private static final String MOBILE_URL  = "mobile.lemonde.fr";
    private WebView mWebView;
    private ProgressDialog progressBar;

    public WebViewFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView     = inflater.inflate(R.layout.fragment_webview, container, false);
        final RssItem rssItem   = ((MainActivityDrawer)getActivity()).getRssItem();
        mWebView                = (WebView) rootView.findViewById(R.id.webview);

        progressBar = new ProgressDialog(getActivity());
        progressBar.setMessage(getString(R.string.wait_loading));
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        String url              = rssItem.getLink();
        String mobileURL        = url.replace(WEB_URL, MOBILE_URL);

        mWebView.setVisibility(View.GONE);

        mWebView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int newProgress){
                progressBar.setProgress(newProgress);
                if(newProgress == 100){
                    progressBar.dismiss();
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                progressBar.show();
            }

            @Override
            public void onPageFinished(WebView view, String url){
                if(mWebView!=null){
                    mWebView.setVisibility(View.VISIBLE);
                }
            }
        });

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