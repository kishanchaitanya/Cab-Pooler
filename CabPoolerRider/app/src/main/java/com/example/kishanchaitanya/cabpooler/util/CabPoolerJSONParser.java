package com.example.kishanchaitanya.cabpooler.util;

import android.location.Location;
import android.util.Log;

import com.example.kishanchaitanya.cabpooler.beans.CabLocationInfo;
import com.example.kishanchaitanya.cabpooler.beans.DriverInfo;
import com.example.kishanchaitanya.cabpooler.beans.JourneyInfo;
import com.example.kishanchaitanya.cabpooler.beans.UserInfo;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rane on 4/25/15.
 */
public class CabPoolerJSONParser {



    public UserInfo parseUserInfo(String data)
    {
        UserInfo uInfo = null;
        JSONObject jsonObject =null;
        try
        {
            uInfo = new UserInfo();
            jsonObject = new JSONObject(data);
            uInfo.setUserNumber((String) jsonObject.get("userNumber"));
            uInfo.setUserName((String)jsonObject.get("userName"));
            uInfo.setPassword((String)jsonObject.get("password"));
            uInfo.setContactNumber((String)jsonObject.get("contactNumber"));
            uInfo.setLanguage((String)jsonObject.get("language"));
            uInfo.setHobbies((String)jsonObject.get("hobbies"));
            uInfo.setCreditCardNumber((String)jsonObject.get("creditCardNumber"));
            uInfo.setName((String)jsonObject.get("name"));
        }catch (Exception ex)
           {
               Log.d("JSON Parser Exception",ex.getStackTrace().toString());
               ex.printStackTrace();
               uInfo = null;

           }

        return  uInfo;

    }

    public Location getGeoCode(JSONObject jsonData)
    {
        Location loc = null;
        JSONArray results = null;
        JSONObject geometry = null;
        JSONObject location = null;

        try{
            results = jsonData.getJSONArray("results");
            geometry  = results.getJSONObject(0).getJSONObject("geometry");
            location= geometry.getJSONObject("location");
            loc = new Location("");
            loc.setLatitude(Double.parseDouble(location.getString("lat")));
            loc.setLongitude(Double.parseDouble(location.getString("lng")));
        }
        catch(Exception ex)
        {
            Log.d("Error in parsing",ex.getStackTrace().toString());
            ex.printStackTrace();
        }

        return  loc;
    }

    public List<List<HashMap<String, String>>> getDirections(JSONObject jObject) {
        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        try {
            jRoutes = jObject.getJSONArray("routes");
            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps
                                .get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat",
                                    Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lng",
                                    Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return routes;
    }


    private List<LatLng> decodePoly (String polyline)
    {
        List<LatLng> points = new ArrayList<LatLng>();

        int index = 0, len = polyline.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = polyline.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = polyline.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            points.add(p);
        }
        return points;
    }

    public DriverInfo parseDriverInfo(String dInfo)
    {
        JSONObject jsonObject = null;
        DriverInfo driverInfo = new DriverInfo();
        try {
            jsonObject = new JSONObject(dInfo);
            driverInfo.setDriverNumber(jsonObject.getString("driverNumber"));
            driverInfo.setName(jsonObject.getString("name"));
            driverInfo.setCarName(jsonObject.getString("carName"));
            driverInfo.setCarNumber(jsonObject.getString("carNumber"));
            driverInfo.setContactNumber(jsonObject.getString("contactNumber"));
            driverInfo.setLanguage(jsonObject.getString("language"));
            driverInfo.setHobbies(jsonObject.getString("hobbies"));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return driverInfo;

    }

    public JourneyInfo parseJourneyInfo(String dInfo)
    {
        JSONObject jsonObject = null;
        JourneyInfo journeyInfo = new JourneyInfo();
        try {
            jsonObject = new JSONObject(dInfo);
            journeyInfo.setDriverNumber(jsonObject.getString("driverNumber"));
            journeyInfo.setStatus(jsonObject.getString("status"));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return journeyInfo;

    }


    public CabLocationInfo parseCabLocation(String cabLocation)
    {
        JSONObject jsonObject = null;
        CabLocationInfo cabLocationInfo = new CabLocationInfo();
        try {

            if(cabLocation!=null)
            {
                jsonObject = new JSONObject(cabLocation);
                cabLocationInfo.setCabLatitude(Double.parseDouble(jsonObject.getString("cabLatitude")));
                cabLocationInfo.setCabLongitude(Double.parseDouble(jsonObject.getString("cabLongitude")));
            }
        }
        catch (Exception ex)
        {

        }

        return cabLocationInfo;
    }

}
