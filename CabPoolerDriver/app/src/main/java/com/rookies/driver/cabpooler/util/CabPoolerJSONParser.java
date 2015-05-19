package com.rookies.driver.cabpooler.util;

import android.location.Location;
import android.util.Log;

import com.rookies.driver.cabpooler.beans.DriverInfo;
import com.rookies.driver.cabpooler.beans.JourneyInfo;
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



    public DriverInfo parseDriverInfo(String data)
    {
        DriverInfo dInfo = null;
        JSONObject jsonObject =null;
        try
        {
            dInfo = new DriverInfo();
            jsonObject = new JSONObject(data);
            dInfo.setDriverNumber((String) jsonObject.get("driverNumber"));
            dInfo.setUserName((String)jsonObject.get("userName"));
            dInfo.setPassword((String)jsonObject.get("password"));
            dInfo.setContactNumber((String)jsonObject.get("contactNumber"));
            dInfo.setCarName((String) jsonObject.get("carName"));
            dInfo.setCarNumber((String) jsonObject.get("carNumber"));
            dInfo.setLanguage((String) jsonObject.get("language"));
            dInfo.setHobbies((String) jsonObject.get("hobbies"));
            dInfo.setName((String)jsonObject.get("name"));
        }catch (Exception ex)
           {
               Log.d("JSON Parser Exception",ex.getStackTrace().toString());
               ex.printStackTrace();
               dInfo = null;

           }

        return  dInfo;

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


    public JourneyInfo parseRideInfo(String jsonData)
    {
        JourneyInfo jInfo = null;
        JSONObject jsonObject =null;
        try
        {
            jInfo = new JourneyInfo();
            Log.d("Parse Ride Info",jsonData);
            jsonObject = new JSONObject(jsonData);
            jInfo.setJourneyNumber(jsonObject.getString("journeyNumber"));
            jInfo.setRideNumber(jsonObject.getString("rideNumber"));
            jInfo.setDriverNumber(jsonObject.getString("driverNumber"));
            jInfo.setUserNumber(jsonObject.getString("userNumber"));
            jInfo.setSource(jsonObject.getString("source"));
            jInfo.setDestination(jsonObject.getString("destination"));
            jInfo.setDistance(jsonObject.getString("distance"));
            jInfo.setCost(jsonObject.getString("cost"));
            jInfo.setStatus(jsonObject.getString("status"));
            jInfo.setLatitude_source(jsonObject.getDouble("latitude_source"));
            jInfo.setLongitude_source(jsonObject.getDouble("longitude_source"));
            jInfo.setLatitude_destination(jsonObject.getDouble("latitude_destination"));
            jInfo.setLongitude_destination(jsonObject.getDouble("longitude_destination"));
        }catch (Exception ex)
        {
            Log.d("JSON Parser Exception",ex.getStackTrace().toString());
            ex.printStackTrace();
            jInfo = null;

        }

        return  jInfo;

    }
}
