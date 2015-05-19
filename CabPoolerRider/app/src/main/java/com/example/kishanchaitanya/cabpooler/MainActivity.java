package com.example.kishanchaitanya.cabpooler;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kishanchaitanya.cabpooler.background.UserPollingService;
import com.example.kishanchaitanya.cabpooler.beans.CabLocationInfo;
import com.example.kishanchaitanya.cabpooler.beans.DriverInfo;
import com.example.kishanchaitanya.cabpooler.beans.JourneyInfo;
import com.example.kishanchaitanya.cabpooler.beans.UserInfo;
import com.example.kishanchaitanya.cabpooler.beans.UserRideInfo;
import com.example.kishanchaitanya.cabpooler.util.CabPoolerHttpConnection;
import com.example.kishanchaitanya.cabpooler.util.CabPoolerJSONParser;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private Toolbar toolbar;
    GPStracker gps;
    GoogleMap mMap;
    double latitude;
    double longitude;
    private LatLng source ;
    private LatLng destination;
    private String sSource;
    private String sDestination;
    private ProgressBar spinner;
    UserInfo uInfo = null;
    private DriverInfo dInfo = null;
    private UserRideInfo rideInfo = null;
    private PollingServiceReq receiver;
    Marker mSource;
    Marker mDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        uInfo = (UserInfo)intent.getSerializableExtra("UserInfo");
        Toast.makeText(getApplicationContext(), "Hi "+uInfo.getUserNumber()+" !!!", Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavigationDrawerFragment drawerFragment;
        drawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,(DrawerLayout)findViewById(R.id.drawer_layout),toolbar,uInfo);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        gps = new GPStracker(this);
        LinearLayout layout = (LinearLayout)findViewById(R.id.driverInfo);
        layout.setVisibility(LinearLayout.INVISIBLE);

        // check if GPS location can get
        if (gps.canGetLocation()) {
            Log.d("Your Location", "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude());

        latitude = gps.getLatitude();
        longitude= gps.getLongitude();

        } else {

            return;
        }

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.homeMapView)).getMap();




        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14));

        final EditText Source = (EditText) findViewById(R.id.Source);
//        source = new LatLng(37.3382088,-121.8863279);
//        destination = new LatLng(37.6176445,-122.3993223);
        Source.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    GeoCode geo = new GeoCode();
                    String URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
                    geo.execute(URL + Source.getText().toString().replaceAll(" ", "+"),"Source");
                    sSource = Source.getText().toString();
                }
            }
        });


        final EditText Destination = (EditText) findViewById(R.id.Destination);
        Destination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    GeoCode geo = new GeoCode();
                    String URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
                    geo.execute(URL + Destination.getText().toString().replaceAll(" ", "+"),"Destination");
                    sDestination = Destination.getText().toString();
                }
            }
        });

        Button btn = (Button) findViewById(R.id.Go);
        btn.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(null!=sSource && null!=sDestination && null!=source && null!=destination) {
                    Directions directions = new Directions();
                    String URL = "https://maps.googleapis.com/maps/api/directions/json?origin=" + source.latitude + "," + source.longitude + "&destination=" + destination.latitude + "," + destination.longitude + "&sensor=false";
//                spinner.setVisibility(View.VISIBLE);
                    directions.execute(URL);
                }
                else
                {
                    Log.d("Reached Toast","Toast");
                    Toast.makeText(getApplicationContext(), "Provide Valid Location Values!!", Toast.LENGTH_LONG).show();
                }

            }
        });

