package com.rookies.driver.cabpooler;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rookies.driver.cabpooler.background.UserPollingService;
import com.rookies.driver.cabpooler.beans.DriverInfo;
import com.rookies.driver.cabpooler.beans.JourneyInfo;
import com.rookies.driver.cabpooler.beans.UserRideInfo;
import com.rookies.driver.cabpooler.util.CabPoolerHttpConnection;
import com.rookies.driver.cabpooler.util.CabPoolerJSONParser;
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

    GoogleMap mMap;
    private LatLng source ;
    private LatLng destination;
    private ProgressBar spinner;
    private DriverInfo dInfo = null;
    private String rideNumber;
    private JourneyInfo jInfo=null;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location presentLocation;
    private MarkerOptions myLocationMarker;
    private Marker cabMarker;
    private Context mContext;
    private UserRideInfo rideInfo = null;
    private PollingService receiver;
    private TextView req;
    private Button yes;
    private Button no;
    private String searchRide = "Yes";
    private boolean pollForRide = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;
        Intent intent = getIntent();
        dInfo = (DriverInfo)intent.getSerializableExtra("DriverInfo");
        Toast.makeText(getApplicationContext(), "Hi "+dInfo.getName()+" !!!", Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavigationDrawerFragment drawerFragment;
        drawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,(DrawerLayout)findViewById(R.id.drawer_layout),toolbar,dInfo);


        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.homeMapView)).getMap();
        mMap.clear();
        mMap.getUiSettings().setZoomControlsEnabled(true);

        GPStracker tracker = new GPStracker(this);

        LinearLayout layout = (LinearLayout)findViewById(R.id.pass);
        layout.setVisibility(LinearLayout.INVISIBLE);
        presentLocation = tracker.getLocation();
            if(tracker.canGetLocation()==true)
            {
                myLocationMarker = new MarkerOptions().position(new LatLng(presentLocation.getLatitude(),presentLocation.getLongitude())).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.cab));
                //.
                //myLocationMarker = new MarkerOptions().position(new LatLng(37.340061, -121.894219)).draggable(true);
                cabMarker= mMap.addMarker(myLocationMarker);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(presentLocation.getLatitude(),presentLocation.getLongitude()),10);
                //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(37.340061, -121.894219),10);
                mMap.animateCamera(cameraUpdate);
                new  UpdateLocation().execute(presentLocation.getLatitude()+"",presentLocation.getLongitude()+"");
            }
            else
            {
                tracker.showSettingsAlert();
            }

         req = (TextView) findViewById(R.id.req);
         req.setVisibility(TextView.INVISIBLE);
         yes = (Button) findViewById(R.id.yes);
         yes.setVisibility(Button.INVISIBLE);
         no = (Button) findViewById(R.id.no);
         no.setVisibility(Button.INVISIBLE);


        yes.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Directions directions = new Directions();
               req.setVisibility(TextView.INVISIBLE);
               yes.setVisibility(Button.INVISIBLE);
               no.setVisibility(Button.INVISIBLE);
               directions.execute(rideNumber,"3");
                ((Button)findViewById(R.id.GO)).setVisibility(Button.VISIBLE);
                searchRide = "No";

            }
        });

        no.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchRide = "Yes";
                        Directions directions = new Directions();
                        req.setVisibility(TextView.INVISIBLE);
                        yes.setVisibility(Button.INVISIBLE);
                        no.setVisibility(Button.INVISIBLE);
                        directions.execute(rideNumber,"0");
                        pollForRide = true;
                        Intent pollingIntent = new Intent(MainActivity.this,UserPollingService.class);
                        pollingIntent.putExtra("searchRide",searchRide);
                        startService(pollingIntent);


                    }
                });



        final Button rideStatus = (Button) findViewById(R.id.GO);
        rideStatus.setVisibility(Button.INVISIBLE);
        rideStatus.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String jInfo = rideStatus.getText().toString();
                if(jInfo.equalsIgnoreCase("Reached Pickup")) {
                    new RideStatus().execute(rideNumber, "Cab Reached Pickup Point");
                    rideStatus.setText("Start Ride");
                }
                else if(jInfo.equalsIgnoreCase("Start Ride")) {
                    new RideStatus().execute(rideNumber, "On your way to Destination");
                    rideStatus.setText("End Ride");
                }
                else if(jInfo.equalsIgnoreCase("End Ride")) {
                    LinearLayout layout = (LinearLayout)findViewById(R.id.pass);
                    layout.setVisibility(LinearLayout.INVISIBLE);
                    rideStatus.setText("Reached Pickup");
                    new RideStatus().execute(rideNumber, "Reached Destination");
                    rideStatus.setVisibility(Button.INVISIBLE);
                    searchRide = "Yes";
                    pollForRide = true;
                    Intent pollingIntent = new Intent(MainActivity.this,UserPollingService.class);
                    pollingIntent.putExtra("searchRide",searchRide);
                    mMap.clear();
                    cabMarker =  mMap.addMarker(new MarkerOptions().position(new LatLng(presentLocation.getLatitude(),presentLocation.getLongitude())).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.cab)));
                    startService(pollingIntent);


                }
            }
        });


        IntentFilter filter = new IntentFilter(PollingService.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new PollingService();
        registerReceiver(receiver, filter);


        Intent pollingIntent = new Intent(MainActivity.this,UserPollingService.class);
        pollingIntent.putExtra("searchRide",searchRide);
        startService(pollingIntent);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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






    private class UpdateLocation extends AsyncTask<String,String,String>
    {

        @Override
        protected String doInBackground(String... params) {

           CabPoolerHttpConnection connection = new CabPoolerHttpConnection();
            connection.updateCabLocation(dInfo.getDriverNumber(),params[0],params[1]);

            return null;
        }


    }

    private class RideStatus extends AsyncTask<String,String,String>
    {
        @Override
        protected String doInBackground(String... params) {
           CabPoolerHttpConnection connection = new CabPoolerHttpConnection();
            connection.updateJourneyStatus(params[0],params[1]);
            return null;
        }

    }



    private class Directions extends AsyncTask<String,String,String>
    {
        String data = null;

        @Override
        protected String doInBackground(String... params){

            String rideNumber = params[0];
            String rideStatus = params[1];
            try {
                CabPoolerHttpConnection connection = new CabPoolerHttpConnection();
                jInfo = connection.getRideInfo(rideNumber,dInfo.getDriverNumber(),rideStatus);
                if(null!=jInfo){
                    Log.d("Source INfo", jInfo.getLatitude_source() + "asasas" + jInfo.getLongitude_source());
                    String waypoints = "&waypoints=" + presentLocation.getLatitude() + "," + presentLocation.getLongitude()+ "|" + jInfo.getLatitude_source() + "," + jInfo.getLongitude_source() + "|" + jInfo.getLatitude_destination() + "," + jInfo.getLongitude_destination();
                   // String waypoints = "&waypoints=" + 37.340061 + "," + "-121.894219" + "|" + jInfo.getLatitude_source() + "," + jInfo.getLongitude_source() + "|" + jInfo.getLatitude_destination() + "," + jInfo.getLongitude_destination();
                    String URL = "https://maps.googleapis.com/maps/api/directions/json?origin=" + presentLocation.getLatitude() + "," + presentLocation.getLongitude() + "&destination=" + jInfo.getLatitude_destination() + "," + jInfo.getLongitude_destination() + waypoints + "&sensor=false";
                    //  String URL = "https://maps.googleapis.com/maps/api/directions/json?origin=" + 37.340061 + "," + "-121.894219" + "&destination=" + jInfo.getLatitude_destination() + "," + jInfo.getLongitude_destination() + waypoints + "&sensor=false";
                    data = connection.getData(URL);

                    Log.d("JSON INfo", data);
                }
            }
            catch (Exception e)
            {
                Log.d("Exception in Directions", e.getStackTrace().toString());
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(null!=jInfo) {
                LinearLayout layout = (LinearLayout)findViewById(R.id.pass);
                layout.setVisibility(LinearLayout.VISIBLE);
                mMap.addMarker(new MarkerOptions().position(new LatLng(jInfo.getLatitude_source(), jInfo.getLongitude_source())).icon(BitmapDescriptorFactory.fromResource(R.drawable.destination)).title("Source"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(jInfo.getLatitude_destination(), jInfo.getLongitude_destination())).icon(BitmapDescriptorFactory.fromResource(R.drawable.source1)).title("Destination"));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(jInfo.getLatitude_source(), jInfo.getLongitude_source()), 15);
                mMap.animateCamera(cameraUpdate);
                ParserTask decode = new ParserTask();
                decode.execute(data);
            }
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

            mMap.addPolyline(polyLineOptions);
            new  RideStatus().execute(rideNumber,"Cab is on the way for pickup!!");

        }
    }

    public class PollingService extends BroadcastReceiver{

        public static final String PROCESS_RESPONSE = "com.example.kishanchaitanya.intent.action.PROCESS_RESPONSE";
        @Override
        public void onReceive(Context context, Intent intent) {

            rideNumber = intent.getStringExtra("rideNumber");

            if(rideNumber!=null)
            {
                Log.d("rideNumber",rideNumber);
                req.setVisibility(TextView.VISIBLE);
                yes.setVisibility(Button.VISIBLE);
                no.setVisibility(Button.VISIBLE);
                searchRide = "No";
                pollForRide = false;
            }
            else {
                Intent pollingIntent = new Intent(MainActivity.this, UserPollingService.class);
                pollingIntent.putExtra("searchRide", searchRide);
                startService(pollingIntent);
            }
        }
    }


    public class GPStracker extends Service implements LocationListener {

        private final Context mContext;

        // flag for GPS status
        boolean isGPSEnabled = false;

        // flag for network status
        boolean isNetworkEnabled = false;

        // flag for GPS status
        boolean canGetLocation = false;

        Location location = null; // location
        double latitude; // latitude
        double longitude; // longitude

        // The minimum distance to change Updates in meters
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

        // The minimum time between updates in milliseconds
        private static final long MIN_TIME_BW_UPDATES = 5000; // 1 minute

        // Declaring a Location Manager
        protected LocationManager locationManager;

        public GPStracker(Context context) {
            this.mContext = context;
            getLocation();
        }

        public Location getLocation() {
            try {
                locationManager = (LocationManager) mContext
                        .getSystemService(LOCATION_SERVICE);

                // getting GPS status
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    Log.d("GPS Disabled", "GPS Disabled");
                    // no network provider is enabled
                } else {
                    this.canGetLocation = true;
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }

            return location;
        }

        /**
         * Stop using GPS listener Calling this function will stop using GPS in your
         * app
         * */
        public void stopUsingGPS() {
            if (locationManager != null) {
                locationManager.removeUpdates(GPStracker.this);
            }
        }

        /**
         * Function to get latitude
         * */
        public double getLatitude() {
            if (location != null) {
                latitude = location.getLatitude();
            }

            // return latitude
            return latitude;
        }

        /**
         * Function to get longitude
         * */
        public double getLongitude() {
            if (location != null) {
                longitude = location.getLongitude();
            }

            // return longitude
            return longitude;
        }

        /**
         * Function to check GPS/wifi enabled
         *
         * @return boolean
         * */
        public boolean canGetLocation() {
            return this.canGetLocation;
        }

        /**
         * Function to show settings alert dialog On pressing Settings button will
         * lauch Settings Options
         * */
        public void showSettingsAlert() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog
                    .setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            mContext.startActivity(intent);
                        }
                    });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            // Showing Alert Message
            alertDialog.show();
        }

        @Override
        public void onLocationChanged(Location location) {

            myLocationMarker = new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.cab));;
            cabMarker.remove();
            cabMarker = mMap.addMarker(myLocationMarker);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),15);
            //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(37.340061, -121.894219),10);
            mMap.animateCamera(cameraUpdate);


            new UpdateLocation().execute(location.getLatitude()+"",location.getLongitude()+"");

        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }
    }



}


