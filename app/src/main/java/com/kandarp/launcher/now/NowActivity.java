package com.kandarp.launcher.now;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.SearchView;

import com.kandarp.launcher.now.geofence_reminders.GeofenceActivity;
import com.kandarp.launcher.now.movies.app.MovieYoutubeActivity;
import com.kandarp.launcher.now.search.SearchableActivity;
import com.kandarp.launcher.now.stocks.StocksActivity;
import com.kandarp.launcher.now.traffic.DirectionsActivity;
import com.kandarp.launcher.now.weather.MainActivity;

/**
 * Created by Kandarp on 2/1/2015.
 */
public class NowActivity extends Activity {

    CardView weather, stocks, places, movies, location, geofence, directions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.now_activity);
        try {
            getActionBar().setDisplayShowTitleEnabled(false);
        } 
        	catch(Exception k)
        	{
        		k.printStackTrace();
        	}
        weather = (CardView) findViewById(R.id.weather);
        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent weather = new Intent(NowActivity.this, MainActivity.class);
                startActivity(weather);
            }
        });

        stocks = (CardView) findViewById(R.id.stocks);
        stocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stocks = new Intent(NowActivity.this, StocksActivity.class);
                startActivity(stocks);
            }
        });

        geofence = (CardView) findViewById(R.id.geofence);
        geofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent geofence = new Intent(NowActivity.this, GeofenceActivity.class);
                startActivity(geofence);
            }
        });

        directions = (CardView) findViewById(R.id.directions);
        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent directions = new Intent(NowActivity.this, DirectionsActivity.class);
                startActivity(directions);
            }
        });

        movies = (CardView) findViewById(R.id.movies);
        movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent movies = new Intent(NowActivity.this, MovieYoutubeActivity.class);
                startActivity(movies);
            }
        });


        places = (CardView) findViewById(R.id.places);
        places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent places = new Intent(NowActivity.this, com.kandarp.launcher.now.places.Menu.class);
                startActivity(places);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_now, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_web_now).getActionView();
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(getApplicationContext(), SearchableActivity.class)));
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);

        return true;
    }
}
