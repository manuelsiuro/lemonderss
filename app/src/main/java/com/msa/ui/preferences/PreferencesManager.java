package com.msa.ui.preferences;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesManager {

    private final String SETTINGS_WIFI = "settings_wifi";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;


    public PreferencesManager(Context context) {
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        editor = settings.edit();
    }

    public void setSettingsWifi(Boolean b){
        editor.putBoolean(SETTINGS_WIFI, b);
        editor.apply();
    }

    public Boolean getSettingsWifi(){
        return settings.getBoolean(SETTINGS_WIFI, false);
    }
}
