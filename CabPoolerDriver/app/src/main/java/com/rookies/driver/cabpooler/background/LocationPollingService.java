package com.rookies.driver.cabpooler.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rookies.driver.cabpooler.util.CabPoolerHttpConnection;

/**
 * Created by rane on 4/26/15.
 */
public class LocationPollingService extends BroadcastReceiver {

    public static final String ACTION_RESP =
            "com.mamlambo.intent.action.MESSAGE_PROCESSED";

    @Override
    public void onReceive(Context context, Intent intent) {

        CabPoolerHttpConnection connection = new CabPoolerHttpConnection();
        String driverNumber = intent.getStringExtra("dNumber");
        String lat = intent.getStringExtra("lat");
        String lang = intent.getStringExtra("lang");
        connection.updateCabLocation(driverNumber,lat,lang);

    }
}
