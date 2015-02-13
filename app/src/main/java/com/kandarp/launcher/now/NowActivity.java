package com.kandarp.launcher.now;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.kandarp.launcher.now.geofence_reminders.GeofenceActivity;
import com.kandarp.launcher.now.stocks.StocksActivity;
import com.kandarp.launcher.now.weather.MainActivity;

/**
 * Created by Kandarp on 2/1/2015.
 */
public class NowActivity extends Activity {

    Button weather, stocks, places, movies, location, geofence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.now_activity);

        weather = (Button) findViewById(R.id.weather);
        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent weather = new Intent(NowActivity.this, MainActivity.class);
                startActivity(weather);
            }
        });

        stocks = (Button) findViewById(R.id.stocks);
        stocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stocks = new Intent(NowActivity.this, StocksActivity.class);
                startActivity(stocks);
            }
        });

        geofence = (Button) findViewById(R.id.geofence);
        geofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent geofence = new Intent(NowActivity.this, GeofenceActivity.class);
                startActivity(geofence);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
