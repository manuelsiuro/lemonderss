package com.msa.ui.parser;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class JsonSourcesParser {

    private Context mContext;
    private String json;
    private JSONObject jsonObject;
    private JSONArray items;
    private ArrayList<HashMap<String, String>> itemList;

    public JsonSourcesParser(Context context){
        this.mContext = context;
    }

    public void parseJson(){

        try {

            itemList    = new ArrayList<>();
            json        = loadJSONFromAsset();
            jsonObject  = new JSONObject(json);

            items = jsonObject.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {

                JSONObject item = items.getJSONObject(i);
                String id       = ""+i;//item.getString("id");
                String label    = item.getString("label");
                String url      = item.getString("url");
                String category = item.getString("category");

                HashMap<String, String> feed = new HashMap<>();

                feed.put("id", id);
                feed.put("label", label);
                feed.put("url", url);
                feed.put("category", category);

                itemList.add(feed);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = mContext.getAssets().open("rss_list.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public ArrayList<HashMap<String, String>> getItemList() {
        return itemList;
    }
}
