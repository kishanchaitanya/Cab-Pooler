package com.example.kishanchaitanya.cabpooler.util;

import android.content.Entity;
import android.util.Log;

import com.example.kishanchaitanya.cabpooler.beans.CabLocationInfo;
import com.example.kishanchaitanya.cabpooler.beans.DriverInfo;
import com.example.kishanchaitanya.cabpooler.beans.JourneyInfo;
import com.example.kishanchaitanya.cabpooler.beans.UserInfo;
import com.example.kishanchaitanya.cabpooler.beans.UserRideInfo;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by rane on 4/21/15.
 */
public class CabPoolerHttpConnection {

    public String getData(String URL)
    {
        HttpURLConnection conn = null;
        String JSONData = "";
        BufferedReader iReader = null;

        try{
             java.net.URL queryURL = new URL(URL);
             conn = (HttpURLConnection) queryURL.openConnection();
             conn.connect();
              if(null!=conn && null!=conn.getInputStream()) {
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

        }
        finally {
            conn.disconnect();
        }
        return JSONData;
    }

    public UserInfo loginUser(String uName,String passwd)
    {

        HttpClient conn = new DefaultHttpClient();
        HttpPost postData = new HttpPost(CabPoolerConstants.REST_BASE_URL+"/user/login");
        HttpResponse response =  null;
        postData.setHeader("content-type", "application/json; charset=UTF-8");
        JSONObject data = new JSONObject();
        InputStream myInfo = null;
        String uInfo = null;
        UserInfo user = null;
        try {

            data.put("username",uName);
            data.put("password",passwd);
            postData.setEntity(new StringEntity(data.toString()));

            response = conn.execute(postData);
           myInfo= (response.getEntity().getContent());
            if(myInfo!=null)
            {
                uInfo = decodeMyData(myInfo);
                user = new CabPoolerJSONParser().parseUserInfo(uInfo);
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

        return  user;

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


    public UserRideInfo requestCabRide(UserRideInfo rideInfo)
    {
        {


            HttpClient conn = new DefaultHttpClient();
            HttpPost postData = new HttpPost(CabPoolerConstants.REST_BASE_URL+"/ride/request");
            HttpResponse response =  null;
            postData.setHeader("content-type", "application/json; charset=UTF-8");
            JSONObject parent = new JSONObject();
            InputStream myInfo = null;
            String uInfo = null;
            UserInfo user = null;
            try {

                JSONObject child = new JSONObject();
                child.put("source", rideInfo.getSource());
                child.put("destination", rideInfo.getDestination());
                child.put("latitude_source", rideInfo.getLatitude_source());
                child.put("longitude_source", rideInfo.getLongitude_source());
                child.put("latitude_destination", rideInfo.getLatitude_destination());
                child.put("longitude_destination", rideInfo.getLongitude_destination());
                child.put("status", "0");
                Calendar calendar = Calendar.getInstance();
                java.util.Date now = calendar.getTime();
                java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
                child.put("requestTime",null);
                child.put("userNumber", rideInfo.getUserNumber());
                parent.put("request",child);
                Log.d("JSON INFO",child.toString());
                postData.setEntity(new StringEntity(child.toString()));

                response = conn.execute(postData);
                myInfo= (response.getEntity().getContent());
                if(myInfo!=null)
                {
                    uInfo = decodeMyData(myInfo);
                    JSONObject jsonObject = new JSONObject(uInfo);
                    rideInfo.setRideNumber((String)jsonObject.get("rideNumber"));
                    //user = new CabPoolerJSONParser().parseUserInfo(uInfo);
                }
                else
                {
                    uInfo = "No Data";
                }


                System.out.print("********************"+uInfo);
                Log.d("Request Ride",uInfo);

            }
            catch(Exception ex)
            {
                Log.d("Request Ride Error",ex.getStackTrace().toString());
                ex.printStackTrace();
            }

            return rideInfo;

        }
    }

    public DriverInfo getDriverInfo(String rideNumber)
    {

        DriverInfo driverInfo = null;
        String jsonData = null;
        JSONObject json=null;
        int counter = 1;
        boolean flag = true;
        try{
            while(flag ) {
                jsonData = getData(CabPoolerConstants.REST_BASE_URL + "ride/" + rideNumber + "/status");
                json = new JSONObject(jsonData);
                if(json.getString("name")!=null)
                {
                    flag = false;
                }
                Log.d("JSON DATA DRIVER INFO",counter+"");
                counter++;
            }
            if(jsonData!=null) {
                driverInfo = new CabPoolerJSONParser().parseDriverInfo(jsonData);
            }


        }
        catch (Exception ex)
        {
            ex.getStackTrace();
        }

        return driverInfo;


    }

    public CabLocationInfo getCabLocation(String driverNumber)
    {

        CabLocationInfo cabLocationInfo = null;
        String jsonData = null;
        try{
            jsonData = getData(CabPoolerConstants.REST_BASE_URL+"/cab/"+driverNumber+"/locationupdate");
            Log.d("cab location",CabPoolerConstants.REST_BASE_URL+"/cab/"+driverNumber+"/locationupdate");
            if(jsonData!=null) {
                cabLocationInfo = new CabPoolerJSONParser().parseCabLocation(jsonData);
            }

        }
        catch (Exception ex)
        {
            ex.getStackTrace();
        }

        return cabLocationInfo;
   }


    public JourneyInfo getCabStatus(String rideNumber)
    {

        String status = null;
        String jsonData = null;
        JourneyInfo journeyInfo = null;
        try{
            jsonData = getData(CabPoolerConstants.REST_BASE_URL + "ride/" + rideNumber + "/status");
            if(jsonData!=null) {
                journeyInfo = new CabPoolerJSONParser().parseJourneyInfo(jsonData);
            }


        }
        catch (Exception ex)
        {
            ex.getStackTrace();
        }

        return journeyInfo;



    }


}
