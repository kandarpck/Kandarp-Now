package com.kandarp.launcher.now.places;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kandarp.launcher.now.R;

public class PlacesActivity extends Activity {
    private int userIcon, foodIcon, drinkIcon, shopIcon, otherIcon;
    private GoogleMap theMap;
    private LocationManager locMan;
    private Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_activity);

        userIcon = R.drawable.yellow_point;
        foodIcon = R.drawable.red_point;
        drinkIcon = R.drawable.blue_point;
        shopIcon = R.drawable.green_point;
        otherIcon = R.drawable.purple_point;

        theMap = ((MapFragment) getFragmentManager().findFragmentById(
                R.id.map)).getMap();

        if (theMap != null) {
            theMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            updatePlaces();
        }

    }

    private void updatePlaces() {

        locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location lastLoc = locMan
                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double lat = lastLoc.getLatitude();
        double lng = lastLoc.getLongitude();
        LatLng lastLatLng = new LatLng(lat, lng);


        theMap.setTrafficEnabled(true);
        if (marker != null)
            marker.remove();
        marker = theMap.addMarker(new MarkerOptions().position(lastLatLng)
                .title("You are here")
                .icon(BitmapDescriptorFactory.fromResource(userIcon))
                .snippet("Your last recorded location"));
        CameraPosition cp = new CameraPosition(lastLatLng, 17, 30, 70);
        theMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
        // theMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 3000,
        // null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


}

				
			
		

	


