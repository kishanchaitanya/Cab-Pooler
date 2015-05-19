package com.rookies.driver.cabpooler.util;

import android.util.Log;

import com.rookies.driver.cabpooler.beans.DriverInfo;
import com.rookies.driver.cabpooler.beans.JourneyInfo;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rane on 4/21/15.
 */
public class CabPoolerHttpConnection {

    public String getData(String URL)
    {
        HttpURLConnection conn = null;
        String JSONData = null;
        BufferedReader iReader = null;

        try{
             java.net.URL queryURL = new URL(URL);
             conn = (HttpURLConnection) queryURL.openConnection();
             conn.connect();
            if(null!=
                    conn.getInputStream()) {
             iReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer buff = new StringBuffer();
               String info = "";


                while ((info = iReader.readLine()) != null) {
                    buff.append(info);
                }

                JSONData = buff.toString();
                iReader.close();
            }

        }
        catch(Exception ex)
        {
            Log.d("Exception", ex.toString());
            ex.printStackTrace();
        }
        finally {
            conn.disconnect();
        }
        return JSONData;
    }

    public DriverInfo loginDriver(String uName,String passwd)
    {

        HttpClient conn = new DefaultHttpClient();
        HttpPost postData = new HttpPost(CabPoolerConstants.REST_BASE_URL+"/driver/login");
        HttpResponse response =  null;
        postData.setHeader("content-type", "application/json; charset=UTF-8");
        JSONObject data = new JSONObject();
        InputStream myInfo = null;
        String uInfo = null;
        DriverInfo driverInfo = null;
        try {

            data.put("username",uName);
            data.put("password",passwd);
            postData.setEntity(new StringEntity(data.toString()));

            response = conn.execute(postData);
           myInfo= (response.getEntity().getContent());
            if(myInfo!=null)
            {
                uInfo = decodeMyData(myInfo);
                driverInfo = new CabPoolerJSONParser().parseDriverInfo(uInfo);
            }
            else
            {
                uInfo = "No Data";
            }


            System.out.print("********************"+uInfo);
            Log.d("Hello Data",uInfo);

        }
        catch(Exception ex)
        {
            Log.d("Login Error",ex.getStackTrace().toString());
            ex.printStackTrace();
        }

        return  driverInfo;

    }


    private static String decodeMyData(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String data = "";
        while((line = bufferedReader.readLine()) != null) {
            data += line;
           Log.d("Database",line);
        }
        inputStream.close();
        return data;

    }


    public DriverInfo getDriverInfo(String rideNumber)
    {

        HttpClient conn = new DefaultHttpClient();
        HttpPost postData = new HttpPost(CabPoolerConstants.REST_BASE_URL+"/ride/"+rideNumber+"/status");
        HttpResponse response =  null;
        postData.setHeader("content-type", "application/json; charset=UTF-8");
        InputStream myInfo = null;
        String dInfo = null;
        DriverInfo driverInfo = null;

        try {
             response = conn.execute(postData);
            myInfo= (response.getEntity().getContent());
            if(myInfo!=null)
            {
                dInfo = decodeMyData(myInfo);
                driverInfo = new CabPoolerJSONParser().parseDriverInfo(dInfo);

            }
            else
            {
                dInfo = "No Data";
            }


            System.out.print("********************"+dInfo);
            Log.d("Hello Data",dInfo);

        }
        catch(Exception ex)
        {
            Log.d("Driver Info Error",ex.getStackTrace().toString());
            ex.printStackTrace();
        }
        return driverInfo;


    }




    public void updateCabLocation(String driverNumber,String lat,String lang)
 {

        HttpClient conn = new DefaultHttpClient();
        HttpPost postData = new HttpPost(CabPoolerConstants.REST_BASE_URL+"/cab/"+driverNumber+"/location");
        Log.d("CAB LOCATION UPDATE",CabPoolerConstants.REST_BASE_URL+"/cab/"+driverNumber+"/location++++"+lat+"))))))"+lang);
        HttpResponse response =  null;
        postData.setHeader("content-type", "application/json; charset=UTF-8");
        JSONObject data = new JSONObject();
        InputStream myInfo = null;

        try {

            data.put("latitude",lat);
            data.put("longitude",lang);
            data.put("driverNumber",driverNumber);
            postData.setEntity(new StringEntity(data.toString()));

            response = conn.execute(postData);
        }
        catch(Exception ex)
        {
            Log.d("Cab Location",ex.getStackTrace().toString());
            ex.printStackTrace();
        }



    }




    public JourneyInfo getRideInfo(String rideNumber,String driverNumber,String status)
    {

        HttpClient conn = new DefaultHttpClient();
        HttpPost postData = new HttpPost(CabPoolerConstants.REST_BASE_URL+"/ride/"+rideNumber+"/update");
        Log.d("RIDE INFO URL",CabPoolerConstants.REST_BASE_URL+"/ride/"+rideNumber+"/update");
        HttpResponse response =  null;
        postData.setHeader("content-type", "application/json; charset=UTF-8");
        JSONObject data = new JSONObject();
        InputStream myInfo = null;
        String uInfo = null;
        JourneyInfo journeyInfo = null;
        try {

            data.put("drivernumber",driverNumber);
            data.put("status",status);

            postData.setEntity(new StringEntity(data.toString()));

            response = conn.execute(postData);
            myInfo= (response.getEntity().getContent());
            if(myInfo!=null)
            {

                uInfo = decodeMyData(myInfo);
                Log.d("uINfo",uInfo);
                journeyInfo = new CabPoolerJSONParser().parseRideInfo(uInfo);
            }
            else
            {
                uInfo = "No Data";
            }


            System.out.print("********************"+uInfo);
            Log.d("Hello Data",uInfo);

        }
        catch(Exception ex)
        {
            Log.d("Login Error",ex.getStackTrace().toString());
            ex.printStackTrace();
        }

        return  journeyInfo;

    }


    public String searchRide()
    {

       String data = getData(CabPoolerConstants.REST_BASE_URL+"ride/search");
   //     Log.d("Info from server",data);
        JSONObject jsonObject = null;
        String rideNumber = null;
        try {

            if(data!=null) {
                rideNumber = data;
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
            return  rideNumber;


    }

    public void updateJourneyStatus(String rideNumber,String status)
    {

        HttpClient conn = new DefaultHttpClient();
        HttpPost postData = new HttpPost(CabPoolerConstants.REST_BASE_URL+"journey/"+rideNumber+"/update");
        Log.d("Update cab position",CabPoolerConstants.REST_BASE_URL+"journey/"+rideNumber+"/update");
        HttpResponse response =  null;
        postData.setHeader("content-type", "application/json; charset=UTF-8");
        JSONObject data = new JSONObject();
        InputStream myInfo = null;
        String uInfo = null;
        DriverInfo driverInfo = null;
        try {

            data.put("status",status);
            postData.setEntity(new StringEntity(data.toString()));
            conn.execute(postData);



        }
        catch(Exception ex)
        {
            Log.d("Login Error",ex.getStackTrace().toString());
            ex.printStackTrace();
        }



    }

}
