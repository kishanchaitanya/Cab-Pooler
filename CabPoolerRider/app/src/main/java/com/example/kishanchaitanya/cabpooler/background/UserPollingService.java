package com.example.kishanchaitanya.cabpooler.background;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.kishanchaitanya.cabpooler.MainActivity;
import com.example.kishanchaitanya.cabpooler.beans.CabLocationInfo;
import com.example.kishanchaitanya.cabpooler.beans.DriverInfo;
import com.example.kishanchaitanya.cabpooler.beans.JourneyInfo;
import com.example.kishanchaitanya.cabpooler.util.CabPoolerHttpConnection;
import com.google.android.gms.drive.Drive;

/**
 * Created by rane on 4/26/15.
 */
public class UserPollingService extends IntentService {


    public UserPollingService()
    {
        super("UserPollingService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {



       CabPoolerHttpConnection connection = new CabPoolerHttpConnection();
        String rideNumber = intent.getStringExtra("rideNumber");
        JourneyInfo jInfo =(JourneyInfo) connection.getCabStatus(rideNumber);
        CabLocationInfo cabLocation = null;
        if(null!=jInfo) {
            cabLocation = connection.getCabLocation(jInfo.getDriverNumber());
        }
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.PollingServiceReq.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("jInfo",jInfo);
        broadcastIntent.putExtra("cabLocation",cabLocation);
        sendBroadcast(broadcastIntent);
//

    }
}
