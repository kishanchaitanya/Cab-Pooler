package com.rookies.driver.cabpooler.background;

import android.app.IntentService;
import android.content.Intent;

import com.rookies.driver.cabpooler.MainActivity;
import com.rookies.driver.cabpooler.util.CabPoolerHttpConnection;

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
        String flag = intent.getStringExtra("searchRide");

        String rideNumber = null;

           if(flag.equalsIgnoreCase("yes"))
           {
                rideNumber=connection.searchRide();
           }


        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra("rideNumber",rideNumber);
        broadcastIntent.setAction(MainActivity.PollingService.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(broadcastIntent);

    }
}
