package com.msa.ui.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.msa.ui.events.ChargingEvent;

import org.greenrobot.eventbus.EventBus;

public class ChargingReceiver extends BroadcastReceiver {

    private EventBus bus = EventBus.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {

        ChargingEvent event = null;

        String eventData = "This device started ";

        if(intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)){
            event = new ChargingEvent(eventData + "charging.");
        } else if(intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)){
            event = new ChargingEvent(eventData + "discharging.");
        }

        // Post the event
        bus.post(event);
    }
}
