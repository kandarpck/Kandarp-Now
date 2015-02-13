package com.kandarp.launcher.now.traffic;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kandarp.launcher.now.R;

/**
 * Created by Kandarp on 2/13/2015.
 */
public class DirectionsActivity extends FragmentActivity {

    GoogleMap map;

    LatLng home = new LatLng(19.0852668, 72.9050546);
    LatLng work = new LatLng(19.047923, 72.890058);
    double home_lat = 19.0852668;
    double home_lon = 72.9050546;
    double work_lat = 19.047923;
    double work_lon = 72.890058;
    double distance;

    private ViewPager mPlaceViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);


        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        map = fm.getMap();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setRotateGesturesEnabled(true);
        map.setTrafficEnabled(true);

        map.addMarker(new MarkerOptions()
                .title("Home")
                .position(home));

        map.addMarker(new MarkerOptions()
                .title("Work/College")
                .position(work));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 13));

        Navigator nav = new Navigator(map, home, work);
        nav.findDirections(true);

        Haversine haversine = new Haversine();
        distance = haversine.haversine(home_lat, home_lon, work_lat, work_lon);

        // Setup the place view pager.
        mPlaceViewPager = (ViewPager) findViewById(R.id.pager);
        mPlaceViewPager.setAdapter(new PlacePagerAdapter());


    }

    /**
     * PagerAdapter that creates Views with place info for the ViewPager.
     */
    private class PlacePagerAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(final View collection, final int position) {
            LayoutInflater inflater = (LayoutInflater) collection.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater
                    .inflate(R.layout.map_view_pager, null, false);

            // If the view is clicked, zoom in on its corresponding marker.
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                }

            });

            ((TextView) view.findViewById(R.id.place_name)).setText("Traffic Information");
            ((TextView) view.findViewById(R.id.title_name))
                    .setText("Distance: " + distance);
            ((TextView) view.findViewById(R.id.title_count))
                    .setText("Time to Destination: ");

            ((ViewPager) collection).addView(view, 0);

            return view;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }

}