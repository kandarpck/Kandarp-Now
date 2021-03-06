package com.kandarp.launcher.now.places;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kandarp.launcher.now.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/*
 * MyMapActivity class forms part of Map application
 * in Mobiletuts+ tutorial series:
 * Using Google Maps and Google Places in Android Apps
 * 
 * This version of the class is for the final part of the series.
 * 
 * Sue Smith
 * March/ April 2013
 */
public class ATM extends Activity {

    //max
    private final int MAX_PLACES = 20;//most returned from google
    //instance variables for Marker icon drawable resources
    private int userIcon, foodIcon, drinkIcon, shopIcon, otherIcon;
    //the map
    private GoogleMap theMap;
    //location manager
    private LocationManager locMan;
    //user marker
    private Marker userMarker;
    //places of interest
    private Marker[] placeMarkers;
    //marker options
    private MarkerOptions[] places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_activity);

        //get drawable IDs
        userIcon = R.drawable.yellow_point;
        foodIcon = R.drawable.red_point;
        drinkIcon = R.drawable.blue_point;
        shopIcon = R.drawable.blue_point;
        otherIcon = R.drawable.purple_point;


        //find out if we already have it
        if (theMap == null) {
            //get the map
            theMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            theMap.setMyLocationEnabled(true);
            theMap.getUiSettings().setZoomGesturesEnabled(true);
            theMap.getUiSettings().setCompassEnabled(true);
            theMap.getUiSettings().setMyLocationButtonEnabled(true);
            theMap.getUiSettings().setRotateGesturesEnabled(true);
            theMap.setTrafficEnabled(true);
            //check in case map/ Google Play services not available
            if (theMap != null) {
                //ok - proceed
                //create marker array
                placeMarkers = new Marker[MAX_PLACES];
                //update location
                updatePlaces();
            }

        }
    }

    //location listener functions


    /*
     * update the place markers
     */
    private void updatePlaces() {
        //get location manager
        locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //get last location
        Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double lat = lastLoc.getLatitude();
        double lng = lastLoc.getLongitude();
        //create LatLng
        LatLng lastLatLng = new LatLng(lat, lng);

        //remove any existing marker
        if (userMarker != null) userMarker.remove();
        //create and set marker properties
        userMarker = theMap.addMarker(new MarkerOptions()
                .position(lastLatLng)
                .title("You are here")
                .icon(BitmapDescriptorFactory.fromResource(userIcon))
                .snippet("Your last recorded location"));
        //move to location
        theMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 3000, null);
        theMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 15));


        //build places query string
        String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                "json?location=" + lat + "," + lng +
                "&radius=3000&sensor=true" +
                "&types=atm" +
                "&key=" + "AIzaSyBaTy6cQFNCFNwqN_KCWF8WoKokFSMBngA";//ADD KEY

        //execute query
        new GetPlaces().execute(placesSearchStr);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (theMap != null) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (theMap != null) {
            finish();
        }
    }

    private class GetPlaces extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... placesURL) {
            //fetch places

            //build result as string
            StringBuilder placesBuilder = new StringBuilder();
            //process search parameter string(s)
            for (String placeSearchURL : placesURL) {
                HttpClient placesClient = new DefaultHttpClient();
                try {
                    //try to fetch the data

                    //HTTP Get receives URL string
                    HttpGet placesGet = new HttpGet(placeSearchURL);
                    //execute GET with Client - return response
                    HttpResponse placesResponse = placesClient.execute(placesGet);
                    //check response status
                    StatusLine placeSearchStatus = placesResponse.getStatusLine();
                    //only carry on if response is OK
                    if (placeSearchStatus.getStatusCode() == 200) {
                        //get response entity
                        HttpEntity placesEntity = placesResponse.getEntity();
                        //get input stream setup
                        InputStream placesContent = placesEntity.getContent();
                        //create reader
                        InputStreamReader placesInput = new InputStreamReader(placesContent);
                        //use buffered reader to process
                        BufferedReader placesReader = new BufferedReader(placesInput);
                        //read a line at a time, append to string builder
                        String lineIn;
                        while ((lineIn = placesReader.readLine()) != null) {
                            placesBuilder.append(lineIn);
                            Log.v("MyMapActivity", lineIn);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return placesBuilder.toString();
        }

        //process data retrieved from doInBackground
        protected void onPostExecute(String result) {
            //parse place data returned from Google Places
            //remove existing markers
            if (placeMarkers != null) {
                for (int pm = 0; pm < placeMarkers.length; pm++) {
                    if (placeMarkers[pm] != null)
                        placeMarkers[pm].remove();
                }
            }
            try {
                //parse JSON

                //create JSONObject, pass stinrg returned from doInBackground
                JSONObject resultObject = new JSONObject(result);
                //get "results" array
                JSONArray placesArray = resultObject.getJSONArray("results");
                //marker options for each place returned
                places = new MarkerOptions[placesArray.length()];
                //loop through places
                for (int p = 0; p < placesArray.length(); p++) {
                    //parse each place
                    //if any values are missing we won't show the marker
                    boolean missingValue = false;
                    LatLng placeLL = null;
                    String placeName = "";
                    String vicinity = "";
                    int currIcon = otherIcon;
                    try {
                        //attempt to retrieve place data values
                        missingValue = false;
                        //get place at this index
                        JSONObject placeObject = placesArray.getJSONObject(p);
                        //get location section
                        JSONObject loc = placeObject.getJSONObject("geometry")
                                .getJSONObject("location");
                        //read lat lng
                        placeLL = new LatLng(Double.valueOf(loc.getString("lat")),
                                Double.valueOf(loc.getString("lng")));
                        //get types
                        JSONArray types = placeObject.getJSONArray("types");
                        //loop through types
                        //userIcon=drinkIcon;
                        //vicinity
                        vicinity = placeObject.getString("vicinity");
                        //name
                        placeName = placeObject.getString("name");
                        Log.v("MyMapActivity", "jnsd: " + placeName);
                    } catch (JSONException jse) {
                        Log.v("PLACES", "missing value");
                        missingValue = true;
                        jse.printStackTrace();
                    }
                    //if values missing we don't display
                    if (missingValue) places[p] = null;
                    else {
                        places[p] = new MarkerOptions()
                                .position(placeLL)
                                .title(placeName)
                                .snippet(vicinity);
                        Log.v("MyMapActivity", "marker created");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (places != null && placeMarkers != null) {
                for (int p = 0; p < places.length && p < placeMarkers.length; p++) {
                    //will be null if a value was missing
                    if (places[p] != null)
                        placeMarkers[p] = theMap.addMarker(places[p]);
                }
            }

        }
    }

}