//
//        RelativeLayout  lay = (Fragment)findViewById(R.id.homeMapView);
//        lay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("Reached Touch Areac","Aea");
//                EditText src = (EditText)findViewById(R.id.Source);
//                src.clearFocus();
//                EditText dest = (EditText)findViewById(R.id.Destination);
//                dest.clearFocus();
//            }
//        });

        TextView status = (TextView)findViewById(R.id.RideStatus);
                status.setVisibility(TextView.INVISIBLE);

        IntentFilter filter = new IntentFilter(PollingServiceReq.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new PollingServiceReq();
        registerReceiver(receiver, filter);



    }

    @Override
    protected void onDestroy() {
        Log.v("YourActivity", "onDestory");
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, setting.class));
            return true;
        }

        if(id == R.id.navigate){
            startActivity(new Intent(this, SubActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }



    private class GeoCode extends AsyncTask<String,String,String>
    {

        private Location loc;
        private String place;

        @Override
        protected String doInBackground(String... params) {

            String url = params[0];
            place = params[1];
            String data = null;
            try {
                CabPoolerHttpConnection connection = new CabPoolerHttpConnection();
                Log.d("HTTP Connection",url);
                data = connection.getData(url);
                Log.d("JSON INfo", data);
                JSONObject json = new JSONObject(data);
                CabPoolerJSONParser jParser = new CabPoolerJSONParser();
                loc = jParser.getGeoCode(json);

            }
            catch (Exception e)
            {
                Log.d("Exception in GetLatLong", e.getStackTrace().toString());
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(String result) {


            super.onPostExecute(result);

            if (loc != null) {
                if (place.equalsIgnoreCase("Source")) {
                    source = new LatLng(loc.getLatitude(), loc.getLongitude());
                } else {
                    destination = new LatLng(loc.getLatitude(), loc.getLongitude());
                }
                if (null != loc && 0 != loc.getLatitude() && 0 != loc.getLongitude()) {
                    LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
                    mMap.animateCamera(cameraUpdate);
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    if (place.equalsIgnoreCase("source")) {

                        if(mSource!=null)
                        {
                            mSource.remove();
                        }
                        mSource = mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title(place).icon(BitmapDescriptorFactory.fromResource(R.drawable.source)));
                    }
                    else {
                        if(mDestination!=null)
                        {
                            mDestination.remove();
                        }
                       mDestination= mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title(place).icon(BitmapDescriptorFactory.fromResource(R.drawable.destinaiton)));
                    }
                }
            }
        }
    }



    private class Directions extends AsyncTask<String,String,String>
    {
        String data = null;
        @Override
        protected String doInBackground(String... params) {

            String url = params[0];

            try {
                CabPoolerHttpConnection connection = new CabPoolerHttpConnection();
                Log.d("HTTP Connection",url);
                data = connection.getData(url);
                Log.d("JSON INfo", data);


            }
            catch (Exception e)
            {
                Log.d("Exception in GetLatLong", e.getStackTrace().toString());
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask decode = new ParserTask();
            decode.execute(data);

        }

    }


    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                CabPoolerJSONParser parser = new CabPoolerJSONParser();
                routes = parser.getDirections(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(10);
                polyLineOptions.color(Color.BLUE);
            }
            if(polyLineOptions!=null) {
                mMap.addPolyline(polyLineOptions);
                RideTask ride = new RideTask();
                Log.d("USERNUMBER((((",uInfo.getUserNumber());
                ride.execute(uInfo.getUserNumber(),sSource,sDestination,source.latitude+"",source.longitude+"",destination.latitude+"",destination.longitude+"");
            }
            else
            {
                Toast.makeText(getApplicationContext(), "This Ride Is Beyond  Radius", Toast.LENGTH_LONG).show();
            }
        }
    }


    private class RideTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        UserRideInfo ride = null;

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            ride = new UserRideInfo();
            ride.setUserNumber(jsonData[0]);
            ride.setSource(jsonData[1]);
            ride.setDestination(jsonData[2]);
            ride.setLatitude_source(Double.parseDouble(jsonData[3]));
            ride.setLongitude_source(Double.parseDouble(jsonData[4]));
            ride.setLatitude_destination(Double.parseDouble(jsonData[5]));
            ride.setLongitude_destination(Double.parseDouble(jsonData[6]));
            ride= new CabPoolerHttpConnection().requestCabRide(ride);


            return null;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            rideInfo = ride;
            EditText s = (EditText)findViewById(R.id.Source);
            s.setVisibility(EditText.INVISIBLE);
            EditText d = (EditText)findViewById(R.id.Destination);
            d.setVisibility(EditText.INVISIBLE);
            Button b = (Button)findViewById(R.id.Go);
            b.setVisibility(Button.INVISIBLE);


            Intent pollingIntent = new Intent(MainActivity.this,UserPollingService.class);
            pollingIntent.putExtra("rideNumber", rideInfo.getRideNumber());
            pollingIntent.putExtra("getDriverInfo","Yes");
            startService(pollingIntent);



            TextView status = (TextView) findViewById(R.id.RideStatus);
            status.setText("Searching Cab!!!");
            status.setVisibility(TextView.VISIBLE);


        }

    }




    private Marker cabLocation = null;
    private boolean data = false;
    public class PollingServiceReq extends BroadcastReceiver{

        public static final String PROCESS_RESPONSE = "com.example.kishanchaitanya.intent.action.PROCESS_RESPONSE_REQ";
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean flag=true;



            JourneyInfo jInfo = (JourneyInfo)intent.getSerializableExtra("jInfo");
            TextView status = (TextView) findViewById(R.id.RideStatus);
            if(null!=jInfo && null!=jInfo.getStatus()) {
                if(!jInfo.getStatus().equalsIgnoreCase("null") && !jInfo.getStatus().equalsIgnoreCase("3"))
                status.setText(jInfo.getStatus());

                LinearLayout layout = (LinearLayout) findViewById(R.id.driverInfo);

                if(jInfo.getStatus().equalsIgnoreCase("Reached Destination"))
                {
                    mMap.clear();
                    status.setVisibility(TextView.INVISIBLE);
                    flag = false;
                    layout.setVisibility(LinearLayout.INVISIBLE);
                    EditText s = (EditText)findViewById(R.id.Source);
                    s.setVisibility(EditText.VISIBLE);
                    EditText d = (EditText)findViewById(R.id.Destination);
                    d.setVisibility(EditText.VISIBLE);
                    Button b = (Button)findViewById(R.id.Go);
                    b.setVisibility(Button.VISIBLE);
                }
            }

            CabLocationInfo cabLocationInfo = (CabLocationInfo) intent.getSerializableExtra("cabLocation");

            if(null!=cabLocation) {
                cabLocation.remove();
            }


            if(null != jInfo && null != jInfo.getStatus() && jInfo.getStatus().equalsIgnoreCase("Cab is on the way for pickup!!"))
            {

                LinearLayout layout = (LinearLayout) findViewById(R.id.driverInfo);
                layout.setVisibility(LinearLayout.VISIBLE);

            }

            if(flag) {

                cabLocation = mMap.addMarker(new MarkerOptions().position(new LatLng(cabLocationInfo.getCabLatitude(), cabLocationInfo.getCabLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.cab)));


                Intent pollingIntent = new Intent(MainActivity.this, UserPollingService.class);
                pollingIntent.putExtra("rideNumber", rideInfo.getRideNumber());

                startService(pollingIntent);
            }
        }
    }

}
