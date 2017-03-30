package com.msa.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.msa.ui.R;
import com.msa.ui.interfaces.FragmentCallBack;
import com.msa.ui.preferences.PreferencesManager;

public class SettingsFragment extends Fragment {

    private FragmentCallBack callback;
    private Switch switch_wifi;

    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        callback = (FragmentCallBack)getActivity();

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        switch_wifi = (Switch) rootView.findViewById(R.id.switch_wifi);

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();

        final PreferencesManager prefs = callback.getPrefs();

        if(prefs.getSettingsWifi()){
            switch_wifi.setChecked(true);
        } else {
            switch_wifi.setChecked(false);
        }

        switch_wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    prefs.setSettingsWifi(true);
                }else{
                    prefs.setSettingsWifi(false);
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}